package au.org.ala.collectory

import au.com.bytecode.opencsv.CSVWriter
import grails.converters.JSON
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

import java.text.MessageFormat
import groovyx.net.http.ContentType

import java.util.concurrent.Executors

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

    static def pool = Executors.newFixedThreadPool(1) // very conservative

    Boolean isDryRun(){
        grailsApplication.config.gbifRegistrationDryRun.toBoolean()
    }

  /**
   * Updates all registrations of data providers and data resources with GBIF.  This will create missing datasets
   * in GBIF, update the organisation metadata in GBIF, and set DOIs in the datasets in the Collectory if configured
   * to do so (i.e. config useGbifDoi=true).
   */
    def updateAllRegistrations() {
        def providers = DataProvider.list()
        providers.each {
            def dp = it
            log.info("Scheduling update of registration of ${dp.uid}: ${dp.name} ${pool}")
            pool.submit(new Runnable() {
                public void run() {
                    updateRegistration(dp, true, true)
                }
            })
        }
    }

    /**
     * Updates the registration in GBIF for the DataProvider.
     * This updates the key metadata and the contacts which is typically all publishers provide to GBIF.
     */
    def updateRegistration(ProviderGroup dp, Boolean syncContacts, Boolean syncDataResources) throws Exception {
        if (dp.gbifRegistryKey) {
            boolean success = updateRegistrationMetadata(dp)
            if(success){
                log.info("Successfully updated provider in GBIF: ${dp.gbifRegistryKey}")
                if(syncContacts){
                    syncContactsForProviderGroup(dp)
                }
                log.info("Successfully synced contacts: ${dp.gbifRegistryKey}")
                if(syncDataResources) {
                    syncDataResourcesForProviderGroup(dp)
                    log.info("Successfully synced resources in GBIF: ${dp.gbifRegistryKey}")
                }
            }
        } else {
          log.info("No GBIF registration exists for dp[${dp.uid}] - nothing to update")
        }
    }

    /**
     * Update the registration metadata.
     *
     * @param dp
     * @return boolean indicating success
     */
    private boolean updateRegistrationMetadata(ProviderGroup dp) {
        log.info("Updating GBIF organisation ${dp.uid}: ${dp.gbifRegistryKey}")

        boolean success = false

        // load the current GBIF entry to get the endorsing node key
        def organisation = loadOrganization(dp.gbifRegistryKey)

        // apply mutations
        populateOrganisation(organisation, dp)

        if(!isDryRun()) {
            // update mutated version in GBIF
            def http = newHttpInstance();
            http.request(Method.PUT, ContentType.JSON) {
                uri.path = MessageFormat.format(API_ORGANIZATION_DETAIL, dp.gbifRegistryKey)
                body = (organisation as JSON).toString()
                response.success = { resp, reader ->
                    success = true
                }
            }
        }

        success
    }

    /**
     * Creates a new registration in GBIF for the DataProvider as a publishing organization, endorsed by the relevant
     * node.  Note: the GBIF Country to Attribute is used to instruct GBIF which country should be credited with
     * publishing the data.
     */
    def register(ProviderGroup dp, Boolean syncContacts, Boolean syncDataResources) throws Exception {
        // create the entity with the mandatory fields in GBIF
        def organisation = [
                "endorsingNodeKey": grailsApplication.config.gbifEndorsingNodeKey,
                "endorsementApproved": true,
                "language": "eng" // required by GBIF
        ]
        populateOrganisation(organisation, dp)

        // create the organization and update the collectory DB
        if(!isDryRun()) {
            def http = newHttpInstance();
            http.parser.'application/json' = http.parser.'text/plain'  // handle sloppy responses from GBIF
            http.request(Method.POST, ContentType.JSON) { req ->
                body = (organisation as JSON).toString()
                uri.path = API_ORGANIZATION
                response.success = { resp, reader ->
                    dp.gbifRegistryKey = reader.text.replaceAll('"', "") // more sloppy GBIF responses
                    log.info("Successfully created provider in GBIF: ${dp.gbifRegistryKey}")
                    dp.save(flush: true)

                    if (syncContacts) {
                        log.info("Attempting to sync contacts: ${dp.gbifRegistryKey}")
                        syncContactsForProviderGroup(dp)
                        log.info("Successfully created contacts: ${dp.gbifRegistryKey}")
                    }

                    if (syncDataResources) {
                        log.info("Attempting to sync resources: ${dp.gbifRegistryKey}")
                        syncDataResourcesForProviderGroup(dp)
                        log.info("Successfully created resources: ${dp.gbifRegistryKey}")
                    }
                }
            }
        } else {
            log.info ("[DRY RUN] Organisation to register: ${organisation}")
        }
    }

    /**
     * Favours institutions, and then looks for a data provider link.
     *
     * @param dataResource
     * @return
     */
    def registerDataResource(DataResource dataResource){

        def result = [success:false, message:""]

        def publisherGbifRegistryKey = "" //data provider or institution

        def institution = dataResource.institution
        def dataProvider = dataResource.dataProvider

        if(!institution) {

            //get the data provider if available...
            def dataLinks = DataLink.findAllByProvider(dataResource.uid)
            def institutionDataLink

            if (dataLinks) {
                //do we have institution link ????
                institutionDataLink = dataLinks.find { it.consumer.startsWith("in") }
                if (institutionDataLink) {
                    institution = Institution.findByUid(institutionDataLink.consumer)
                }
            }
        }

        if(institution) {
            // sync institution

            if(institution.gbifRegistryKey){
                updateRegistrationMetadata(institution)
            } else {
                register(institution, true, false)
            }

            publisherGbifRegistryKey = institution.gbifRegistryKey

        } else if(dataProvider) {

            // sync institution
            if(dataProvider.gbifRegistryKey){
                updateRegistrationMetadata(dataProvider)
            } else {
                register(dataProvider, true, false)
            }

            publisherGbifRegistryKey = dataProvider.gbifRegistryKey

        } else if(grailsApplication.config.gbifOrphansPublisherID){
            log.info("Unable to sync resource: ${dataResource.uid} -  ${dataResource.name}. No publishing organisation associated.")
            publisherGbifRegistryKey = grailsApplication.config.gbifOrphansPublisherID
        } else {
            log.info("Unable to sync resource: ${dataResource.uid} -  ${dataResource.name}. No publishing organisation associated.")
            result.success = false
            result.message = "Unable to sync resource: ${dataResource.uid} -  ${dataResource.name}. No publishing organisation associated."
        }

        //if no institution, get the data provider and create in GBIF
        if(publisherGbifRegistryKey) {
            //create the resource in GBIF
            log.info("Syncing data resource ${dataResource.uid} -  ${dataResource.name}")
            syncDataResource(dataResource, publisherGbifRegistryKey)
            log.info("Sync complete for data resource ${dataResource.uid} -  ${dataResource.name}")
            result.success = true
            result.message = "Data resource sync-ed with GBIF."
        }

        result
    }

    /**
     * Syncs the contacts with the GBIF registry.
     */
    private def syncContactsForProviderGroup(ProviderGroup dp) {
        // load the current value from GBIF and remove the contacts
        def organisation = loadOrganization(dp.gbifRegistryKey)
        if (organisation.contacts) {
            log.info("Removing contacts")
            organisation.contacts.each {
                if(!isDryRun()) {
                    def http = newHttpInstance();
                    http.parser.'application/json' = http.parser.'text/plain' // handle sloppy responses from GBIF
                    http.request(Method.DELETE, ContentType.JSON) { req ->
                        uri.path = MessageFormat.format(API_ORGANIZATION_CONTACT_DETAIL, dp.gbifRegistryKey, it.key as String)
                        response.success = { resp, reader -> log.info("Removed contact ${it.key as String}") }
                    }
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

                if(!isDryRun()) {
                    http.request(Method.POST, ContentType.JSON) { req ->
                        uri.path = MessageFormat.format(API_ORGANIZATION_CONTACT, dp.gbifRegistryKey)
                        body = (gbifContact as JSON).toString()
                        response.success = { resp, reader -> log.info("Added contact ${reader}") }
                    }
                }
            }
        }
    }

  /**
   * This creates any missing data resources and updates endpoints for all datasets.
   * Deletions are not propogated at this point instead deferring to the current helpdesk@gbif.org process.
   */
    private def syncDataResourcesForProviderGroup(ProviderGroup dp) {

        if(dp.gbifRegistryKey) {
            if (dp instanceof DataProvider) {
                def resources = dp.getResources()
                resources.each { resource ->

                    def skipSync = false
                    // if theres an institution link
                    if(resource.institution){
                       //dont sync
                        log.warn("${resource.uid} is sourced from an institution [${resource.institution.uid}]... not syncing  ")
                        skipSync = true
                    }

                    def dataLinks = DataLink.findAllByProvider(dp.uid)
                    dataLinks.each { dataLink ->
                        if(dataLink.consumer.startsWith("in")){
                            skipSync = true
                            log.warn("${resource.uid} is linked to an institution [${dataLink.consumer}]... not syncing  ")
                        }
                    }

                    if(!skipSync) {
                        syncDataResource(resource, dp.gbifRegistryKey)
                    }
                }
            } else {
                log.warn("Need to add syncing of resources for institution....via datalinks...")
                def dataLinks = DataLink.findAllByConsumer(dp.uid)
                if (dataLinks) {
                    dataLinks.each { dataLink ->
                        DataResource dr = DataResource.findByUid(dataLink.provider)
                        if (dr) {
                            syncDataResource(dr, dp.gbifRegistryKey)
                        }
                    }
                }
            }
        } else {
            log.warn("Not syncing resources for ${dp}. Not registered with GBIF....")
        }
    }

    /**
     * Sync the data resource with the provided ProviderGroup instance.
     *
     * @param dataResource the resource to sync.
     * @param organisationRegistryKey the gbif key for the publishing institution or a data provider.
     * @return
     */
    def syncDataResource(DataResource dataResource, String organisationRegistryKey){
        // register the missing datasets
        if (!dataResource.gbifRegistryKey) {
            log.info("Creating GBIF resource for ${dataResource.uid}")
            def dataset = newGBIFDatasetInstance(dataResource, organisationRegistryKey)
            log.info("Creating dataset in GBIF: ${dataset}")

            if (dataset) {
                if (!isDryRun()) {
                    def http = newHttpInstance();
                    http.parser.'application/json' = http.parser.'text/plain' // handle sloppy responses from GBIF
                    http.request(Method.POST, ContentType.JSON) { req ->
                        uri.path = MessageFormat.format(API_DATASET, organisationRegistryKey)
                        body = (dataset as JSON).toString()

                        // on success, save the key in GBIF
                        response.success = { resp, reader ->
                            dataResource.gbifRegistryKey = reader.text.replaceAll('"', "") // more sloppy GBIF responses
                            log.info("Added dataset ${dataResource.gbifRegistryKey}")
                            log.info("Successfully created dataset in GBIF: ${dataResource.gbifRegistryKey}")
                            dataResource.save(flush: true)
                        }
                    }

                    if (Boolean.valueOf(grailsApplication.config.useGbifDoi)) {
                        def created = loadDataset(dataResource.gbifRegistryKey)
                        dataResource.gbifDoi = created.doi
                        dataResource.save(flush: true)
                    }
                } else {
                    log.info("[isDryRun()] Dataset to register ${dataset}")
                }
            } else {
                log.warn("Unable to register dataset - please check license: ${dataResource.uid} :  ${dataResource.name} :  ${dataResource.licenseType}")
            }
        } else {
            // ensure the organisation is correct in GBIF as ownership varies over time, and that the DOI
            // is used if configured
            def dataset = loadDataset(dataResource.gbifRegistryKey)
            if(!isDryRun()) {
                if (Boolean.valueOf(grailsApplication.config.useGbifDoi) && dataResource.gbifDoi != dataset.doi) {
                    log.info("Setting resource[${dataResource.uid}] to use gbifDOI[${dataset.doi}]")
                    dataResource.gbifDoi = dataset.doi
                    dataResource.save(flush: true)
                }

                log.info("Updating the GBIF registry dataset[${dataResource.gbifRegistryKey}] to point to " +
                        "organisation[${organisationRegistryKey}]")
                dataset.publishingOrganizationKey = organisationRegistryKey
                dataset.deleted = null
                dataset.license = getGBIFCompatibleLicence(dataResource.licenseType)
                if(dataset.license) {
                    def http = newHttpInstance();
                    def datasetKey = dataResource.gbifRegistryKey
                    http.request(Method.PUT, ContentType.JSON) {
                        uri.path = MessageFormat.format(API_DATASET_DETAIL, datasetKey)
                        body = (dataset as JSON).toString()
                        response.success = { resp, reader ->
                            log.info("Successfully updated dataset in GBIF: ${datasetKey}")
                        }
                    }
                } else {
                    log.warn("Unable to update dataset - please check license: ${dataResource.uid} :  ${dataResource.name} :  ${dataResource.licenseType}")
                }
            } else {
                log.info("[DRYRUN] Updating data resource ${dataset}")
            }
        }
        syncEndpoints(dataResource)
    }

    def deleteDataResource(DataResource resource){

        def http = newHttpInstance()
        if(!isDryRun()) {
            http.request(Method.DELETE, ContentType.JSON) { req ->
                uri.path = MessageFormat.format(API_DATASET_DETAIL, resource.gbifRegistryKey)
                response.success = { resp, reader ->
                    log.info("Deleted  Dataset[${resource.gbifRegistryKey}] from GBIF")
                    resource.gbifRegistryKey = null
                    resource.save(flush:true)
                }
                response.failure = { resp ->
                    log.info("The delete of ${resource.uid} from GBIF was unsuccessful: ${resp.status}")
                }
            }
        } else {
            log.info("[DryRun] Deleting ${resource.uid}")
        }
        resource
    }

    /**
     * Checks that the GBIF registry holds the single endpoint for the data resource creating it or updating if required.
     */
    private def syncEndpoints(DataResource resource) {
        if (resource.gbifRegistryKey) {
            log.info("Syncing endpoints for resource[${resource.id}], gbifKey[${resource.gbifRegistryKey}]")

            def http = newHttpInstance()
            def dataset = loadDataset(resource.gbifRegistryKey)
            if (dataset) {

                if(!isDryRun()) {

                    http.parser.'application/json' = http.parser.'text/plain' // handle sloppy responses from GBIF

                    def dwcaUrl = grailsApplication.config.resource.gbifExport.url.template.replaceAll("@UID@", resource.getUid());

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
                                "url" : dwcaUrl
                        ]
                        http.request(Method.POST, ContentType.JSON) { req ->
                            uri.path = MessageFormat.format(API_DATASET_ENDPOINT, resource.gbifRegistryKey)
                            body = (endpoint as JSON).toString()
                            response.success = { resp, reader ->
                                log.info("Created endpoint for Dataset[${resource.gbifRegistryKey}] with URL[${endpoint.url}]")
                            }
                        }
                    }
                } else {
                    log.info("[DRYRUN] syncing endpoints with registry key ${resource.gbifRegistryKey}, for resource ${resource.uid}")
                }
            } else {
                log.info("Unable to load dataset info from GBIF with registry key ${resource.gbifRegistryKey}, for resource ${resource.uid}. Not syncing.....")
            }
        } else {
            log.info("Registry key not set for resource: ${resource.uid}. Not syncing.....")
        }
    }

    def getGBIFCompatibleLicence(String licenseType){

        if(grailsApplication.config.gbifLicenceMappingUrl && grailsApplication.config.gbifLicenceMappingUrl != 'null'){
            def jsonLicense = new JsonSlurper().parse(new URL(grailsApplication.config.gbifLicenceMappingUrl))
           return jsonLicense.get(licenseType)
        } else {

            // map to GBIF, recognising GBIF are particular about the correct name
            switch (licenseType) {
                case 'CC0':
                    return 'https://creativecommons.org/publicdomain/zero/1.0/legalcode'
                case 'CC-BY':
                    return 'https://creativecommons.org/licenses/by/4.0/legalcode'
                case 'CC-BY-NC':
                    return 'https://creativecommons.org/licenses/by-nc/4.0/legalcode'
                case 'OGL':
                    // See https://en.wikipedia.org/wiki/Open_Government_Licence
                    // Note that publisher has explicitly confirmed a desire to register in GBIF, knowing that GBIF support
                    // CC0, CC-BY and CC-BY-NC only.  This seems the most appropriate license to map to.
                    return 'https://creativecommons.org/licenses/by/4.0/legalcode'
                default:
                    log.info("Unsupported license ${licenseType} for GBIF so cannot be registered")
                    return null
            }
        }
    }


    /**
     * Creates the content for a dataset to POST to GBIF from the supplied resource or null if it can't be created.
     */
    private def newGBIFDatasetInstance(DataResource resource, String organisationRegistryKey) {
        def license = getGBIFCompatibleLicence(resource.licenseType)

        if(!license){
            return null
        }

        def dataset = [
                "type": "OCCURRENCE",
                "license": license,
                "installationKey": grailsApplication.config.gbifInstallationKey,
                "publishingOrganizationKey": organisationRegistryKey,
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
     * Loads a dataset from the GBIF API.
     */
    private def loadDataset(gbifRegistryKey) {
        def http = newHttpInstance()
        def dataset
        http.get(path: MessageFormat.format(API_DATASET_DETAIL, gbifRegistryKey)) { resp, reader ->
            dataset = reader
        }
        return dataset
    }

    /**
     * Takes the values from the DataProvider and populates them in the organisation object suitable for the GBIF API.
     */
    private def populateOrganisation(Object organisation, ProviderGroup dp) {
        organisation.title = dp.name
        // defensive coding follows to pass GBIF validation rules
        if (dp.acronym && dp.acronym.length()<=10) {
            organisation.abbreviation = dp.acronym
        }
        organisation.description = dp.pubDescription
        organisation.email = [dp.email]
        organisation.phone = [dp.phone]
        organisation.homepage = [dp.websiteUrl]
        organisation.latitude = Math.floor(dp.latitude as float) == -1.0 ? null : dp.latitude
        organisation.longitude = Math.floor(dp.longitude as float) == -1.0 ? null : dp.longitude
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
                organisation.country = iso2
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

    def writeCSVReportForGBIF(outputStream) {

        log.debug("Starting report.....")
        def url = grailsApplication.config.biocacheServicesUrl + "/occurrences/search?q=*:*&facets=data_resource_uid&pageSize=0&facet=on&flimit=-1"

        def js = new JsonSlurper()
        def biocacheSearch = js.parse(new URL(url), "UTF-8")

        def csvWriter = new CSVWriter(new OutputStreamWriter(outputStream))

        String[] header = [
                "UID",
                "Data resource",
                "Data resource GBIF ID",
                "Record count",

                "Data provider UID",
                "Data provider name",
                "Data provider GBIF ID",

                "Institution UID",
                "Institution name",
                "Institution GBIF ID",

                "Licence",

                "Shareable with GBIF",
                "Licence Issues (preventing sharing)",

                "Not Shareable (no owner)",
                "Flagged as Not-Shareable",
                "Provided by GBIF",

                "Linked to Data Provider",
                "Linked to Institution"
        ]

        csvWriter.writeNext(header)

        biocacheSearch.facetResults[0].fieldResult.each { result ->
            def uid = result.fq.replaceAll("\"","").replaceAll("data_resource_uid:","")

            //retrieve current licence
            def dataResource = DataResource.findByUid(uid)
            if(dataResource) {

                def isShareable = true
                def licenceIssues = false
                def flaggedAsNotShareable = false
                def providedByGBIF = false
                def notShareableNoOwner = false

                //retrieve current licence
                def dataProvider
                def institution

                //get the data provider if available...
                def dataLinks = DataLink.findAllByProvider(uid)
                def institutionDataLink

                if(dataLinks){
                    //do we have institution link ????
                    institutionDataLink = dataLinks.find { it.consumer.startsWith("in")}
                    if(institutionDataLink){
                        //we have an institution
                        institution = Institution.findByUid(institutionDataLink.consumer)
                    }
                }

                if(!institutionDataLink) {
                    dataProvider = dataResource.getDataProvider()
                    if(!dataProvider){
                        notShareableNoOwner = true
                        isShareable = false //no institution and no data provider
                    }
                }

                if (dataResource.licenseType == null || !getGBIFCompatibleLicence(dataResource.licenseType)) {
                    licenceIssues = true
                    isShareable = false
                }

                if (!dataResource.isShareableWithGBIF) {
                    flaggedAsNotShareable = true
                    isShareable = false
                }

                if (dataResource.gbifDataset) {
                    providedByGBIF = true
                    isShareable = false
                }

                String[] row = [
                        dataResource.uid,
                        dataResource.name,
                        dataResource.gbifRegistryKey,

                        result.count,

                        dataProvider?.uid,
                        dataProvider?.name,
                        dataProvider?.gbifRegistryKey,

                        institution?.uid,
                        institution?.name,
                        institution?.gbifRegistryKey,

                        dataResource.licenseType,

                        isShareable ? "yes" : "no",
                        licenceIssues ? "yes" : "no",
                        notShareableNoOwner ? "yes" : "no",
                        flaggedAsNotShareable ? "yes" : "no",
                        providedByGBIF ? "yes" : "no",

                        institution ? "yes" : "no",
                        dataProvider ? "yes" : "no"
                ]
                csvWriter.writeNext(row)
            }
        }
    }

    /**
     * Synchronise all resources with GBIF.
     *
     * @return a map of statistics showing number of updates.
     */
    def syncAllResources(){

        def results = generateSyncBreakdown()

        def resourcesRegistered= 0
        def resourcesUpdated = 0
        def dataProviderRegistered = 0
        def dataProviderUpdated = 0
        def institutionsRegistered = 0
        def institutionsUpdated = 0

        log.info("Attempting to sync ${results.shareable.size()} data resources.....")

        results.shareable.keySet().each { dataResource ->

            def publisherGbifRegistryKey = "" //data provider or institution

            //get the institution, and check it has been created in GBIF
            Institution institution = results.linkedToInstitution.get(dataResource)
            DataProvider dataProvider = results.linkedToDataProvider.get(dataResource)
            if(institution) {
                // sync institution
                if(institution.gbifRegistryKey){
                    updateRegistrationMetadata(institution)
                    institutionsUpdated ++
                } else {
                    register(institution, true, false)
                    institutionsRegistered ++
                }

                publisherGbifRegistryKey = institution.gbifRegistryKey

            } else if(dataProvider) {
                // sync institution
                if(dataProvider.gbifRegistryKey){
                    updateRegistrationMetadata(dataProvider)
                    dataProviderUpdated ++
                } else {
                    register(dataProvider, true, false)
                    dataProviderRegistered ++
                }
                publisherGbifRegistryKey = dataProvider.gbifRegistryKey
            } else if(grailsApplication.config.gbifOrphansPublisherID){
                publisherGbifRegistryKey = grailsApplication.config.gbifOrphansPublisherID
                log.info("Using orphans publisher ID  to sync resource: ${dataResource.uid}")
            } else {
                log.info("Unable to sync resource: ${dataResource.uid} -  ${dataResource.name}. No publishing organisation associated.")
            }

            //if no institution, get the data provider and create in GBIF
            if(publisherGbifRegistryKey) {
                //create the resource in GBIF
                log.info("Syncing data resource ${dataResource.uid} -  ${dataResource.name}")
                if(dataResource.gbifRegistryKey){
                    resourcesUpdated ++
                } else {
                    resourcesRegistered ++
                }

                try {
                    syncDataResource(dataResource, publisherGbifRegistryKey)
                    log.info("Sync complete for data resource ${dataResource.uid} -  ${dataResource.name}")
                } catch (Exception e){
                    log.error("Sync error for data resource ${dataResource.uid} -  ${dataResource.name} - " + e.getMessage(), e)
                }
            }
        }
        [
                resourcesRegistered : resourcesRegistered,
                resourcesUpdated : resourcesUpdated,
                dataProviderRegistered : dataProviderRegistered,
                dataProviderUpdated : dataProviderUpdated,
                institutionsRegistered : institutionsRegistered,
                institutionsUpdated : institutionsUpdated
        ]
    }

    /**
     * Retrieves a breakdown of data resources with available data.
     *
     * @return
     */
    def generateSyncBreakdown(){
        def url = grailsApplication.config.biocacheServicesUrl + "/occurrences/search?q=*:*&facets=data_resource_uid&pageSize=0&facet=on&flimit=-1"

        def js = new JsonSlurper()
        def biocacheSearch = js.parse(new URL(url), "UTF-8")

        def dataResourcesWithData = [:]
        def shareable = [:]
        def licenceIssues = [:]
        def notShareable = [:]
        def providedByGBIF = [:]
        def notShareableNoOwner = [:]
        def linkedToDataProvider = [:]
        def linkedToInstitution = [:]
        def recordsShareable = 0

        biocacheSearch.facetResults[0].fieldResult.each { result ->
            def uid = result.fq.replaceAll("\"","").replaceAll("data_resource_uid:","")

            def isShareable = true

            //retrieve current licence
            def dataResource = DataResource.findByUid(uid)
            if(dataResource) {

                dataResourcesWithData[dataResource] = result.count

                //find links to institutions
                def institution = dataResource.institution

                if(institution){
                    linkedToInstitution[dataResource] = dataResource.institution

                } else {

                    //get the data provider if available...
                    def dataLinks = DataLink.findAllByProvider(uid)
                    def institutionDataLink

                    if (dataLinks) {
                        //do we have institution link ????
                        institutionDataLink = dataLinks.find { it.consumer.startsWith("in") }
                        if (institutionDataLink) {

                            institution = Institution.findByUid(institutionDataLink.consumer)

                            //we have an institution
                            linkedToInstitution[dataResource] = institution
                        }
                    }
                }

                if(!institution) {
                    def dataProvider = dataResource.getDataProvider()
                    if(dataProvider){
                        linkedToDataProvider[dataResource] = dataProvider
                    } else {

                        // if there is not a orphans publisher ID configured, theres no home
                        if(!grailsApplication.config.gbifOrphansPublisherID) {
                            notShareableNoOwner[dataResource] = result.count
                            isShareable = false //no institution and no data provider
                        }
                    }
                }

                if (dataResource.licenseType == null || !getGBIFCompatibleLicence(dataResource.licenseType)) {
                    licenceIssues[dataResource] = result.count
                    isShareable = false
                }

                if (!dataResource.isShareableWithGBIF) {
                    notShareable[dataResource] = result.count
                    isShareable = false
                }

                if (dataResource.gbifDataset) {
                    providedByGBIF[dataResource] = result.count
                    isShareable = false
                }

                if (isShareable) {
                    shareable[dataResource] = result.count
                    recordsShareable += result.count
                }
            }
        }
        [
                indexedRecords : biocacheSearch.totalRecords,
                recordsShareable: recordsShareable,
                dataResourcesWithData:dataResourcesWithData,
                shareable:shareable,
                licenceIssues:licenceIssues,
                notShareable:notShareable,
                providedByGBIF:providedByGBIF,
                notShareableNoOwner:notShareableNoOwner,
                linkedToDataProvider: linkedToDataProvider,
                linkedToInstitution: linkedToInstitution,
        ]
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
