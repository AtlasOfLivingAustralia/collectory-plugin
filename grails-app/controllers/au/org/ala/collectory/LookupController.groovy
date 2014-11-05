package au.org.ala.collectory

import java.text.ParseException
import java.text.NumberFormat
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import au.com.bytecode.opencsv.CSVWriter

import groovy.xml.StreamingMarkupBuilder
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

class LookupController {

    def idGeneratorService

    static allowedMethods = [citation:['POST','GET']]

    def index = { }

    def collection = {
        def inst = params.inst
        def coll = params.coll
        if (!inst && !coll) {
            // do standard entity lookup
            renderEntitySummaries(Collection, params.id)
        }
        else {
            // do coll/inst code lookup
            if (!inst) {
                def error = ["error":"must specify an institution code as parameter inst"]
                render error as JSON
            }
            if (!coll) {
                def error = ["error":"must specify a collection code as parameter coll"]
                render error as JSON
            }
            Collection col = ProviderMap.findMatch(inst, coll)
            if (col) {
                render col.buildSummary() as JSON
            } else {
                def error = ["error":"unable to find collection with inst code = ${inst} and coll code = ${coll}"]
                render error as JSON
            }
        }
    }

    def institution = {
        renderEntitySummaries(Institution, params.id)
    }

    def dataProvider = {
        renderEntitySummaries(DataProvider, params.id)
    }

    def dataResource = {
        renderEntitySummaries(DataResource, params.id)
    }

    def renderEntitySummaries(domain, uid) {
        def pg = null
        if (uid) {
            // summary for single entity
            pg = ProviderGroup._get(uid)
            if (pg) {
                render pg.buildSummary() as JSON
            } else {
                log.error "Unable to find entity. id = ${params.id}"
                def error = ["error":"unable to find entity - id = ${params.id}"]
                render error as JSON
            }
        } else {
            // return summaries for all
            def list = []
            domain.list().each {
                list << it.buildSummary()
            }
            render list as JSON
        }
    }

    /**
     * Returns a summary for any entity when passed a UID.
     *
     * If the id is not a UID it will be assumed to be a collection and will be treated as:
     * 1. lsid if it starts with uri:lsid:
     * 2. database id if it is a number
     * 3. acronym if it matches a collection
     */
    def summary = {
        log.debug "debugging summary: request from ${request.remoteAddr} for ${params.id}"
        def instance
        if (params.id?.startsWith('drt')) {
            instance = TempDataResource.findByUid(params.id)
        }
        else {
            instance = ProviderGroup._get(params.id)
        }
        if (!instance) {
            instance = findCollection(params.id)
        }
        if (instance) {
            render instance.buildSummary() as JSON
        } else {
            // we would normally log this as a warning but it occurs too often
            //log.warning "Unable to find entity for id = ${params.id}"
            def error = ["error":"unable to find an entity for id = " + params.id]
            render error as JSON
        }
    }

    /**
     * Returns the name for the entity with the passed UID.
     */
    def name = {
        String uid = params.id
        def result
        if (uid && uid.size() > 2) {
            def nm = ''
            def prefix = uid.startsWith('drt') ? 'drt' : uid[0..1]
            switch (prefix) {
                case 'co': nm = Collection.executeQuery("select c.name from Collection c where c.uid = ?",[uid]); break
                case 'in': nm = Collection.executeQuery("select c.name from Institution as c where c.uid = ?",[uid]); break
                case 'dp': nm = Collection.executeQuery("select c.name from DataProvider as c where c.uid = ?",[uid]); break
                case 'dr': nm = Collection.executeQuery("select c.name from DataResource as c where c.uid = ?",[uid]); break
                case 'drt': nm = Collection.executeQuery("select c.name from TempDataResource as c where c.uid = ?",[uid]); break
                case 'dh': nm = Collection.executeQuery("select c.name from DataHub as c where c.uid = ?",[uid]); break
            }
            if (nm) {
                result = [name: nm[0]]
            } else {
                result = ["error":"uid not found: " + params.id]
            }
        } else {
            result = ["error":"invalid uid = " + params.id]
        }
        render result as JSON
    }

