package au.org.ala.collectory

import org.codehaus.groovy.grails.web.json.JSONArray
import grails.converters.JSON

import grails.web.JSONBuilder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.web.json.JSONObject

class CrudService {

    static transactional = true
    def idGeneratorService

    static baseStringProperties = ['guid','name','acronym','phone','email','state','pubDescription','techDescription','notes',
                'isALAPartner','focus','attributions','websiteUrl','networkMembership','altitude',
                'street','postBox','postcode','city','state','country','file','caption','attribution','copyright']
    static baseNumberProperties = ['latitude','longitude']
    static baseObjectProperties = ['address', 'imageRef','logoRef']
    static baseJSONArrays = ['networkMembership']

    static dataHubStringProperties = ['memberDataResources']
    static dataHubNumberProperties = []

    static dataResourceStringProperties = ['rights','citation','dataGeneralizations','informationWithheld',
                'permissionsDocument','licenseType','licenseVersion','status','mobilisationNotes','provenance',
                'harvestingNotes','connectionParameters','resourceType','permissionsDocumentType','riskAssessment',
                'filed','publicArchiveAvailable','contentTypes','defaultDarwinCoreValues']
    static dataResourceNumberProperties = ['harvestFrequency','downloadLimit']
    static dataResourceTimestampProperties = ['lastChecked','dataCurrency']
    static dataResourceJSONArrays = ['connectionParameters', 'contentTypes', 'defaultDarwinCoreValues']
    //static dataResourceObjectProperties = ['dataProvider']

    static tempDataResourceStringProperties = ['firstName','lastName','name','email']
    static tempDataResourceNumberProperties = ['numberOfRecords']

    static institutionStringProperties = ['institutionType']

    static collectionStringProperties = ['collectionType','keywords','active','states','geographicDescription',
            'startDate','endDate','kingdomCoverage','scientificNames','subCollections']
    static collectionNumberProperties = ['numRecords','numRecordsDigitised','eastCoordinate','westCoordinate',
            'northCoordinate','southCoordinate']
    static collectionJSONArrays = ['keywords','collectionType','scientificNames','subCollections']

    //static collectionObjectProperties = ['institution','providerMap']

    /**
     * NOTE **** containsKey does not work on JSONObject until version 1.3.4
     * using keySet().contains until then
     */
    def has = {map, key ->
        return map.keySet().contains(key)
    }

    /* data provider */

    def readDataProvider(DataProvider p) {
        def builder = new JSONBuilder()

        def result = builder.build {
            name = p.name
            acronym = p.acronym
            uid = p.uid
            guid = p.guid
            if (p.address) {
                address {
                    street = p.address?.street
                    city = p.address?.city
                    state = p.address?.state
                    postcode = p.address?.postcode
                    country = p.address?.country
                    postBox = p.address?.postBox
                }
            } else {
                address = null
            }
            phone = p.phone
            email = p.email
            pubDescription = p.pubDescription
            techDescription = p.techDescription
            focus = p.focus
            if (p.latitude != -1) latitude = p.latitude
            if (p.longitude != -1) longitude = p.longitude
            state = p.state
            websiteUrl = p.websiteUrl
            alaPublicUrl = p.buildPublicUrl()
            if (p.imageRef?.file) {
                imageRef {
                    filename = p.imageRef?.file
                    caption = p.imageRef?.caption
                    copyright = p.imageRef?.copyright
                    attribution = p.imageRef?.attribution
                    uri = ConfigurationHolder.config.grails.serverURL + "/data/dataProvider/" + p.imageRef.file
                }
            }
            if (p.logoRef?.file) {
                logoRef {
                    filename = p.logoRef?.file
                    caption = p.logoRef?.caption
                    copyright = p.logoRef?.copyright
                    attribution = p.logoRef?.attribution
                    uri = ConfigurationHolder.config.grails.serverURL + "/data/dataProvider/" + p.logoRef.file
                }
            }
            use (OutputFormat) {
                networkMembership = p.networkMembership?.formatNetworkMembership()
                attributions = p.attributionList.formatAttributions()
                dateCreated = p.dateCreated
                lastUpdated = p.lastUpdated
                userLastModified = p.userLastModified

                // provider specific
                dataResources = p.resources.briefEntity()
                if (p.listConsumers()) {
                    linkedRecordConsumers = p.listConsumers().formatEntitiesFromUids()
                }
            }
        }
        return result
    }

