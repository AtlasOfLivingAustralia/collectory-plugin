package au.org.ala.collectory

import au.com.bytecode.opencsv.CSVWriter
import grails.converters.JSON
import grails.converters.XML
import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

/**
 * Request a scan and an update of a data provider that links to a GBIF IPT instance.
 */
class IptController {
    static final API_KEY_COOKIE = "ALA-API-Key"

    def collectoryAuthService
    def iptService

    /**
     * Scan an IPT instance described by a data provider and provide a list of datasets that need to be updated.
     * Each dataset is mapped onto an ALA data resource; these can be created automatically during the scan.
     * The data provider uid must be provided. There are two additional optional parameters:
     * <dl>
     *    <dt>create (false)</dt>
     *    <dd> If set to true, then any previously unknown datasets have a data resource created and any existing datasets are updates.</dd>
     *    <dt>check (true)</dt>
     *    <dd>If set to true then only report data resources that need updating</dd>
     *    <dt>key (catalogNumber)</dt>
     *    <dd>The term that is used as a key when </dd>
     * <dt>
     * <p>
     * Authentication is either via standard authentication cookies or by the
     * "ALA-API-Key" cookie that contains a valid API Key. The user is then taken
     * from the API key.
     * <p>
     * Output formats are JSON, XML or plain text (the default). Plain text is a list of updatable data resource ids
     * suitable for feeding into a shell script.
     */
    def scan() {
        def create = params.create != null && params.create.equalsIgnoreCase("true")
        def check = params.check == null || !params.check.equalsIgnoreCase("false")
        def keyName = params.key ?: 'catalogNumber'
        def provider = ProviderGroup._get(params.uid)
        def apiKey = request.cookies.find { cookie -> cookie.name == API_KEY_COOKIE }
        def keyCheck = apiKey ? collectoryAuthService.checkApiKey(apiKey.value) : null
        def username = keyCheck?.userEmail ?: collectoryAuthService.username()
        def admin = keyCheck?.valid || collectoryAuthService.userInRole(ProviderGroup.ROLE_ADMIN)

        log.debug "Access via apikey: ${keyCheck}, user ${username}, admin ${admin}"
        if (create && !admin) {
            render (status: 403, text: "Unable to create resources for " + params.uid)
            return
        }
        if (provider == null) {
            render (status: 400, text: "Unable to get data provider " + params.uid)
            return
        }
        try {
            def updates = provider == null ? null : iptService.scan(provider, create, check, keyName, username, admin)
            log.info "${updates.size()} data resources to update for ${params.uid}"
            response.addHeader HttpHeaders.VARY, HttpHeaders.ACCEPT
            withFormat {
                text {
                    render updates.findAll({ dr -> dr.uid != null }).collect({ dr -> dr.uid }).join("\n")
                }
                xml {
                    render updates as XML
                }
                json {
                    render updates as JSON
                }
            }
        } catch (Exception e){
            log.error("Problem scanning IPT endpoint: " + e.getMessage(), e)
        }
    }

    def syncReport(){

        response.setContentType("text/csv")
        response.setCharacterEncoding("UTF-8")
        response.setHeader("Content-disposition", "attachment;filename=ipt-sync.csv")

        def csvWriter = new CSVWriter(new OutputStreamWriter(response.outputStream))
        def provider = ProviderGroup._get(params.uid)
        if(provider.websiteUrl) {

            def iptInventory = new JsonSlurper().parse(new URL( provider.websiteUrl + "/inventory/dataset"))

            def newMap = [:]
            DataResource.findAll().each { dr ->
                def idx = dr.name.toLowerCase().indexOf("- version")
                if (idx > 0) {
                    def searchedWith = dr.name.substring(0, idx).trim()
                    newMap.put(searchedWith, dr.uid)
                } else {
                    newMap.put(dr.name.toLowerCase(), dr.uid)
                }
            }


            def count = 0
            def iptMap = [:]

            String[] header = [
                    "EML URL",
                    "GUID",
                    "Title",
                    "Number of records in IPT",
                    "Number of records in Atlas",
                    "Atlas ID"
            ]
            csvWriter.writeNext(header)

            iptInventory.registeredResources.each { item ->
                iptMap.put(item.title, item.records)
                //retrieve UID, and do a count from the services
                def uid = newMap.get(item.title)
                if(uid){
                    def jsonCount = new JsonSlurper().parse(new URL(grailsApplication.config.biocacheServicesUrl + "/occurrences/search?pageSize=0&fq=data_resource_uid:" + uid))
                    String[] row = [
                            item.eml,
                            item.gbifKey,
                            item.title,
                            item.records,
                            jsonCount.totalRecords,
                            uid
                    ]
                    csvWriter.writeNext(row)
                } else {String[] row = [item.eml, item.gbifKey, item.title, item.records, "0", "Not registered"]
                    csvWriter.writeNext(row)
                }
                count += 1
            }
            csvWriter.flush()
        }
    }
}