package au.org.ala.collectory

import grails.converters.JSON
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import java.text.MessageFormat
import groovyx.net.http.ContentType

/**
 * Services required to register and update organisation and datasets in GBIF.
 *
 * This is intended to be used only when the ALA is the publishing gateway to GBIF and not when the ALA installation is
 * sourcing it's data from GBIF.  The service was originally written for the needs of the UK ALA installation.
 */
class GbifRegistryService {

    def grailsApplication
    def isoCodeService

    // URL templates for the GBIF API relative to the base GBIF API url (e.g. https://api.gbif.org)
    static final String API_ORGANIZATION = "/v1/organization"
    static final String API_ORGANIZATION_DETAIL = "/v1/organization/{0}"
    static final String API_ORGANIZATION_CONTACT = "/v1/organization/{0}/contact"
    static final String API_ORGANIZATION_CONTACT_DETAIL = "/v1/organization/{0}/contact/{1}"

    static final String API_DATASET = "/v1/dataset"
    static final String API_DATASET_DETAIL = "/v1/dataset/{0}"
    static final String API_DATASET_ENDPOINT = "/v1/dataset/{0}/endpoint"
    static final String API_DATASET_ENDPOINT_DETAIL = "/v1/dataset/{0}/endpoint/{1}"


    /**
     * Updates the registration in GBIF for the DataProvider.
     * This updates the key metadata and the contacts which is typically all publishers provide to GBIF.
     */
    def updateRegistration(DataProvider dp) throws Exception {
        log.info("Updating GBIF organisation: ${dp.gbifRegistryKey}")

        // load the current GBIF entry to get the endorsing node key
        def organisation = loadOrganization(dp.gbifRegistryKey)

        // apply mutations
        populateOrganisation(organisation, dp)

        // update mutated version in GBIF
        def http = newHttpInstance();
        http.request (Method.PUT, ContentType.JSON) {
            uri.path = MessageFormat.format(API_ORGANIZATION_DETAIL, dp.gbifRegistryKey)
            body = (organisation as JSON).toString()
            response.success = { resp, reader ->
                log.info("Successfully updated provider in GBIF: ${dp.gbifRegistryKey}")
                syncContacts(dp)
                log.info("Successfully synced contacts: ${dp.gbifRegistryKey}")
                syncDataResources(dp)
                log.info("Successfully synced ${dp.resources.size()} resources in GBIF: ${dp.gbifRegistryKey}")
            }
        }
    }

    /**
     * Creates a new registration in GBIF for the DataProvider as a publishing organization, endorsed by the relevant
     * node.  Note: the GBIF Country to Attribute is used to instruct GBIF which country should be credited with
     * publishing the data.
     */
    def register(DataProvider dp) throws Exception {
        // create the entity with the mandatory fields in GBIF
        def organisation = [
                "endorsingNodeKey": grailsApplication.config.gbifEndorsingNodeKey,
                "endorsementApproved": true,
                "language": "eng" // required by GBIF
        ]
        populateOrganisation(organisation, dp)

        // create the organization and update the collectory DB
        def http = newHttpInstance();
        http.parser.'application/json' = http.parser.'text/plain'  // handle sloppy responses from GBIF
        http.request (Method.POST, ContentType.JSON) { req ->
            body = (organisation as JSON).toString()
            uri.path = API_ORGANIZATION
            response.success = { resp, reader ->
                dp.gbifRegistryKey = reader.text.replaceAll('"', "") // more sloppy GBIF responses
                log.info("Successfully created provider in GBIF: ${dp.gbifRegistryKey}")
                dp.save(flush: true)
                syncContacts(dp)
                log.info("Successfully created contacts: ${dp.gbifRegistryKey}")
                syncDataResources(dp)
                log.info("Successfully created ${dp.resources.size()} resources: ${dp.gbifRegistryKey}")
            }
        }
    }