    def insertDataProvider(obj) {
        DataProvider dp = new DataProvider(uid: idGeneratorService.getNextDataProviderId())
        updateBaseProperties(dp, obj)
        dp.userLastModified = obj.user ?: 'Data services'
        if (!dp.hasErrors()) {
             dp.save(flush: true)
        }
        return dp
    }
    
    def updateDataProvider(dp, obj) {
        updateBaseProperties(dp, obj)
        dp.userLastModified = obj.user ?: 'Data services'
        if (!dp.hasErrors()) {
             dp.save(flush: true)
        }
        return dp
    }

    /* data hub */

    def readDataHub(DataHub p) {
        def builder = new JSONBuilder()

        def result = builder.build {
            name = p.name
            acronym = p.acronym
            uid = p.uid
            guid = p.guid
            if (p.address) {
                address {
                    street = p.address?.street
                    city = p.address?.city
                    state = p.address?.state
                    postcode = p.address?.postcode
                    country = p.address?.country
                    postBox = p.address?.postBox
                }
            } else {
                address = null
            }
            phone = p.phone
            email = p.email
            pubDescription = p.pubDescription
            techDescription = p.techDescription
            focus = p.focus
            if (p.latitude != -1) latitude = p.latitude
            if (p.longitude != -1) longitude = p.longitude
            state = p.state
            websiteUrl = p.websiteUrl
            alaPublicUrl = p.buildPublicUrl()
            if (p.imageRef?.file) {
                imageRef {
                    filename = p.imageRef?.file
                    caption = p.imageRef?.caption
                    copyright = p.imageRef?.copyright
                    attribution = p.imageRef?.attribution
                    uri = ConfigurationHolder.config.grails.serverURL + "/data/dataHub/" + p.imageRef.file
                }
            }
            if (p.logoRef?.file) {
                logoRef {
                    filename = p.logoRef?.file
                    caption = p.logoRef?.caption
                    copyright = p.logoRef?.copyright
                    attribution = p.logoRef?.attribution
                    uri = ConfigurationHolder.config.grails.serverURL + "/data/dataHub/" + p.logoRef.file
                }
            }
            use (OutputFormat) {
                networkMembership = p.networkMembership?.formatNetworkMembership()
                attributions = p.attributionList.formatAttributions()
                dateCreated = p.dateCreated
                lastUpdated = p.lastUpdated
                userLastModified = p.userLastModified
            }
            // hub specific
            members = p.listMembers()
            memberInstitutions = p.listMemberInstitutions()
            memberCollections = p.listMemberCollections()
            memberDataResources = p.listMemberDataResources()
        }
        return result
    }

    def insertDataHub(obj) {
        DataHub dp = new DataHub(uid: idGeneratorService.getNextDataHubId())
        updateBaseProperties(dp, obj)
        dp.userLastModified = obj.user ?: 'Data services'
        if (!dp.hasErrors()) {
             dp.save(flush: true)
        }
        return dp
    }

    def updateDataHub(dh, obj) {
        updateBaseProperties(dh, obj)
        updateDataHubProperties(dh, obj)
        dh.userLastModified = obj.user ?: 'Data services'
        if (!dh.hasErrors()) {
            dh.save(flush: true)
        }
        return dh
    }

    def updateDataHubProperties(dh, obj){
        dh.properties[dataHubStringProperties] = obj
        dh.properties[dataHubNumberProperties] = obj
    }

    /* data resource */

