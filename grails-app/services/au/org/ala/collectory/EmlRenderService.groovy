/*
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 */

package au.org.ala.collectory

import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import java.text.SimpleDateFormat
import java.text.DateFormat

class EmlRenderService {

    static transactional = true
    def messageSource
    def grailsApplication
    def ns = [eml:"eml://ecoinformatics.org/eml-2.1.1",
            xsi:"http://www.w3.org/2001/XMLSchema-instance",
            dc:"http://purl.org/dc/terms/"]

    def emlNs = ['xsi:schemaLocation':"eml://ecoinformatics.org/eml-2.1.1 http://rs.gbif.org/schema/eml-gbif-profile/1.1/eml-gbif-profile.xsd",
            'xmlns:d':"eml://ecoinformatics.org/dataset-2.1.0",
            'system':"ALA-Registry",
            'scope':"system",
            'xml:lang':"en"]

    final static String DATE_PATTERN = "yyyy-MM-dd";
    final static String DATE_TIME_PATTERN = "yyyy-MM-dd'T'hh:mm:ss";

    /**
      * DateFormat to be used to format dates
      */
    final static DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN)
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    final static DateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_PATTERN)
    static {
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * General entry point for any entity.
     *
     * @param entity
     * @return eml for the entity
     */
    String emlForEntity(entity) {
        if (entity instanceof DataResource) {
            return emlForResource(entity)
        }
        else if (entity instanceof Collection) {
            return emlForCollection(entity)
        }
        else {
            return emlForOtherEntity(entity)
        }
    }

    /**
     * Binds the elements that are common to all entities
     *
     * <title/>
     * <creator/>
     * <metadataProvider/>
     * <associatedParty/>  (ALA)
     * <pubDate/>
     * <language/>
     * <abstract/>
     *
     * @param builder
     * @param pg the entity
     */
    def commonElements1(builder, ProviderGroup pg) {

        /* title */
        builder.title('xmlns:lang':'en', pg.name)

        /* creator */
        def crt = pg.createdBy()
        organisation(builder, 'creator', crt, null)

        /* metadata provider */
        // always the same as creator
        organisation(builder, 'metadataProvider', crt, null)

        /* associated parties */
        builder.associatedParty(ala(true))
        pg.listConsumers().each { con ->
            organisation(builder, 'associatedParty', ProviderGroup._get(con), 'originator')
        }
        pg.listProviders().each { pro ->
            organisation(builder, 'associatedParty', ProviderGroup._get(pro), 'publisher')
        }

        /* pub date */
        def lastPub = pg.lastUpdated
        if (lastPub) {
          lastPub = lastPub.toString()[0..9]
        }
        builder.pubDate lastPub

        /* language */
        builder.language "English"

        /* abstract */
        builder.'abstract'() {
            builder.para stripFormatting([pg.pubDescription, pg.techDescription])
        }
    }

    /**
     * Binds the additional metadata elements that are common to all entities
     *
     * <dateStamp/>
     * <metadataLanguage/>
     * <hierarchyLevel/>
     * <resourceLogoUrl/>
     *
     * @param builder
     * @param pg the entity
     */
    def commonElements2(builder, ProviderGroup pg) {

        /* dateStamp */
        builder.dateStamp dateTimeFormat.format(pg.lastUpdated)

        /* hierarchyLevel */
        builder.hierarchyLevel 'dataset'

        /* resourceLogoUrl */
        def logo = pg.buildLogoUrl()
        if (logo) {
            builder.resourceLogoUrl logo
        }
    }

    def organisation(builder, tag, ProviderGroup pg, role) {
        builder."${tag}"() {
            builder.organizationName(pg.name)
            def address = pg.resolveAddress()
            if (address && !address.isEmpty()) {
                out << addAddress(address)
            }
            out << addIf(pg.phone, 'phone' )
            out << addIf(pg.email, 'electronicMailAddress')
            out << addIf(pg.websiteUrl, 'onlineUrl')
            if (role) {
                builder.role role
            }
        }
    }

    /**
     * Binds the primary contact.
     *
     * @param builder
     * @param pg the entity
     */
    def contacts(builder, pg) {
        def cnt = pg.inheritPrimaryContact()
        if (cnt) {
            builder.contact {
                if (cnt.contact.firstName || cnt.contact.lastName) {
                    builder.individualName {
                        if(cnt.contact.firstName && cnt.contact.lastName){
                            builder.givenName(cnt.contact.firstName?:'')
                            builder.surName(cnt.contact.lastName?:'')
                        } else {
                            builder.surName(cnt.contact.firstName?:' ')
                        }
                    }
                }
                cnt.role ? builder.positionName(cnt.role) : ""
                cnt.contact.phone ? builder.phone(cnt.contact.phone) : ""
                cnt.contact.email ? builder.electronicMailAddress(cnt.contact.email) : ""
            }
        } else {
            // last resort
            builder.contact(ala(false))
        }

    }

    /**
     * Extracts identifiers. Uses LSID as primary if available. Builds packageId and namespace.
     *
     * @param pg
     * @return id, packageId, alt id, uuid, and eml namespace
     */
    def identifiers(pg) {
        def id = ""
        def altId = ""
        if (pg.guid?.startsWith('urn:lsid')) {
            id = pg.guid
            altId = grailsApplication.config.grails.serverURL + "/public/show/" + pg.uid
        }
        else {
            id = grailsApplication.config.grails.serverURL + "/public/show/" + pg.uid
        }
        def uuid = UUID.nameUUIDFromBytes(id as byte[]).toString()
        def packageId = uuid + "/v" + pg.version
        def ns = emlNs << [packageId: packageId]
        return [id:id, packageId: packageId, altId:altId, uuid: uuid, ns: ns]
    }

    /**
     * Generates EML representation of the collection.
     *
     * @param pg the collection
     */
    String emlForCollection(Collection pg) {
        def markupBuilder = new StreamingMarkupBuilder()
        markupBuilder.encoding = 'UTF-8'
        markupBuilder.useDoubleQuotes = true

        def eml = markupBuilder.bind { builder ->
            mkp.xmlDeclaration()
            namespaces << ns

            def ids = identifiers(pg)

            'eml:eml'(ids.ns) {
                dataset() {

                    /* alt identifier */
                    alternateIdentifier ids.uuid

                    alternateIdentifier "${grailsApplication.config.grails.serverURL}/public/show/" + ids.id
                    if (ids.altId) {
                        alternateIdentifier(ids.altId)
                    }

                    /* title, creator, metadataProvider, associatedParty, pubDate, language, abstract */
                    commonElements1 builder, pg

                    /* keywords */
                    keywordSet() {
                        pg.listKeywords().each {
                            keyword it
                        }
                        keywordThesaurus 'free text'
                    }

                    /* distribution */
                    distribution {
                        online {
                          url('function':'information',"${grailsApplication.config.grails.serverURL}/public/show/" + pg.uid)
                        }
                    }

                    /* coverage */
                    coverage() {

                        /* geographic */
                        def hasBoundingBox = pg.eastCoordinate != ProviderGroup.NO_INFO_AVAILABLE &&
                            pg.westCoordinate != ProviderGroup.NO_INFO_AVAILABLE &&
                            pg.northCoordinate != ProviderGroup.NO_INFO_AVAILABLE &&
                            pg.southCoordinate != ProviderGroup.NO_INFO_AVAILABLE

                        if (pg.geographicDescription || hasBoundingBox) {
                            geographicCoverage() {
                                if (pg.geographicDescription) {
                                    geographicDescription pg.geographicDescription
                                }
                                // must have all bounds
                                if (hasBoundingBox) {
                                    boundingCoordinates() {
                                        westBoundingCoordinate pg.westCoordinate
                                        eastBoundingCoordinate pg.eastCoordinate
                                        northBoundingCoordinate pg.northCoordinate
                                        southBoundingCoordinate pg.southCoordinate
                                    }
                                }
                            }
                        }

                        /* temporal */
                        // no relevant data (start/end dates apply to the collection not the span of specimens

                        /* taxonomic */
                        // use taxonomic hints for now
                        taxonomicCoverage() {
                            if (pg.focus) {
                                generalTaxonomicCoverage pg.focus
                            }
                            def ranks = []
                            if (pg.kingdomCoverage) {
                                pg.listKingdoms().each { kingdom ->
                                    ranks << [rank: 'kingdom',
                                             name: kingdom]
                                }
                            }
                            if (pg.taxonomyHints) {
                                pg.listTaxonomyHints().each { taxon ->
                                    // hints may be at kingdom level and potentially duplicate the explicit kingdoms
                                    def exists = ranks.find { i ->
                                        i.rank.toLowerCase() == taxon.rank.toLowerCase() &&
                                        i.name.toLowerCase() == taxon.name.toLowerCase()
                                    }
                                    // if it's not already there - add it
                                    if (!exists) {
                                        ranks << taxon
                                    }
                                }
                            }
                            if (ranks) {
                                ranks.each { rank ->
                                    taxonomicClassification() {
                                        taxonRankName rank.rank.toLowerCase()
                                        taxonRankValue rank.name.toLowerCase()
                                    }
                                }
                            }
                        }
                    }

                    contacts builder, pg

                }

                additionalMetadata() {
                    metadata() {
                        gbif() {

                            /* dateStamp, metadataLanguage, hierarchyLevel, resourceLogoUrl */
                            commonElements2 builder, pg

                            /* collection */
                            collection() {

                                parentCollectionIdentifier pg.institution ? identifiers(pg).id : 'no parent'

                                if (ids.id.startsWith('urn:lsid')) {
                                    collectionIdentifier ids.id
                                }
                                else {
                                    collectionIdentifier pg.buildUri()
                                }

                                collectionName pg.name
                            }

                            if (pg.startDate) {
                                formationPeriod pg.startDate
                            }

                            if (pg.numRecords != -1) {
                                jgtiCuratorialUnit() {
                                    jgtiUnitType getCuratorialUnit(pg)
                                    jgtiUnits(uncertaintyMeasure:1, pg.numRecords)
                                }
                            }
                        }
                    }
                }

            }
        }

        //return eml.toString()  // for production usage
        return XmlUtil.serialize(eml) // pretty-printed for development
    }

    /**
     * Generates EML representation of an entity.
     *
     * @param pg the entity
     */
    String emlForOtherEntity(ProviderGroup pg) {

        def markupBuilder = new StreamingMarkupBuilder()
        markupBuilder.encoding = 'UTF-8'
        markupBuilder.useDoubleQuotes = true

        def eml = markupBuilder.bind { builder ->
            mkp.xmlDeclaration()
            namespaces << ns

            def ids = identifiers(pg)

            'eml:eml'(ids.ns) {
                dataset() {

                    /* alt identifier */
                    alternateIdentifier ids.uuid
                    alternateIdentifier ids.id
                    if (ids.altId) {
                        alternateIdentifier(ids.altId)
                    }

                    /* title, creator, metadataProvider, associatedParty, pubDate, language, abstract */
                    commonElements1 builder, pg

                    /* distribution */
                    distribution {
                        online {
                          url('function':'information',"${grailsApplication.config.grails.serverURL}/public/show/" + pg.uid)
                        }
                    }

                    contacts builder, pg

                }

                additionalMetadata() {
                    metadata() {
                        gbif() {
                            /* dateStamp, metadataLanguage, hierarchyLevel, resourceLogoUrl */
                            commonElements2 builder, pg
                        }
                    }
                }
            }
        }

        //return eml.toString()  // for production usage
        return XmlUtil.serialize(eml) // pretty-printed for development
    }

    /**
     * Generates EML representation of the resource.
     *
     * @param pg the data resource
     */
    String emlForResource(DataResource pg) {

        def markupBuilder = new StreamingMarkupBuilder()
        markupBuilder.encoding = 'UTF-8'
        markupBuilder.useDoubleQuotes = true
        def dp = pg.dataProvider
        def licence = Licence.where({ acronym == pg.licenseType && (pg.licenseVersion == null || licenceVersion == pg.licenseVersion) }).list()
        def eml = markupBuilder.bind { builder ->
            mkp.xmlDeclaration()
            namespaces << ns

            def ids = identifiers(pg)

            'eml:eml'(ids.ns) {
                dataset() {

                    /* alt identifier */
                    alternateIdentifier ids.uuid
                    if(pg.gbifDoi){
                        alternateIdentifier pg.gbifDoi
                    }
                    if(pg.gbifRegistryKey){
                        alternateIdentifier pg.gbifRegistryKey
                    }

                    alternateIdentifier ids.id
                    if (ids.altId) {
                        alternateIdentifier(ids.altId)
                    }

                    /* title, creator, metadataProvider, associatedParty, pubDate, language, abstract */
                    commonElements1 builder, pg

                    /* additional info */
                    if (pg.dataGeneralizations || pg.informationWithheld) {
                        additionalInfo() {
                            if (pg.dataGeneralizations) {
                                para pg.dataGeneralizations
                            }

                            if (pg.informationWithheld) {
                                para pg.informationWithheld
                            }
                        }
                    }

                    /* intellectual rights */
                    intellectualRights {
                        if (pg.rights || pg.citation || licence) {
                            para (){
                                mkp.yield pg.rights
                                if(pg.rights && pg.citation){
                                    mkp.yield " "
                                }
                                mkp.yield pg.citation
                                licence.each { Licence lic ->
                                    mkp.yield " "
                                    ulink(url: lic.url) {
                                        citetitle() {
                                            mkp.yield lic.name
                                            if (lic.acronym) {
                                                mkp.yield " ("
                                                mkp.yield lic.acronym
                                                if (lic.licenceVersion) {
                                                    mkp.yield " "
                                                    mkp.yield lic.licenceVersion
                                                }
                                                mkp.yield ")"
                                            }
                                        }
                                    }
                                }
                            }
                        }
                     }

                    /* distribution */
                    distribution {
                        online {
                            url('function':'information',"${grailsApplication.config.grails.serverURL}/public/show/" + pg.uid)
                        }
                    }

                    coverage {
                        if (pg.geographicDescription && pg.westBoundingCoordinate) {
                            geographicCoverage {
                                geographicDescription pg.geographicDescription
                                if(pg.westBoundingCoordinate) {
                                    boundingCoordinates {
                                        westBoundingCoordinate pg.westBoundingCoordinate
                                        eastBoundingCoordinate pg.eastBoundingCoordinate
                                        northBoundingCoordinate pg.northBoundingCoordinate
                                        southBoundingCoordinate pg.southBoundingCoordinate
                                    }
                                }
                            }
                        }
                        if (pg.beginDate && pg.endDate) {
                            temporalCoverage {
                                rangeOfDates {
                                    beginDate {
                                        calendarDate pg.beginDate
                                    }
                                    endDate {
                                        calendarDate pg.endDate
                                    }
                                }
                            }
                        }
                    }

                    purpose {
                        para pg.purpose?:''
                    }

                    contacts builder, pg

                    methods {
                        if (pg.methodStepDescription) {
                            methodStep {
                                description {
                                    para pg.methodStepDescription?:''
                                }
                            }
                        }
                        if (pg.qualityControlDescription) {
                            qualityControl {
                                description {
                                    para pg.qualityControlDescription?:''
                                }
                            }
                        }
                    }
                }

                additionalMetadata() {
                    metadata() {
                        gbif() {

                            /* dateStamp, metadataLanguage, hierarchyLevel, resourceLogoUrl */
                            commonElements2 builder, pg

                        }
                    }
                }
            }
         }
        
        //return eml.toString()  // for production usage
        return XmlUtil.serialize(eml) // pretty-printed for development
    }

    def addIf = { value, tag ->
        { it ->
            if (value)
                "${tag}"(value)
        }
    }

    def addAddress = { ad ->
        { it ->
            address {
                out << addIf(ad.street, 'deliveryPoint' )
                out << addIf(ad.city, 'city' )
                out << addIf(ad.state, 'administrativeArea' )
                out << addIf(ad.postcode, 'postalCode' )
                out << addIf(ad.country, 'country' )
            }
        }
    }

    def addContact = { cnt ->
        { it ->
            contact {
                if (cnt.contact.firstName || cnt.contact.lastName) {
                    individualName {
                        //out << addIf(cnt.contact.title, 'salutation')
                        out << addIf(cnt.contact.firstName, 'givenName')
                        out << surName(cnt.contact.lastName)
                    }
                }
                out << addIf(cnt.contact.phone, 'phone')
                out << addIf(cnt.contact.email, 'electronicMailAddress')
            }
        }
    }

    /**
     * inject ALA as an agentType or agentTypeWithRole
     * @param boolean if true will include role
     */
    def ala = { withRole ->
        { it ->
            organizationName grailsApplication.config.eml.organizationName
            address {
                deliveryPoint grailsApplication.config.eml.deliveryPoint
                city grailsApplication.config.eml.city
                administrativeArea grailsApplication.config.eml.administrativeArea
                postalCode grailsApplication.config.eml.postalCode
                country grailsApplication.config.eml.country
            }
            electronicMailAddress grailsApplication.config.eml.electronicMailAddress
            if (withRole) {
                role "distributor"
            }
        }
    }

    def stripFormatting(List items) {
        items.collect {
            if (it) {
                removeMarkup(handleLinks(it))
            } else {
                ''
            }
        }.join('\n').trim()
    }

    def removeMarkup(str) {
        if (str) {
            def italicMarkup = /_([^\r\n_]*)_/
            str = str.replaceAll(italicMarkup) {match, group -> group}
            def boldMarkup = /\+([^\r\n+]*)\+/
            str = str.replaceAll(boldMarkup) {match, group -> group}
        }
        return str
    }

    /**
     * Outputs str as content of the specified tag with bold markup (+xxx+) output as emphasis.
     *
     * @param builder
     * @param tag
     * @param str
     */
    def docBookEmphasis(builder, String tag, String str) {
        // docbook has no tag for italics so treat both italics and bold as emphasis
        builder."${tag}"() {
            def em = ""
            def inEm = false
            str.each { ch ->
                if (ch == '+') {
                    if (inEm) {
                        // end of emphasis span
                        builder.emphasis em
                        em = ""
                        inEm = false
                    } else {
                        // start emphasis span
                        inEm = true
                    }
                } else {
                    if (inEm) {
                        // add to span
                        em += ch
                    } else {
                        // just output
                        mkp.yield ch
                    }
                }
            }
        }
    }

    /**
     * Transforms wiki style link markup ([url name]) to name (url)
     * @param str
     * @return
     */
    def handleLinks(str) {
        if (str) {
            def urlMatch = /\[(https?:\S*)\b ([^\]]*)\]/   // [(http + s(optional) + : + text to next word boundary + space + all text until next ]
            str = str.replaceAll(urlMatch) {s1, s2, s3 ->
                "${s2} (${s3})"
            }
        }
        return str
    }

    def getCuratorialUnit(Collection pg) {
        def types = pg.collectionType
        if (types =~ "preserved") {
            return "specimens"
        }
        else if (types =~ "cellcultures") {
            return "cultures"
        }
        else if (types =~ "genetic") {
            return "samples"
        }
        else {
            return "specimens"  // default
        }
    }

}