  /**
   * Syncs the contacts with the GBIF registry.
   */
    private def syncContacts(DataProvider dp) {
        // load the current value from GBIF and remove the contacts
        def organisation = loadOrganization(dp.gbifRegistryKey)
        if (organisation.contacts) {
            log.info("Removing contacts")
            organisation.contacts.each {
                def http = newHttpInstance();
                http.parser.'application/json' = http.parser.'text/plain' // handle sloppy responses from GBIF
                http.request(Method.DELETE, ContentType.JSON) { req ->
                    uri.path = MessageFormat.format(API_ORGANIZATION_CONTACT_DETAIL, dp.gbifRegistryKey, it.key as String)
                    response.success = { resp, reader -> log.info("Removed contact ${it.key as String}") }
                }
            }
        }

        // now add the current ones from the collectory
        if (dp.contacts) {
            def http = newHttpInstance();
            http.parser.'application/json' = http.parser.'text/plain' // handle sloppy responses from GBIF

            dp.contacts.each {
                log.info("Adding contact ${it.contact}")
                def gbifContact = [
                        "firstName": it.contact.firstName,
                        "lastName": it.contact.lastName,
                        "type": "ADMINISTRATIVE_POINT_OF_CONTACT",
                        "email": [it.contact.email],
                        "phone": [it.contact.phone]
                ]

                http.request(Method.POST, ContentType.JSON) { req ->
                    uri.path = MessageFormat.format(API_ORGANIZATION_CONTACT, dp.gbifRegistryKey)
                    body = (gbifContact as JSON).toString()
                    response.success = { resp, reader -> log.info("Added contact ${reader}")}
                }
            }
        }
    }

  /**
   * This creates any missing data resources and updates endpoints for all datasets.
   * Deletions are not propogated at this point instead deferring to the current helpdesk@gbif.org process.
   */
    private def syncDataResources(DataProvider dp) {
        def resources = dp.getResources()
        resources.each {

            // register the missing datasets
            if (!it.gbifRegistryKey) {
                log.info("Creating resource for dr ${it.id}")
                def dataset = newDatasetInstance(it)
                log.info("Creating dataset in GBIF: ${dataset}")
                if (dataset) {
                    def http = newHttpInstance();
                    http.parser.'application/json' = http.parser.'text/plain' // handle sloppy responses from GBIF
                    http.request(Method.POST, ContentType.JSON) { req ->
                        uri.path = MessageFormat.format(API_DATASET, dp.gbifRegistryKey)
                        body = (dataset as JSON).toString()

                        // on success, save the key in GBIF
                        response.success = { resp, reader ->
                            it.gbifRegistryKey = reader.text.replaceAll('"', "") // more sloppy GBIF responses
                            log.info("Added dataset ${it.gbifRegistryKey}")
                            log.info("Successfully created dataset in GBIF: ${it.gbifRegistryKey}")
                            it.save(flush: true)
                        }
                    }

                }
            }
            syncEndpoints(it)
        }
    }

  /**
   * Checks that the GBIF registry holds the single endpoint for the data resource creating it or updating if required.
   */
    private def syncEndpoints(DataResource resource) {
        if (resource.gbifRegistryKey) {
            log.info("Syncing endpoints for resource[${resource.id}], gbifKey[${resource.gbifRegistryKey}]")

            def http = newHttpInstance()
            def dataset
            http.get(path: MessageFormat.format(API_DATASET_DETAIL, resource.gbifRegistryKey)) { resp, reader ->
                dataset = reader
            }

            if (dataset) {
                http.parser.'application/json' = http.parser.'text/plain' // handle sloppy responses from GBIF

                def dwcaUrl = grailsApplication.config.gbifExportUrlBase + "dr" + resource.getId() + ".dwca";

                if (dataset.endpoints && dataset.endpoints.size() == 1 && dwcaUrl.equals(dataset.endpoints.get(0).url)) {
                    log.info("Dataset[${resource.gbifRegistryKey}] has correct URL[${dwcaUrl}]")
                } else {

                    // delete the existing ones
                    if (dataset.endpoints) {
                        dataset.endpoints.each {
                            http.request(Method.DELETE, ContentType.JSON) { req ->
                                uri.path = MessageFormat.format(API_DATASET_ENDPOINT_DETAIL, resource.gbifRegistryKey, it.key as String)
                                response.success = { resp, reader -> log.info("Removed endpoint ${it.key as String}") }
                            }
                        }
                    }

                    // now add the correct one
                    def endpoint = [
                            "type": "DWC_ARCHIVE",
                            "url": dwcaUrl
                    ]
                    http.request(Method.POST, ContentType.JSON) { req ->
                        uri.path = MessageFormat.format(API_DATASET_ENDPOINT, resource.gbifRegistryKey)
                        body = (endpoint as JSON).toString()
                        response.success = { resp, reader ->
                            log.info("Created endpoint for Dataset[${resource.gbifRegistryKey}] with URL[${endpoint.url}]")
                        }
                    }
                }
            }
        }
    }