    def readDataResource(DataResource p) {
        def builder = new JSONBuilder()

        def result = builder.build {
            name = p.name
            acronym = p.acronym
            uid = p.uid
            guid = p.guid
            if (p.address) {
                address {
                    street = p.address?.street
                    city = p.address?.city
                    state = p.address?.state
                    postcode = p.address?.postcode
                    country = p.address?.country
                    postBox = p.address?.postBox
                }
            } else {
                address = null
            }
            phone = p.phone
            email = p.email
            pubDescription = p.pubDescription
            techDescription = p.techDescription
            focus = p.focus
            if (p.latitude != -1) latitude = p.latitude
            if (p.longitude != -1) longitude = p.longitude
            state = p.state
            websiteUrl = p.websiteUrl
            alaPublicUrl = p.buildPublicUrl()
            if (p.imageRef?.file) {
                imageRef {
                    filename = p.imageRef?.file
                    caption = p.imageRef?.caption
                    copyright = p.imageRef?.copyright
                    attribution = p.imageRef?.attribution
                    uri = ConfigurationHolder.config.grails.serverURL + "/data/dataResource/" + p.imageRef.file
                }
            }
            if (p.logoRef?.file) {
                logoRef {
                    filename = p.logoRef?.file
                    caption = p.logoRef?.caption
                    copyright = p.logoRef?.copyright
                    attribution = p.logoRef?.attribution
                    uri = ConfigurationHolder.config.grails.serverURL + "/data/dataResource/" + p.logoRef.file
                }
            }
            use (OutputFormat) {
                networkMembership = p.networkMembership?.formatNetworkMembership()
                hubMembership = p.listHubMembership()?.formatHubMembership()
                taxonomyCoverageHints = JSONHelper.taxonomyHints(p.taxonomyHints)
                attributions = p.attributionList.formatAttributions()
                dateCreated = p.dateCreated
                lastUpdated = p.lastUpdated
                userLastModified = p.userLastModified

                // resource specific
                if (p.dataProvider) {
                    provider { name = p.dataProvider.name; uri = p.dataProvider.buildUri(); uid = p.dataProvider.uid }
                }
                rights = p.rights
                licenseType = p.licenseType
                licenseVersion = p.licenseVersion
                citation = p.citation
                resourceType = p.resourceType
                dataGeneralizations = p.dataGeneralizations
                informationWithheld = p.informationWithheld
                permissionsDocument = p.permissionsDocument
                permissionsDocumentType = p.permissionsDocumentType
                if (p.permissionsDocumentType == 'Data Provider Agreement') {
                    filed = p.filed
                    riskAssessment = p.riskAssessment
                }
                contentTypes = p.contentTypes ? p.contentTypes.formatJSON() : []
                if (p.listConsumers()) {
                    linkedRecordConsumers = p.listConsumers().formatEntitiesFromUids()
                }
                if (p.connectionParameters) {
                    connectionParameters = p.connectionParameters.formatJSON()
                }
                if (p.defaultDarwinCoreValues) {
                    defaultDarwinCoreValues = p.defaultDarwinCoreValues.formatJSON()
                }
                hasMappedCollections = p.hasMappedCollections()
                status = p.status
                provenance = p.provenance
                harvestFrequency = p.harvestFrequency
                lastChecked = p.lastChecked
                dataCurrency = p.dataCurrency
                harvestingNotes = p.harvestingNotes
                publicArchiveAvailable = p.publicArchiveAvailable
                publicArchiveUrl = ConfigurationHolder.config.resource.publicArchive.url.template.replaceAll('@UID@',p.uid)
                downloadLimit = p.downloadLimit
            }
        }
        return result
    }

    def insertDataResource(obj) {
        DataResource dr = new DataResource(uid: idGeneratorService.getNextDataResourceId())
        updateBaseProperties(dr, obj)
        updateDataResourceProperties(dr, obj)
        dr.userLastModified = obj.user ?: 'Data services'
        if (!dr.hasErrors()) {
             dr.save(flush: true)
        }
        return dr
    }

    def updateDataResource(dr, obj) {
        updateBaseProperties(dr, obj)
        updateDataResourceProperties(dr, obj)
        dr.userLastModified = obj.user ?: 'Data services'
        if (!dr.hasErrors()) {
             dr.save(flush: true)
        }
        return dr
    }