    /**
     * Returns a list of rank:name pairs that describe the expected taxonomic range of the entity.
     *
     * @return a JSON list of rank:name pairs - may be empty
     */
    def taxonomyCoverageHints = {
        ProviderGroup instance = ProviderGroup._get(params.id)
        if (instance) {
            render JSONHelper.taxonomyHints(instance.taxonomyHints) as JSON
        } else {
            log.error "Unable to find entity for id = ${params.id}"
            def error = ["error":"unable to find an entity for id = " + params.id]
            render error as JSON
        }
    }

    def downloadLimits = {
        def list = DataResource.list(sort: "uid").findAll({it.downloadLimit > 0}).collect {
            [uid: it.uid,
            name: it.name,
            downloadLimit: it.downloadLimit]
        }
        render list as JSON
    }

    def citations = {
        if (params.include) {
            params.uids = "[${params.include}]"
        }
        forward(action: 'citation')
    }

    def citation = {
        // input is a json object of the form ['co123','in23','dp45']
        def uids = null
        response.addHeader HttpHeaders.VARY, HttpHeaders.ACCEPT
        switch (request.method) {
            case "POST":
                if (request.JSON) { uids = request.JSON }
                break;
            case "GET":
                if (params.uids) { uids = JSON.parse(params.uids) }
                break;
        }
        if (uids) {
            if (uids.size() > 0 && uids[0] == "all") {
                uids = DataResource.list(sort: "uid").collect { it.uid }
            }
            withFormat {
                text {  // handles text/plain
                    render csvCitations(uids)
                }
                csv {  // same as text - handles text/csv
                    response.addHeader HttpHeaders.CONTENT_TYPE, 'text/csv'
                    render csvCitations(uids)
                }
                tsv {  // old
                    response.addHeader HttpHeaders.CONTENT_TYPE, 'text/tsv'
                    String result = "Resource name\tCitation\tRights\tMore information\tData generalizations\tInformation withheld\tDownload limit\tUID"
                    uids.each {
                        // get each pg
                        def pg = it.startsWith('drt') ? TempDataResource.findByUid(it) : ProviderGroup._get(it)
                        if (pg) {
                            result += "\n" + buildCitation(pg,"tab separated")
                        }
                    }
                    render result
                }
                json {
                    def result = uids.collect {
                        // get each pg
                        def pg = it.startsWith('drt') ? TempDataResource.findByUid(it) : ProviderGroup._get(it)
                        if (pg) {
                            return buildCitation(pg,"map")
                        }
                    }
                    render result as JSON
                }
                all {  // handles text/html
                    render csvCitations(uids)
                }
            }
        } else {
            render ([error:"no uids posted"] as JSON)
        }
    }

    def csvCitations(uids) {
        StringWriter sw = new StringWriter()
        CSVWriter writer = new CSVWriter(sw)
        writer.writeNext(["Resource name","Citation","Rights","More information", "Data generalizations", "Information withheld","Download limit","UID"] as String[])
        uids.each {
            def pg = it.startsWith('drt') ? TempDataResource.findByUid(it) : ProviderGroup._get(it)
            if (pg) {
                writer.writeNext(buildCitation(pg,"array") as String[])
            }
        }
        return sw.toString()
    }

    def testCitation = {
        def data = URLEncoder.encode("['co123','in23','dp45']","UTF-8")

        def url = new URL("http://localhost:8080/Collectory/admin/citation")
        def conn = url.openConnection()
        conn.requestMethod = "POST"

        conn.setDoOutput(true)
        conn.setDoInput(true)
        def writer = new OutputStreamWriter(conn.getOutputStream())
        writer.write(data)
        writer.flush()

        def result = conn.content.text

        writer.close()
        conn.disconnect()

        render (result) as JSON
    }

