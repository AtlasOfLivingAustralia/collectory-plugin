package au.org.ala.collectory
import au.com.bytecode.opencsv.CSVWriter
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder

import java.text.NumberFormat
import java.text.ParseException
/**
 * Handles all the public pages generated from the collectory.
 *
 * This includes:
 * Natural History Collections page - with map of collection locations (map)
 * Display pages for collection, institution, data provider and data resource (show)
 *
 * Handles ajax requests for data for those pages.
 */
class PublicController {

    def collectoryAuthService

    def delay = 3000    // testing delay for responses

    def sleep = {}

    def renderJson = {json ->
        if (params.callback) {
            render(contentType:'text/javascript', text: "${params.callback}(${json})", encoding: "UTF-8")
        } else {
            render json
        }
    }

    def renderAsJson = { json -> renderJson(json as JSON) }

    def index = { redirect(action: 'map') }

    /**
     * Do logouts through this app so we can invalidate the session.
     *
     * @param casUrl the url for logging out of cas
     * @param appUrl the url to redirect back to after the logout
     */
    def logout = {
        session.invalidate()
        redirect(url:"${params.casUrl}?url=${params.appUrl}")
    }

    /**
     * Shows the public page for any entity when passed a UID.
     *
     * If the id is not a UID it will be assumed to be a collection and will be treated as:
     * 1. lsid if it starts with uri:lsid:
     * 2. database id if it is a number
     * 3. acronym if it matches a collection
     */
    def show = {
        // is it a UID
        if (params.id instanceof String && params.id.startsWith(Institution.ENTITY_PREFIX)) {
            forward(action: 'showInstitution', params: params)
        } else if (params.id instanceof String && params.id.startsWith('dp')) {
            forward(action: 'showDataProvider', params: params)
        } else if (params.id instanceof String && params.id.startsWith('drt')) {
            forward(action: 'showTempDataResource', params: params)
        } else if (params.id instanceof String && params.id.startsWith('dr')) {
            forward(action: 'showDataResource', params: params)
        } else if (params.id instanceof String && params.id.startsWith('dh')) {
            forward(action: 'showDataHub', params: params)
        } else {
            // assume it's a collection
            def collectionInstance = findCollection(params.id)
            if (!collectionInstance) {
                flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'collection.label', default: 'Collection'), params?.id])}"
                redirect(controller: "public", action: "map")
            } else {
                ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), collectionInstance.uid, Action.VIEW
                [instance: collectionInstance, contacts: collectionInstance.getContacts(),
                        biocacheRecordsAvailable: collectionInstance.providerMap]
            }
        }
    }

    /**
     * json call to retrieve a summary of biocache records
     *
     * @param uid the uid of an entity with records or a comma separated list of uids
     * @return total number + decade breakdown + other facets as Google data table
     */
    def biocacheRecords = {
        def uid = params.uid

        def url = grailsApplication.config.biocacheServicesUrl + "/occurrences/search?q=" +
             uid.tokenize(',').collect({ fieldNameForSearch(it) + ":" + it}).join(' OR '.encodeAsURL())

        def conn = new URL(url).openConnection()
        try {
            conn.setConnectTimeout(10000)
            conn.setReadTimeout(50000)
            def json = conn.content.text
            def searchResult = JSON.parse(json)

            // build map of facets
            def facets = [:]
            searchResult?.facetResults?.each {
                facets.put it.fieldName, it.fieldResult
            }

            // build response
            def result = [
                    totalRecords: searchResult?.totalRecords,
                    decades: buildDecadeDataTableFromFacetResults(searchResult?.facetResults)
            ]

            // add additional facets
            ['institution_uid','country','state','species_group','state_conservation','assertions'].each {
                if (facets[it]) {
                    result.put it, buildPieChartDataTable(facets[it], it, uid)
                }
            }

            renderAsJson result
        } catch (SocketTimeoutException e) {
            log.warn "Timed out looking up record count. URL= ${url}."
            def error = [error:"Timed out looking up record count.", totalRecords: 0, decades: null]
            renderAsJson error
        } catch (Exception e) {
            log.warn "Failed to lookup record count. ${e.getClass()} ${e.getMessage()} URL= ${url}."
            def error = ["error":"Failed to lookup record count. ${e.getClass()} ${e.getMessage()} URL= ${url}."]
            renderAsJson error
        }
    }

    def getRecordsSummary(uid) {
        // lookup number of biocache records
        def url = "http://ala-bie1.vm.csiro.au:8080/biocache-service/occurrences/institutions/${uid}.json?pageSize=0"

        def conn = new URL(url).openConnection()
        try {
            conn.setConnectTimeout(10000)
            conn.setReadTimeout(50000)
            //conn.setRequestProperty('Connection','close')
            def json = conn.content.text
            return JSON.parse(json)
        } catch (SocketTimeoutException e) {
            log.warn "Timed out looking up record count. URL= ${url}."
            def error = [error:"Timed out looking up record count.", totalRecords: 0, decades: null]
            return error as JSON
        } catch (Exception e) {
            log.warn "Failed to lookup record count. ${e.getClass()} ${e.getMessage()} URL= ${url}."
            def error = ["error":"Failed to lookup record count. ${e.getClass()} ${e.getMessage()} URL= ${url}."]
            return error as JSON
        }
    }

    def recordsByDecadeByInstitution = {
        if (params.static) {
            String json = new File("/data/collectory/data/dataHub/records-by-decade-by-institution-dh1.json").getText()
            renderJson json
        } else {
            def result = []
            def institutions = [
                    [uid:'in4',name:'Australian Museum',acronym:'AM',label:"Aust Museum"],
                    [uid:'in16',name:'Museum Victoria',acronym:'NMV',label:"Museum Victoria"],
                    [uid:'in34',name:'Western Australian Museum',acronym:'WAM',label:"WA Museum"],
                    [uid:'in22',name:'South Australian Museum',acronym:'SAM',label:"SA Museum"],
                    [uid:'in17',name:'Northern Territory Museum and Art Gallery',acronym:'MAGNT',label:"MAGNT"],
                    [uid:'in13',name:'Queen Victoria Museum Art Gallery',acronym:'QVMAG',label:"QVMAG"],
                    [uid:'in15',name:'Queensland Museum',acronym:'QM',label:"Qld Museum"],
                    [uid:'in25',name:'Tasmanian Museum and Art Gallery',acronym:'TMAG',label:"TMAG"],
                    [uid:'co16',name:'Australian National Wildlife Collection',acronym:'ANWC',label:"ANWC"]
            ]
            institutions.each {
                def facets = getRecordsSummary(it.uid).facetResults
                def decadeBreakdown = facets.find {it.fieldName == 'occurrence_date'}
                def decades = [:]
                decades.put('name', it.name)
                decades.put('acronym', it.acronym)
                decades.put('label', it.label)
                decades.put('uid', it.uid)
                def total = 0
                decadeBreakdown.fieldResult.each {
                    if (it.label != 'before') {
                        total += it.count
                        decades.put('d' + it.label[0..3], total)
                    }
                }
                result << decades
            }
            if (params.save) {
                new File("/data/collectory/data/dataHub/records-by-decade-by-institution-dh1.json").setText((result as JSON) as String)
            }
            renderAsJson result
        }
    }

    def recordsByCollectionByInstitution = {
        response.addHeader('Content-Type','application/json')
        String json = new File("/data/collectory/data/dataHub/count-by-collection-dh1.json").getText()
        // need to parse first as source file is formatted with white-space
        def obj = JSON.parse(json)
        renderAsJson obj
    }

    def newBiocacheBreakdown = {
        def url = "http://ala-bie1.vm.csiro.au:8080/biocache-service/occurrences/search.json?q=*:*&pageSize=0";
        def conn = new URL(url).openConnection()
        conn.setConnectTimeout 1500
        def dataTable = null
        def json
        try {
            renderJson(conn.content.text)
        } catch (SocketTimeoutException e) {
            log.warn "Timed out getting decade breakdown. URL= ${url}."
            def result = [error:"Timed out getting decade breakdown.", dataTable: null]
            render result as JSON
        } catch (Exception e) {
            log.error "Failed to lookup decade breakdown. ${e.getMessage()} URL= ${decadeUrl}."
            render e as JSON
        }
    }

    def serviceRedirect = {
        def url = "http://ala-bie1.vm.csiro.au:8080/biocache-service/breakdown/institutions/in4/rank/${params.rank}";
        if (params.name) {
            url += "/name/${params.name}"
        }
        def conn = new URL(url).openConnection()
        conn.setConnectTimeout 1500
        def dataTable = null
        def json
        try {
            renderJson(conn.content.text)
        } catch (SocketTimeoutException e) {
            log.warn "Timed out. URL= ${url}."
            def result = [error:"Timed out.", dataTable: null]
            renderAsJson result
        } catch (Exception e) {
            log.error "Failed. ${e.getMessage()} URL= ${url}."
            renderAsJson e
        }
    }

    /**
     * Returns JSON in Google charts DataTable format showing breakdown of records by decade.
     *
     * Makes request to biocache service for breakdown data.
     */
    def decadeBreakdown = {
        response.setHeader("Pragma","no-cache")
        response.setDateHeader("Expires",1L)
        response.setHeader("Cache-Control","no-cache")
        response.addHeader("Cache-Control","no-store")
        def instance = ProviderGroup._get(params.id)
        //println ">>debug map key " + grailsApplication.config.google.maps.v2.key
        if (!instance) {
            log.error "Unable to find entity for id = ${params.id}"
            def error = ["error":"unable to find entity for id = " + params.id]
            render error as JSON
        } else {
            /* get decade breakdown */
            def decadeUrl = ConfigurationHolder.config.biocacheServicesUrl+ "/breakdown/collection/decades/${instance.generatePermalink()}.json";
            //println decadeUrl
            def conn = new URL(decadeUrl).openConnection()
            conn.setConnectTimeout 1500
            def dataTable = null
            def json
            try {
                json = conn.content.text
                //println "Response = " + json
                def decades = JSON.parse(json)?.decades
                dataTable = buildDecadeDataTable(decades)
                //println "dataTable = " + dataTable
            } catch (SocketTimeoutException e) {
                log.warn "Timed out getting decade breakdown. URL= ${url}."
                def result = [error:"Timed out getting decade breakdown.", dataTable: null]
                render result as JSON
            } catch (Exception e) {
                log.error "Failed to lookup decade breakdown. ${e.getMessage()} URL= ${decadeUrl}."
            }
            if (dataTable) {
                render dataTable
            } else {
                log.warn "unable to build data table from decade json = " + json
                def error = ["error":"Unable to build data table from decade json"]
                render error as JSON
            }
        }
    }

    def slowResponseForTesting = {
        sleep 5000
        render "Done."
    }

    /**
     * Returns JSON in Google charts DataTable format showing breakdown of records by taxonomic group.
     *
     * Makes request to biocache service for breakdown data.
     * Chooses the taxon rank based on the spread of records and the threshold value supplied in the request.
     *
     * @param id a single uid or a comma-separated list of uids
     * @param threshold a guide to the selection of an appropriate rank for the breakdown
     */
    def taxonBreakdown = {
        response.setHeader("Pragma","no-cache")
        response.setDateHeader("Expires",1L)
        response.setHeader("Cache-Control","no-cache")
        response.addHeader("Cache-Control","no-store")
        def threshold = params.threshold ?: 20
        /* get taxon breakdown */
        def taxonUrl = ConfigurationHolder.config.biocacheServicesUrl + "/breakdown/{entity}/{uid}?max=" + threshold
        taxonUrl = taxonUrl.replaceFirst(/\{uid\}/, params.id ?: '')
        taxonUrl = taxonUrl.replaceFirst(/\{entity\}/, wsEntityForBreakdown(params.id))
        //println "taxonUrl: " + taxonUrl

        def conn = new URL(taxonUrl).openConnection()
        def jsonResponse = null
        def breakdown = null
        try {
            conn.setConnectTimeout(10000)
            conn.setReadTimeout(50000)
            jsonResponse = conn.content.text
            //println "Response = " + json
            //sleep delay
            breakdown = JSON.parse(jsonResponse)?.breakdown ?: JSON.parse(jsonResponse)
        } catch (SocketTimeoutException e) {
            log.warn "Timed out getting taxa breakdown."
            def error = [error:"Timed out getting taxa breakdown.", dataTable: null]
            render error as JSON
        } catch (Exception e) {
            log.error "Failed to lookup taxa breakdown. ${e.getMessage()} URL= ${taxonUrl}."
            def error = [error:"Failed to lookup taxa breakdown. ${e.getMessage()} URL= ${taxonUrl}.", dataTable: null]
            render error as JSON
        }
        if (breakdown && breakdown.toString() != "null") {
            def dataTable = buildTaxonChartDataTable(breakdown,"all","")
            withFormat {
                // seems weird but only way to include csv but default to json
                html { render dataTable }
                json {render dataTable}
                csv { render buildCsvForTaxonBreakdown(breakdown)}
            }
        } else {
            log.warn "no data returned from taxa json = " + jsonResponse
            def error = ["error":"No data returned from taxa json"]
            render error as JSON
        }
    }

    /**
     * Returns JSON in Google charts DataTable format showing breakdown of records for the specified taxonomic group.
     *
     * Makes request to biocache service for namerank breakdown data.
     * @param id a single uid or a comma-separated list of uids
     * @param rank the rank of the taxon
     * @param name the name of the taxon
     */
    def rankBreakdown = {
        response.setHeader("Pragma","no-cache")
        response.setDateHeader("Expires",1L)
        response.setHeader("Cache-Control","no-cache")
        response.addHeader("Cache-Control","no-store")
        /* get rank breakdown */
        def rankUrl = grailsApplication.config.biocacheServicesUrl + "/breakdown/{entity}/{uid}?rank=${params.rank}&name=${params.name}"
        def conn = new URL(rankUrl).openConnection()
        def dataTable = null
        def json
        try {
            conn.setConnectTimeout 10000
            conn.setReadTimeout 50000
            json = conn.content.text
            //println "Response = " + json
            def breakdown = JSON.parse(json)?.breakdown ?: JSON.parse(json)
            if (breakdown && breakdown.toString() != "null") {
                dataTable = buildTaxonChartDataTable(breakdown,params.rank,params.name)
                if (dataTable) {
                    //sleep delay
                    render dataTable
                } else {
                    log.warn "unable to build data table from taxa json = " + json
                    def error = ["error":"Unable to build data table from taxa json"]
                    render error as JSON
                }
            }
        } catch (SocketTimeoutException e) {
            log.warn "Timed out getting rank breakdown."
            def error = [error:"Timed out getting rank breakdown.", dataTable: null]
            render error as JSON
        } catch (Exception e) {
            log.error "Failed to lookup taxa breakdown. ${e.getMessage()} URL= ${rankUrl}."
            def error = [error:"Failed to lookup taxa breakdown. ${e.getMessage()} URL= ${rankUrl}.", dataTable: null]
            render error as JSON
        }
    }

    /**
     * Shows the public page for an institution.
     */
    def showInstitution = {
        def institution = findInstitution(params.id)
        if (!institution) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'institution.label', default: 'Institution'), params.code ? params.code : params.id])}"
            redirect(controller: "public", action: "map")
        } else {
            // get some data on child institutions
            def childInstitutions = []
            def ciCollectionsCount = 0
            institution.childInstitutions?.tokenize(' ')?.each {
                def inst = ProviderGroup._get(it as String)
                if (inst) {
                    childInstitutions << inst
                    ciCollectionsCount += inst.listCollections().size()
                }
            }
            def recordExceptions = [:]
            if (childInstitutions) {
                recordExceptions['childInstitutions'] = childInstitutions
                recordExceptions['listType'] = "excludes"  // default

                def includes = [:]
                // if all collection are from child institutions - use excludes
                if (institution.listCollections().size() == ciCollectionsCount) {
                    recordExceptions['listType'] = "excludes-all"
                } else
                // we want to list either the included or excluded collections whichever is shorter
                if ((institution.listCollections().size() - ciCollectionsCount) < ciCollectionsCount) {
                    // it's better to list the includes
                    institution.listCollections().each { coll ->
                        boolean isFromChildInstitution = false
                        childInstitutions.each { inst ->
                            if (inst.uid == coll.institution?.uid) {
                                isFromChildInstitution = true
                            }
                        }
                        if (!isFromChildInstitution) {
                            includes[coll.uid] = coll.name
                        }
                    }
                    recordExceptions['listType'] = "includes"
                    recordExceptions['includes'] = includes
                }
            }

            ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.isAdmin(), institution.uid, Action.VIEW
            [instance: institution, exceptions: recordExceptions]
        }
    }

    /**
     * Shows the public page for a data provider.
     */
    def showDataProvider = {
        def instance = ProviderGroup._get(params.id)
        if (!instance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'dataProvider.label', default: 'Data provider'), params.code ? params.code : params.id])}"
            redirect(controller: "public", action: "map")
        } else {
            ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), instance.uid, Action.VIEW
            [instance: instance]
        }
    }

    /**
     * Shows the public page for a data resource.
     */
    def showDataResource = {
        def instance = ProviderGroup._get(params.id)
        if (!instance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'dataResource.label', default: 'Data resource'), params.code ? params.code : params.id])}"
            redirect(controller: "public", action: "map")
        }
        else if (instance.status == "declined") {
            render "This resource has decided to not contribute to the Atlas."
        }
        else {
            ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), instance.uid, Action.VIEW
            [instance: instance]
        }
    }

    /**
     * Shows the public page for a temporary data resource.
     */
    def showTempDataResource = {
        def instance = TempDataResource.findByUid(params.id)
        if (!instance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'dataResource.label', default: 'Data resource'), params.code ? params.code : params.id])}"
            redirect(controller: "public", action: "map")
        }
        else {
            ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), instance.uid, Action.VIEW
            def name = (instance.firstName ? instance.firstName + ' ' : '') + (instance.lastName ?: '')
            if (!name) { name = instance.email }
            if (!name) {
                def pc = instance.getPrimaryContact()
                name = pc ? pc.contact.buildName() : 'anonymous'
            }
            [instance: instance, name: name]
        }
    }

    /**
     * Shows the public page for a data hub.
     */
    def showDataHub = {
        def instance = ProviderGroup._get(params.id)
        if (!instance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'dataHub.label', default: 'Data hub'), params.code ? params.code : params.id])}"
            redirect(controller: "public", action: "map")
        } else {
            ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), instance.uid, Action.VIEW
            [instance: instance]
        }
    }

    def datasets = {
        forward(action: 'dataSets')
    }

    def dataSets = {}

    def dataSetSearch = {
        def drs = DataResource.findAllByNameLikeAndStatusNotEqual('%' + params.q + '%', 'declined').collect {
           it.uid
        }
        render drs as JSON
    }

    def resources = {
        cache shared:true, validFor: 3600*24
        def drs = DataResource.findAllByStatusNotEqual('declined',[sort:'name']).collect {
        //def drs = DataResource.list([sort:'name']).collect {
            def pdesc = it.pubDescription ? cl.formattedText(dummy:'1',limit(it.pubDescription,1000)) : ""
            def tdesc = it.techDescription ? cl.formattedText(dummy:'1',limit(it.techDescription,1000)) : ""
            def inst = it.institution
            def instName = (inst && inst.name.size() > 36 && inst.acronym) ? inst.acronym : inst?.name

            [name: it.name, resourceType: it.resourceType, licenseType: it.licenseType,
             licenseVersion: it.licenseVersion, pubDescription: pdesc, techDescription: tdesc,
             uid: it.uid, status: it.status, websiteUrl: it.websiteUrl, contentTypes: it.contentTypes,
             institution: instName]
        }
        render drs as JSON
    }

    def downloadDataSets = {
        def filters = params.filters ? JSON.parse(params.filters) : [];
        println 'filters'
        filters.each { println it }
        println 'uids: ' + params.uids
        def uids = params.uids.tokenize(',')
        def drs = DataResource.list([sort:'name'])
        /*if (filters) {
            drs = drs.findAll { dr ->
                // check each filter
                for (filter in filters) {
                    if (filter.value == 'noValue') {
                        if (dr[filter.name]) {
                            return false
                        }
                    }
                    // TODO: handle filters for properties with multiple values
                    else if (dr[filter.name] != filter.value) {
                        return false
                    }
                }
                return true
            }
        }*/
        drs = drs.findAll { dr ->
            if (dr.status == 'declined') {
                return false
            }
            else if (uids) {
                return dr.uid in uids
            }
            else {
                return true
            }
        }
        def out = new StringWriter()
        def csvWriter = new CSVWriter(out)
        csvWriter.writeNext(["name","resourceType","licenseType","licenseVersion","rights","uri","status","contact"] as String[])
        drs.each {
            csvWriter.writeNext([it.name,it.resourceType,it.licenseType,it.licenseVersion,it.rights,
                    it.buildUri(),it.status,it.inheritPrimaryPublicContact()?.contact?.buildName()] as String[])
        }
        csvWriter.close()
        response.addHeader("Content-Disposition", "attachment;filename=datasets.csv");
        render(contentType: 'text/csv', text:out.toString())
    }

    /**
     * Displays main page for Natural History Collections.
     *
     * Although an initial list of collections is placed in the model, the data is sourced by ajax callback. The initial
     * list is used if the callback fails.
     */
    def map = {
        def partnerCollections = Collection.list([sort:"name"]).findAll {
            it.isALAPartner()
        }
        render(view: 'map3', model: [collections: partnerCollections])
    }

    /**
     * Returns GEOJson for populating the map based on the selected filters.
     */
    def mapFeatures = {
        log.info ">> Map features action called (no cross domain issues)"

        def locations = [type:"FeatureCollection", features: new ArrayList()]
        def showAll = params.filters == 'all'

        //add collections
        Collection.list([sort:"name"]).each {
            // only show ALA partners
            if (it.isALAPartner()) {
                // make 0 values be -1
                def lat = (it.latitude == 0.0) ? -1 : it.latitude
                def lon = (it.longitude == 0.0) ? -1 : it.longitude
                // use parent institution if lat/long not defined
                def inst = it.getInstitution()
                if (inst && lat == -1) {lat = inst.latitude}
                if (inst && lon == -1) {lon = inst.longitude}
                // show if matches current filter
                if (showAll || Classification.matchKeywords(it.keywords, params.filters)) {
                    def loc = [type: "Feature"]
                    loc.properties = [
                            name: it.name,
                            entityType: it.ENTITY_TYPE,
                            acronym: it.acronym,
                            uid: it.uid,
                            instName: inst?.name,
                            instUid: inst?.uid,
                            instAcronym: inst?.acronym,
                            isMappable: it.canBeMapped(),
                            address: it.address?.buildAddress(),
                            desc: it.makeAbstract(),
                            url: request.getContextPath() + "/public/show/" + it.uid]
                    loc.geometry = [type: "Point", coordinates: [lon,lat]]
                    locations.features << loc
                }
            }
        }

        //add data providers
        DataProvider.list([sort:"name"]).each {
            // only show ALA partners
            if (it.isALAPartner) {
                // make 0 values be -1
                def lat = (it.latitude == 0.0) ? -1 : it.latitude
                def lon = (it.longitude == 0.0) ? -1 : it.longitude
                // show if matches current filter
                if (showAll || Classification.matchKeywords(it.keywords, params.filters)) {
                    def loc = [type: "Feature"]
                    loc.properties = [
                            name: it.name,
                            entityType: it.ENTITY_TYPE,
                            acronym: it.acronym,
                            uid: it.uid,
                            isMappable: it.canBeMapped(),
                            address: it.address?.buildAddress(),
                            desc: it.makeAbstract(),
                            dataResourceCount: it.resources.size(),
                            url: request.getContextPath() + "/public/show/" + it.uid]
                    loc.geometry = [type: "Point", coordinates: [lon,lat]]
                    locations.features << loc
                }
            }
        }

        render( locations as JSON )
    }

    def chart = {}
    
    /************************************ helpers ***********************************/
    private String limit(str, int length) {
        if (str?.size() > length) {
            return str[0..length] + "... and more."
        }
        return str
    }

    private boolean matchNetwork(pg, filterString) {
        def filters = filterString.tokenize(",")
        for (int i = 0; i < filters.size(); i++) {
            //println "Checking filter ${filters[i]} against network membership ${pg?.networkMembership}"
            if (pg?.isMemberOf(filters[i])) {
                return true;
            }
        }
        return false
    }

    private findCollection(id) {
        if (!id) { return null }
        // try lsid
        if (id instanceof String && id.startsWith(ProviderGroup.LSID_PREFIX)) {
            return Collection.findByGuid(id)
        }
        // try uid
        if (id instanceof String && id.startsWith(Collection.ENTITY_PREFIX)) {
            return Collection.findByUid(id)
        }
        // try id
        try {
            NumberFormat.getIntegerInstance().parse(id)
            def result = Collection.read(id)
            if (result) {return result}
        } catch (Exception e) {}
        // try acronym
        return Collection.findByAcronym(id)
    }

    private findInstitution(id) {
        if (!id) { return null }
        // try lsid
        if (id instanceof String && id.startsWith(ProviderGroup.LSID_PREFIX)) {
            return Institution.findByGuid(id)
        }
        // try uid
        if (id instanceof String && id.startsWith(Institution.ENTITY_PREFIX)) {
            return Institution.findByUid(id)
        }
        // try id
        try {
            NumberFormat.getIntegerInstance().parse(id)
            def result = Institution.read(id)
            if (result) {return result}
        } catch (ParseException e) {}
        // try acronym
        return Institution.findByAcronym(id)
    }

    private String buildBiocacheQueryString(instCodes, collCodes) {
        // must have at least one value to build a query
        if (instCodes || collCodes) {
            def instClause = instCodes ? buildSearchClause("inst", instCodes) : ""
            //println instClause
            def collClause = collCodes ? buildSearchClause("coll", collCodes) : ""
            //println collClause
            def url = grailsApplication.config.biocacheUiURL + "/searchForUID.JSON?pageSize=0" + instClause + collClause
        } else {
            return ""
        }
    }

    private String buildSearchClause(String field, List valueList) {
        def result = ""
        valueList.eachWithIndex {it, i ->
            result += "&${field}=${it}"
        }
        return result
    }


    /**
     * // input of form: [count:1, fieldValue:null1870, prefix:null, label:1870],
     *                   [count:16, fieldValue:null1880, prefix:null, label:1880],
     *                   [count:44, fieldValue:null1890, prefix:null, label:1890]
       // output of form: {"cols":[
            {"id":"","label":"","pattern":"","type":"string"},
            {"id":"","label":"","pattern":"","type":"number"}],
         "rows":[
            {"c":[{"v":"1870","f":null},{"v":1,"f":null}]},
            {"c":[{"v":"1880","f":null},{"v":16,"f":null}]},
            {"c":[{"v":"1890","f":null},{"v":44,"f":null}]}
            ],
        "p":null}
     *
     * @param input
     * @return
     */
    private String buildDecadeDataTable(input) {
        int maximum = 0
        boolean stagger = input.size() > 6
        String result = """{"cols":[{"id":"","label":"","pattern":"","type":"string"},{"id":"","label":"","pattern":"","type":"number"}],"rows":["""
        input.eachWithIndex {it, index ->
            maximum = Math.max(maximum, it.count) as Integer
            String label = (stagger && (index % 2) == 0) ? "" : it.label + "s"
            result += '{"c":[{"v":"' + label + '","f":null},{"v":' + it.count + ',"f":null}]}'
            result += (index == input.size() - 1) ? "" : ","
        }
        result += '],"p":{"max":' + maximum + '}}'
        return result
    }

    /**
     * // input of form:
     * facetResults.fieldResult[fieldName:'occurrence_date']
     *  [
     * {fieldValue: 1850-01-01T12:00:00Z,count: 0,label: 1850-01-01T12:00:00Z,prefix: null},
     * {fieldValue: 1860-01-01T12:00:00Z,count: 0,label: 1860-01-01T12:00:00Z,prefix: null},
     * {fieldValue: 1870-01-01T12:00:00Z,count: 2,label: 1870-01-01T12:00:00Z,prefix: null}
     *  ]
     *
     * // output of form: {"cols":[
     *      {"id":"","label":"","pattern":"","type":"string"},
     *      {"id":"","label":"","pattern":"","type":"number"}],
     *   "rows":[
     *      {"c":[{"v":"1870","f":null},{"v":1,"f":null}]},
     *      {"c":[{"v":"1880","f":null},{"v":16,"f":null}]},
     *      {"c":[{"v":"1890","f":null},{"v":44,"f":null}]}
     *      ],
     *  "p":null}
     *
     * @param input
     * @return
     */
    private String buildDecadeDataTableFromFacetResults(facetResults) {
        def decades = facetResults.find {it.fieldName == "occurrence_date"}
        if (!decades) {
            decades = facetResults.find {it.fieldName == "occurrence_year"}
        }
        def input = decades?.fieldResult
        if (!input) {
            log.warn "Failed to find any decade breakdown. Response= ${facetResults}."
            return ""
        }

        boolean started = false

        // describe columns
        String prefix = """{"cols":[{"id":"","label":"","pattern":"","type":"string"},{"id":"","label":"","pattern":"","type":"number"}],"rows":["""
        String beforeLabel = ""
        int beforeCount = 0

        // build rows
        String records = ""
        input.eachWithIndex {it, index ->
            // don't show the 'before' set
            // don't show decades with no records at the start
            if (it.label != "before" && (it.count > 0 || started)) {

                // build a label from the input date
                String label = it.label[0..3] + "s"

                // grab the label for the first decade shown (first non-zero count)
                if (!records) {beforeLabel = label}

                // put a comma before each record but the first
                records += (records == "") ? "" : ","

                // add the record
                records += '{"c":[{"v":"' + label + '","f":null},{"v":' + it.count + ',"f":null}]}'

                // flag that we have started - so don't skip decades with zero count
                started = true
            }

            // grab the before count
            if (it.label == 'before') {
                beforeCount = it.count
            }
        }

        // check whether there is anything to show
        if (!records) {
            return ""
        }

        // add the before data (if it exists) before the other records
        if (beforeCount) {
            records = '{"c":[{"v":"earlier' + '","f":null},{"v":' + beforeCount + ',"f":null}]},' + records
        }

        // build the table
        return prefix + records + '],"p":null}'
    }

    def stripGenusName(name) {
        def list = name.tokenize(" ")
        if (list.size() > 1) {
            list = list - list[0]
        }
        return list.join(" ")
    }

    /**
     * // input of form: {count:1, label:1870},
     *                   {count:16, label:1880},
     *                   {count:44, label:1890}
     *
     * // output: Two columns. The first column should be a string, and contain the slice label.
     *                         The second column should be a number, and contain the slice value.
     *
     * e.g. {"cols":[
     * {"id":"","label":"Class","pattern":"","type":"string"},{"id":"","label":"No. specimens","pattern":"","type":"number"}],
     * "rows":[
     *  {"c":[{"v":"Insecta","f":null},{"v":2129,"f":null}]},
     *  {"c":[{"v":"Trebouxiophyceae","f":null},{"v":3407,"f":null}]},
     *  {"c":[{"v":"Magnoliopsida","f":null},{"v":859,"f":null}]},
     *  {"c":[{"v":"Diplopoda","f":null},{"v":134,"f":null}]},
     *  {"c":[{"v":"Actinopterygii","f":null},{"v":88,"f":null}]},
     *  {"c":[{"v":"Arachnida","f":null},{"v":54,"f":null}]},
     *  {"c":[{"v":"Malacostraca","f":null},{"v":5,"f":null}]}]
     * "p":null}
     *
     * @param input
     * @param scope the rank of the group being displayed if this is a drill-down
     * @param name of the group being displayed if this is a drill-down
     * @return
     */
    private String buildTaxonChartDataTable(input,scope,name) {
        boolean stripGenus = input.rank == "species" && scope != "all"
        String result = """{"cols":[{"id":"","label":"${input.rank}","pattern":"","type":"string"},{"id":"","label":"No. specimens","pattern":"","type":"number"}],"rows":["""
        def list = input.taxa.collect {
            def label = it.label
            if (stripGenus) {
                label = stripGenusName(label)
            }
            [label: label, count: it.count]
        }
        list.eachWithIndex {it, index ->
            result += '{"c":[{"v":"' + it.label + '","f":null},{"v":' + it.count + ',"f":null}]}'
            result += (index == list.size() - 1) ? "" : ","
        }
        result += '],"p":{"rank":"' + input.rank + '","scope":"' + scope + '","name":"' + name + '"}}'
        return result
    }

    /**
     * Generic conversion for all data in the following form.
     * // input of form: {count:1, label:1870},
     *                   {count:16, label:1880},
     *                   {count:44, label:1890}
     *
     * // output: Two columns. The first column should be a string, and contain the slice label.
     *                         The second column should be a number, and contain the slice value.
     *
     * e.g. {"cols":[
     * {"id":"","label":"Class","pattern":"","type":"string"},{"id":"","label":"No. specimens","pattern":"","type":"number"}],
     * "rows":[
     *  {"c":[{"v":"Insecta","f":null},{"v":2129,"f":null}]},
     *  {"c":[{"v":"Trebouxiophyceae","f":null},{"v":3407,"f":null}]},
     *  {"c":[{"v":"Magnoliopsida","f":null},{"v":859,"f":null}]},
     *  {"c":[{"v":"Diplopoda","f":null},{"v":134,"f":null}]},
     *  {"c":[{"v":"Actinopterygii","f":null},{"v":88,"f":null}]},
     *  {"c":[{"v":"Arachnida","f":null},{"v":54,"f":null}]},
     *  {"c":[{"v":"Malacostraca","f":null},{"v":5,"f":null}]}]
     * "p":null}
     *
     * @param input list of objects of the form described above
     * @param label the type of facet
     * @param parentUid the uid of the overall entity
     * @return Google Viz API dataTable
     */
    private String buildPieChartDataTable(input, label, parentUid) {
        def chartLabel = chartLabels[label] ?: label
        String result = """{"cols":[{"id":"","label":"${chartLabel}","pattern":"","type":"string"},{"id":"","label":"No. specimens","pattern":"","type":"number"}],"rows":["""
        def list = input.collect {
            [label: it.label, count: it.count]
        }
        list.eachWithIndex {it, index ->
            // discard null labels
            if (it.label != 'null') {
                Closure transform = labelTransforms[label]
                def displayLabel = it.label
                if (transform) {
                    displayLabel = transform(it.label)
                }
                result += '{"c":[{"v":"' + it.label + '","f":"' + displayLabel + '"},{"v":' + it.count + ',"f":null}]}'
                result += (index == list.size() - 1) ? "" : ","
            }
        }
        result += '],"p":{"uid":"' + parentUid + '"}}'
        return result
    }

    static labelTransforms = [
        institution_uid: {uid ->  ProviderGroup._get(uid) }
    ]

    static chartLabels = [
        institution_uid: 'institution',
        assertions: 'data assertions'
    ]

    /**
     * Simple 2 column csv showing label and count.
     *
     * @param breakdown
     * @return
     */
    private String buildCsvForTaxonBreakdown(input) {
        def out = new StringWriter()
        def list = input.taxa.collect {
            out << "${it.label},${it.count}\n"
        }
        return out.toString()
    }

    // temp while ws resource nouns are changed
    private String wsEntity(uid) {
        switch (uid[0..1]) {
            case 'co': return 'collections'
            case 'in': return 'institutions'
            case 'dr': return 'dataResources'
            case 'dp': return 'dataProviders'
            case 'dh': return 'dataHubs'
            default: return ""
        }
    }
    private String wsEntityForBreakdown(uid) {
        switch (uid[0..1]) {
            case 'co': return 'collections'
            case 'in': return 'institutions'
            case 'dr': return 'dataResources'
            case 'dp': return 'dataProviders'
            case 'dh': return 'dataHubs'
            default: return ""
        }
    }
    private String fieldNameForSearch(uid) {
        switch (uid[0..1]) {
            case 'co': return 'collection_uid'; break
            case 'in': return 'institution_uid'; break
            case 'dr': return 'data_resource_uid'; break
            case 'dp': return 'data_provider_uid'; break
            case 'dh': return 'data_hub_uid'; break
            default: return ""
        }
    }
}