    private void updateDataResourceProperties(DataResource dr, obj) {
        convertJSONToString(obj, dataResourceJSONArrays)
        dr.properties[dataResourceStringProperties] = obj
        dr.properties[dataResourceNumberProperties] = obj
        updateTimestamps(dr,obj, dataResourceTimestampProperties)
        if (obj.has('dataProvider')) {
            // find it
            DataProvider dp = DataProvider._get(obj.dataProvider.uid) as DataProvider
            if (dp) {
                dr.dataProvider = dp
            }
        }
    }

    /* temp data resource */

    def readTempDataResource(TempDataResource p) {
        def builder = new JSONBuilder()

        def result = builder.build {
            name = p.name
            uid = p.uid
            email = p.email
            firstName = p.firstName
            lastName = p.lastName
            dateCreated = p.dateCreated
            lastUpdated = p.lastUpdated
            numberOfRecords = p.numberOfRecords
        }
        return result
    }

    def insertTempDataResource(obj) {
        TempDataResource drt= new TempDataResource(uid: idGeneratorService.getNextTempDataResource())
        updateTempDataResourceProperties(drt, obj)
        //drt.userLastModified = obj.user ?: 'Data services'
        if (!drt.hasErrors()) {
             drt.save(flush: true)
        }
        return drt
    }

    def updateTempDataResource(drt, obj) {
        updateBaseProperties(drt, obj)
        updateTempDataResourceProperties(drt, obj)
        //drt.userLastModified = obj.user ?: 'Data services'
        if (!drt.hasErrors()) {
             drt.save(flush: true)
        }
        return drt
    }

    def updateTempDataResourceProperties(drt, obj) {
        drt.properties[tempDataResourceStringProperties] = obj
        drt.properties[tempDataResourceNumberProperties] = obj
    }

    /* institution */

    def readInstitution(Institution p) {
        def builder = new JSONBuilder()

        def result = builder.build {
            name = p.name
            acronym = p.acronym
            uid = p.uid
            guid = p.guid
            if (p.address) {
                address {
                    street = p.address?.street
                    city = p.address?.city
                    state = p.address?.state
                    postcode = p.address?.postcode
                    country = p.address?.country
                    postBox = p.address?.postBox
                }
            } else {
                address = null
            }
            phone = p.phone
            email = p.email
            pubDescription = p.pubDescription
            techDescription = p.techDescription
            focus = p.focus
            if (p.latitude != -1) latitude = p.latitude
            if (p.longitude != -1) longitude = p.longitude
            state = p.state
            websiteUrl = p.websiteUrl
            alaPublicUrl = p.buildPublicUrl()
            if (p.imageRef?.file) {
                imageRef {
                    filename = p.imageRef?.file
                    caption = p.imageRef?.caption
                    copyright = p.imageRef?.copyright
                    attribution = p.imageRef?.attribution
                    uri = ConfigurationHolder.config.grails.serverURL + "/data/institution/" + p.imageRef.file
                }
            }
            if (p.logoRef?.file) {
                logoRef {
                    filename = p.logoRef?.file
                    caption = p.logoRef?.caption
                    copyright = p.logoRef?.copyright
                    attribution = p.logoRef?.attribution
                    uri = ConfigurationHolder.config.grails.serverURL + "/data/institution/" + p.logoRef.file
                }
            }
            use (OutputFormat) {
                networkMembership = p.networkMembership?.formatNetworkMembership()
                hubMembership = p.listHubMembership()?.formatHubMembership()
                attributions = p.attributionList.formatAttributions()
                dateCreated = p.dateCreated
                lastUpdated = p.lastUpdated
                userLastModified = p.userLastModified

                // institution specific
                institutionType = p.institutionType
                collections = p.collections.briefEntity()
                parentInstitutions = p.listParents().briefEntity()
                childInstitutions = p.listChildren().briefEntity()
                if (p.listProviders()) {
                    linkedRecordProviders = p.listProviders().formatEntitiesFromUids()
                }
            }
        }
        return result
    }

    def insertInstitution(obj) {
        Institution inst = new Institution(uid: idGeneratorService.getNextInstitutionId())
        updateBaseProperties(inst, obj)
        updateInstitutionProperties(inst, obj)
        inst.userLastModified = obj.user ?: 'Data services'
        if (!inst.hasErrors()) {
             inst.save(flush: true)
        }
        return inst
    }