  /**
   * Creates the content for a dataset to POST to GBIF from the supplied resource or null if it can't be created.
   */
    private def newDatasetInstance(DataResource resource) {
        def license
        // map to GBIF, recognising GBIF are particular about the correct name
        switch (resource.licenseType) {
            case 'CC0':
                license = 'http://creativecommons.org/publicdomain/zero/1.0/legalcode'
                break
            case 'CC-BY':
                license = 'http://creativecommons.org/licenses/by/4.0/legalcode'
                break
            case 'CC-BY-NC':
                license = 'http://creativecommons.org/licenses/by-nc/4.0/legalcode'
                break
            case 'OGL':
                // See https://en.wikipedia.org/wiki/Open_Government_Licence
                // Note that publisher has explicitly confirmed a desire to register in GBIF, knowing that GBIF support
                // CC0, CC-BY and CC-BY-NC only.  This seems the most appropriate license to map to.
                license = 'http://creativecommons.org/licenses/by/4.0/legalcode'
                break
            default:
                log.info("Unsupported license ${resource.licenseType} for GBIF so cannot be registered")
                return null
        }

        def dataset = [
                "type": "OCCURRENCE",
                "license": license,
                "installationKey": grailsApplication.config.gbifInstallationKey,
                "publishingOrganizationKey": resource.dataProvider.gbifRegistryKey,
                "title": resource.name,
                "description": resource.pubDescription
        ]
        return dataset
    }

  /**
   * Loads an organization from the GBIF API.
   */
    private def loadOrganization(gbifRegistryKey) {
        def http = newHttpInstance()
        def organisation
        http.get(path: MessageFormat.format(API_ORGANIZATION_DETAIL, gbifRegistryKey)) { resp, reader ->
            organisation = reader
        }
        return organisation
    }

    /**
     * Takes the values from the DataProvider and populates them in the organisation object suitable for the GBIF API.
     */
    private def populateOrganisation(Object organisation, DataProvider dp) {
        organisation.title = dp.name
        // defensive coding follows to pass GBIF validation rules
        if (dp.acronym && dp.acronym.length()<=10) {
            organisation.abbreviation = dp.acronym
        }
        organisation.description = dp.pubDescription
        organisation.email = [dp.email]
        organisation.phone = [dp.phone]
        organisation.homepage = [dp.websiteUrl]
        organisation.latitude = dp.latitude
        organisation.longitude = dp.longitude
        organisation.logoUrl = dp.buildLogoUrl()

        // convert the 3 digit ISO code to the 2 digit ISO code GBIF needs
        // Note: GBIF use this for counting "data published by Country X".  There are cases where the postal Address
        // indicates the headquarters of an international organisation and the country it is located should not be
        // credited in GBIF as "owning the data".  For those cases, the country is left deliberately null.  This is a
        // GBIF specific requirement.
        organisation.country = null
        if (dp.gbifCountryToAttribute) {
            def iso2 = isoCodeService.iso3CountryCodeToIso2CountryCode(dp.gbifCountryToAttribute.toUpperCase())
            if (iso2) {
                log.info("Setting GBIF country of attribution to ${iso2}")
                organisation.country=iso2
            }
        }

        Address address = dp.getAddress()
        if (address) {
            organisation.province = address.state
            organisation.address = [address.street]
            organisation.city = address.city
            organisation.postalCode = address.postcode
        }
    }

    /**
     * Creates a new instance of an HTTP builder configured with the basic authentication account and standard
     * error handling.
     */
    private def newHttpInstance() {
        def http = new HTTPBuilder(grailsApplication.config.gbifApiUrl)

        // GBIF does not return the expected 401 challenge so this needs to be set preemptively
        // Note: Using Grails built in encoding which is a Java7-safe version
        def token = grailsApplication.config.gbifApiUser + ':' + grailsApplication.config.gbifApiPassword
        http.setHeaders([Authorization: "Basic ${token.bytes.encodeBase64().toString()}"])

        http.handler.'400' = { resp, reader -> throw new Exception("Bad request to GBIF: ${resp.status} ${reader}")}
        http.handler.'401' = { resp, reader -> throw new Exception("GBIF Authorisation required: ${resp.status}")}
        http.handler.'403' = { resp, reader -> throw new Exception("Not authorised to update GBIF: ${resp.status}")}
        http.handler.'422' = { resp, reader -> throw new Exception("Content fails GBIF validation: ${resp.status}")}
        http.handler.failure = { resp, reader ->
            throw new Exception("GBIF API error : ${resp} ${reader}")}

        return http
    }
}