    def buildCitation(pg, format) {
        def citation = ConfigurationHolder.config.citation.template
        def rights = ConfigurationHolder.config.citation.rights.template
        def name = pg.name
        def dataGen = ''
        def infoWithheld = ''
        def downloadLimit = ''
        if (pg instanceof DataResource) {
            def cit = pg.getCitation()
            citation = cit ? cit : citation
            def rit = ""
            if (pg.creativeCommons) {
                def display = DataResource.ccDisplayList.find { it.type == pg.licenseType }
                rit = "${display.display} ${pg.licenseVersion} ${grailsApplication.config.regionName} (${pg.licenseType})"
            }
            if (pg.getRights()) {
                rit = rit ? rit + " " + pg.getRights() : pg.getRights()
            }
            rights = rit ? rit : rights
            def dg = pg.getDataGeneralizations()
            dataGen = dg ?: dataGen
            def ih = pg.getInformationWithheld()
            infoWithheld = ih ?: infoWithheld
            downloadLimit = pg.downloadLimit ?: ""
        }
        if (pg instanceof TempDataResource) {
            citation = "This is a temporary data set"
            rights = "No explicit rights"
        }
        else {
            citation =  citation.replaceAll("@entityName@",name)
        }
        def link = ConfigurationHolder.config.citation.link.template
        link =  link.replaceAll("@link@",makeLink(pg.uid))
        switch (format) {
            case "tab separated": return "${name}\t${citation}\t${rights}\t${link}\t${dataGen}\t${infoWithheld}\t${downloadLimit}\t${pg.uid}"
            case "map": return ['name': name, 'citation': citation, 'rights': rights, 'link': link,
                'dataGeneralizations': dataGen, 'informationWithheld': infoWithheld, 'downloadLimit': downloadLimit, 'uid': pg.uid]
            case "array": return [name, citation, rights, link, dataGen, infoWithheld, downloadLimit, pg.uid]
        }
    }

    def findResourceByGuid = {
        response.addHeader HttpHeaders.CONTENT_TYPE, 'application/json'
        def result = []
        if(params.guid) {
            def dr = DataResource.findAllByGuid(params.guid)
            if(dr){
               result << dr
            }
        }
        render result as JSON
    }

    def listResources = {
        response.addHeader HttpHeaders.CONTENT_TYPE, 'text/csv'
        Writer w = response.getWriter()
        CSVWriter wr = new CSVWriter(w)
        String[] header = ['uid','name']
        wr.writeNext(header)
        def list = params.id ? DataResource.findAllByResourceType(params.id, [sort:'name']) : DataResource.list([sort:'name'])
        list.each {
            String[] values = [it.uid, it.name]
            wr.writeNext values
        }
        wr.close()
        render ""
    }

    String makeLink(uid) {
        return "${ConfigurationHolder.config.grails.serverURL}/public/show/${uid}"
    }

    private findInstitution(id) {
        // try lsid
        if (id instanceof String && id.startsWith('urn:lsid:')) {
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

    private ProviderGroup findCollection(id) {
        if (!id) {
            return null
        }
        // try lsid
        if (id instanceof String && id.startsWith('urn:lsid:')) {
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
        } catch (ParseException e) {}
        // try acronym
        return Collection.findByAcronym(id)
    }

    def generateCollectionUid = {
        def resultMap = ['uid':idGeneratorService.getNextCollectionId()]
        render resultMap as JSON
    }

    def generateInstitutionUid = {
        def resultMap = ['uid':idGeneratorService.getNextInstitutionId()]
        render resultMap as JSON
    }

    def generateDataProviderUid = {
        def resultMap = ['uid':idGeneratorService.getNextDataProviderId()]
        render resultMap as JSON
    }

    def generateDataResourceUid = {
        def resultMap = ['uid':idGeneratorService.getNextDataResourceId()]
        render resultMap as JSON
    }

    def generateDataHubUid = {
        def resultMap = ['uid':idGeneratorService.getNextDataHubId()]
        render resultMap as JSON
    }

    def sitemap = {
        def xml = new StreamingMarkupBuilder()
        xml.encoding = "UTF-8"
        response.contentType = 'text/xml'
        render xml.bind {
            mkp.xmlDeclaration()
            urlset(xmlns: "http://www.sitemaps.org/schemas/sitemap/0.9") {
                url {
                    loc(g.createLink(absolute: true, controller: 'public', action: 'map'))
                    changefreq('weekly')
                    priority(1.0)
                }
                [Collection, Institution, DataProvider, DataResource].each {
                    it.list().each {domain->
                        url {
                            loc(g.createLink(absolute: true, controller: 'public', action: 'show', id: domain.uid))
                            changefreq('weekly')
                        }
                    }
                }
            }
        }.toString()
    }
}