    def updateInstitution(inst, obj) {
        updateBaseProperties(inst, obj)
        updateInstitutionProperties(inst, obj)
        inst.userLastModified = obj.user ?: 'Data services'
        if (!inst.hasErrors()) {
             inst.save(flush: true)
        }
        return inst
    }

    private void updateInstitutionProperties(Institution inst, obj) {
        inst.properties[institutionStringProperties] = obj
    }

    /* collection */

    /*def basics = { c ->
        { it ->
            name = c.name
            acronym = c.acronym
            uid = c.uid
            guid = c.guid
        }
    }*/

    def readCollection(Collection p) {
        def builder = new JSONBuilder()

        def result = builder.build {
            name = p.name
            acronym = p.acronym
            uid = p.uid
            guid = p.guid
            if (p.address) {
                address {
                    street = p.address?.street
                    city = p.address?.city
                    state = p.address?.state
                    postcode = p.address?.postcode
                    country = p.address?.country
                    postBox = p.address?.postBox
                }
            } else {
                address = null
            }
            phone = p.phone
            email = p.email
            pubDescription = p.pubDescription
            techDescription = p.techDescription
            focus = p.focus
            if (p.latitude != -1) latitude = p.latitude
            if (p.longitude != -1) longitude = p.longitude
            state = p.state
            websiteUrl = p.websiteUrl
            alaPublicUrl = p.buildPublicUrl()
            if (p.imageRef?.file) {
                imageRef {
                    filename = p.imageRef?.file
                    caption = p.imageRef?.caption
                    copyright = p.imageRef?.copyright
                    attribution = p.imageRef?.attribution
                    uri = ConfigurationHolder.config.grails.serverURL + "/data/collection/" + p.imageRef.file
                }
            }
            use (OutputFormat) {
                networkMembership = p.networkMembership?.formatNetworkMembership()
                hubMembership = p.listHubMembership()?.formatHubMembership()
                taxonomyCoverageHints = JSONHelper.taxonomyHints(p.taxonomyHints)
                attributions = p.attributionList.formatAttributions()

                dateCreated = p.dateCreated
                lastUpdated = p.lastUpdated
                userLastModified = p.userLastModified

                // collection specific
                collectionType = p.collectionType?.formatJSON()
                keywords = p.keywords?.formatJSON()

                active = p.active
                numRecords = p.numRecords == -1 ? 'not known' : p.numRecords
                numRecordsDigitised = p.numRecordsDigitised == -1 ? 'not known' : p.numRecordsDigitised
                states = p.states
                geographicDescription = p.geographicDescription
                if (p.eastCoordinate + p.westCoordinate + p.northCoordinate + p.southCoordinate != -4) {
                    geographicRange {
                        eastCoordinate = p.eastCoordinate == -1 ? 'not known' : p.eastCoordinate
                        westCoordinate = p.westCoordinate == -1 ? 'not known' : p.westCoordinate
                        northCoordinate = p.northCoordinate == -1 ? 'not known' : p.northCoordinate
                        southCoordinate = p.southCoordinate == -1 ? 'not known' : p.southCoordinate
                    }
                }
                startDate = p.startDate
                endDate = p.endDate
                kingdomCoverage = p.kingdomCoverage?.formatSpaceSeparatedList()
                scientificNames = p.scientificNames?.formatJSON()
                subCollections = p.listSubCollections()
                if (p.institution) {
                    institution {
                        name = p.institution.name
                        uri = p.institution.buildUri()
                        uid = p.institution.uid
                    }
                }
                if (p.providerMap) {
                    recordsProviderMapping {
                        collectionCodes = p.getListOfCollectionCodesForLookup()
                        institutionCodes = p.getListOfInstitutionCodesForLookup()
                        matchAnyCollectionCode = p.providerMap.matchAnyCollectionCode
                        exact = p.providerMap.exact
                        warning = p.providerMap.warning
                        dateCreated = p.providerMap.dateCreated
                        lastUpdated = p.providerMap.lastUpdated
                    }
                }
                if (p.listProviders()) {
                    linkedRecordProviders = p.listProviders().formatEntitiesFromUids()
                }
            }
        }
        return result
    }

    def insertCollection(obj) {
        Collection inst = new Collection(uid: idGeneratorService.getNextCollectionId())
        updateBaseProperties(inst, obj)
        updateCollectionProperties(inst, obj)
        inst.userLastModified = obj.user ?: 'Data services'
        if (!inst.hasErrors()) {
             inst.save(flush: true)
        }
        return inst
    }

    def updateCollection(inst, obj) {
        updateBaseProperties(inst, obj)
        updateCollectionProperties(inst, obj)
        inst.userLastModified = obj.user ?: 'Data services'
        if (inst.hasErrors()) {
            inst.errors.each { log.error it }
        }
        else {
             inst.save(flush: true)
        }
        return inst
    }

    private void updateCollectionProperties(Collection co, obj) {
        // handle values that might be passed as JSON arrays or string representations of JSON arrays
        convertJSONToString(obj, collectionJSONArrays)
        co.properties[collectionStringProperties] = obj
        co.properties[collectionNumberProperties] = obj
        if (obj.has('institution')) {
            if (!obj.institution.has('uid')) {
                co.errors.rejectValue('institution','NO_UID','institution must specify a uid')
            } else {
                // find it
                Institution institution = Institution._get(obj.institution.uid) as Institution
                if (institution) {
                    co.institution = institution
                } else {
                    co.errors.rejectValue('institution','NOT_FOUND',"specified institution (${obj.institution.uid}) does not exist")
                }
            }
        }
        // handle provider codes
        if (obj.has('recordsMapping')) {
            def map = obj.recordsMapping
            // check existing map
            ProviderMap pm = co.id ? ProviderMap.findByCollection(co) : null
            if (pm) {
                // clear codes
                def colls = pm.collectionCodes.collect{it}
                colls.each {pm.removeFromCollectionCodes it}
                def insts = pm.institutionCodes.collect{it}
                insts.each {pm.removeFromInstitutionCodes it}
            } else {
                pm = new ProviderMap()
                pm.collection = co
            }
            // get codes
            if (map.has('institutionCodes')) {
                def instCodes = (map.institutionCodes instanceof String) ? [map.institutionCodes] : map.institutionCodes.collect{it}
                instCodes.each {
                    // does it exist
                    ProviderCode pc = ProviderCode.findByCode(it)
                    if (!pc) {
                        pc = new ProviderCode(code: it)
                    }
                    pm.addToInstitutionCodes(pc)
                }
            }
            if (map.has('collectionCodes')) {
                def collCodes = (map.collectionCodes instanceof String) ? [map.collectionCodes] : map.collectionCodes.collect{it}
                collCodes.each {
                    // does it exist
                    ProviderCode pc = ProviderCode.findByCode(it)
                    if (!pc) {
                        pc = new ProviderCode(code: it)
                    }
                    pm.addToCollectionCodes(pc)
                }
            }
            if (map.has('exact')) {
                pm.exact = map.exact
            }
            if (map.has('warning') && map.warning != 'null' && map.warning != "") {
                pm.warning = map.warning
            }
            if (map.has('matchAnyCollectionCode')) {
                pm.matchAnyCollectionCode = map.matchAnyCollectionCode
            }
            co.providerMap = pm
        }
    }

    private void updateBaseProperties(pg, obj) {
        adjustEmptyProperties obj
        // handle values that might be passed as JSON arrays or string representations of JSON arrays
        convertJSONToString(obj, baseJSONArrays)
        // inject properties (this method does type conversions automatically)
        pg.properties[baseStringProperties] = obj
        pg.properties[baseNumberProperties] = obj
        // only add objects if they exist
        baseObjectProperties.each {
            if (obj.has(it)) {
                pg."${it}" = obj."${it}"
            }
        }
    }

    /**
     * We don't want to create objects in the target if there is no data for them.
     * @param obj
     */
    private void removeNullObjects(obj) {
        baseObjectProperties.each {
            if (obj.has(it) && obj."${it}".toString() == 'null') {
                obj.remove(it)
            }
        }
    }

    /**
     * Null numbers are represented as -1 (as they cannot be null).
     * JSON null objects are changed to Java null.
     * Properties that are objects are processed recursively.
     * @param obj map of properties to adjust
     */
    private void adjustEmptyProperties(obj) {
        // numbers should be set to -1 if the value comes in as null
        baseNumberProperties.each {
            if (obj.has(it) && obj."${it}"?.toString() == 'null') {
                obj."${it}" = -1
            }
        }
        // null objects are copies of JSONObject.NULL - set them to Java null
        [baseStringProperties,dataResourceStringProperties].flatten().each {
            //println "checking base string property ${it} " + obj.has(it) ? "exists - " + obj."${it}" : "absent"
            if (obj.has(it) && obj."${it}".toString() == 'null') {
                obj."${it}" = null
            }
        }
        baseObjectProperties.each {
            if (obj.has(it)) {
                if (obj."${it}".toString() == 'null') {
                    obj."${it}" = null
                } else {
                    // adjust nested properties - all known nested props are strings
                    adjustEmptyProperties(obj."${it}")
                }
            }
        }
    }

    def updateTimestamps(pg, obj, properties) {
        properties.each {
            def strDate = obj."${it}"
            if (strDate) {
                if (strDate == 'now') {
                    pg."${it}" = new Date().toTimestamp()
                }
                else {
                    pg."${it}" = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(strDate).toTimestamp()
                }
            }
        }
    }

    /**
     * Handles values that might be passed as JSON arrays/objects or string representations of JSON arrays/objects
     * @param obj
     * @param properties
     * @return
     */
    def convertJSONToString(obj, properties) {
        properties.each {
            if (obj.has(it) && (obj."${it}" instanceof JSONArray || obj."${it}" instanceof JSONObject)) {
                // convert to string representation
                obj."${it}" = obj."${it}".toString()
            }
        }
    }
}

class OutputFormat {

    static def formatEntitiesFromUids(listOfUid) {
        if (!listOfUid) return null
        def result = []
        listOfUid.each {
            def pg = ProviderGroup._get(it)
            if (pg) {
                result << [name: pg.name, uri: pg.buildUri(), uid: pg.uid]
            }
        }
        return result
    }

    static def formatJSON(jsonListStr) {
        if (!jsonListStr) return null
        try {
            return JSON.parse(jsonListStr)
        } catch (ConverterException e) {
            return "error"
        }
    }

    static def formatAttributions(List list) {
        def result = []
        list.each {
            //println "attribution ${it.name} - ${it.url}"
            // lookup attribution
            result << [name: it.name, url: it.url]
        }
        return result
    }

    static def formatSpaceSeparatedList(str) {
        str.tokenize(" ")
    }

    static def briefEntity(list) {
        return list.collect {[name: it.name, uri: it.buildUri(), uid: it.uid]}
    }

    static def formatHubMembership(hubs) {
        return hubs.collect { [uid: it.uid, name: it.name, uri: it.buildUri()] }
    }

    static def formatNetworkMembership(jsonListStr) {
        if (!jsonListStr) return null
        def list
        try {
            list = JSON.parse(jsonListStr)
        } catch (ConverterException e) {
            return "error"
        }
        def result = []
        list.each {
            switch (it) {
                case 'CHAFC':
                    result << [name: 'Council of Heads of Australian Faunal Collections', acronym: it,
                            logo: ConfigurationHolder.config.grails.serverURL + "/data/network/CHAFC_sm.jpg"]
                    break
                case 'CHAEC':
                    result << [name: 'Council of Heads of Australian Entomological Collections', acronym: it,
                            logo: ConfigurationHolder.config.grails.serverURL + "/data/network/chaec-logo.png"]
                    break
                case 'CHAH':
                    result << [name: 'Council of Heads of Australasian Herbaria', acronym: it,
                            logo: ConfigurationHolder.config.grails.serverURL + "/data/network/CHAH_logo_col_70px_white.gif"]
                    break
                case 'CHACM':
                    result << [name: 'Council of Heads of Australian Collections of Microorganisms', acronym: it,
                            logo: ConfigurationHolder.config.grails.serverURL + "/data/network/chacm.png"]
                    break
                default:
                    result << "did not match"
                    break
            }
        }
        return result
    }

}