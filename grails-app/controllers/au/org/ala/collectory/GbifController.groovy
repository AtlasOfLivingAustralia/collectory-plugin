package au.org.ala.collectory

import au.org.ala.collectory.resources.gbif.GbifRepatDataSourceAdapter
import grails.converters.JSON
import groovy.json.JsonSlurper

class GbifController {
    static final API_KEY_COOKIE = "ALA-API-Key"

    def collectoryAuthService
    def gbifRegistryService
    def gbifService
    def authService
    def grailsApplication
    def externalDataService

    def healthCheck() {
        gbifRegistryService.generateSyncBreakdown()
    }

    def healthCheckLinked() {

        log.info("Starting report.....")
        def url = grailsApplication.config.biocacheServicesUrl + "/occurrences/search?q=*:*&facets=data_resource_uid&pageSize=0&facet=on&flimit=-1"
        def js = new JsonSlurper()
        def biocacheSearch = js.parse(new URL(url), "UTF-8")

        def dataResourcesWithData = [:]
        def shareable = [:]
        def licenceIssues = [:]
        def notShareable = [:]
        def providedByGBIF = [:]
        def linkedToDataProvider = [:]
        def linkedToInstitution = [:]

        biocacheSearch.facetResults[0].fieldResult.each { result ->
            def uid = result.fq.replaceAll("\"","").replaceAll("data_resource_uid:","")

            def isShareable = true

            //retrieve current licence
            def dataResource = DataResource.findByUid(uid)
            if(dataResource) {

                dataResourcesWithData[dataResource] = result.count

                //get the data provider if available...
                def dataLinks = DataLink.findAllByProvider(uid)
                def institutionDataLink
                def linked = false

                if(dataLinks){
                    //do we have institution link ????
                    institutionDataLink = dataLinks.find { it.consumer.startsWith("in")}
                    if(institutionDataLink){
                        //we have an institution
                        linkedToInstitution[dataResource] = result.count
                        linked = true
                    }
                }

                if(!institutionDataLink) {
                    def dataProvider = dataResource.getDataProvider()
                    if(!dataProvider){
                        isShareable = false //no institution and no data provider
                    } else {
                        linkedToDataProvider[dataResource] = result.count
                        linked = true
                    }
                }

                if(linked) {

                    if (dataResource.licenseType == null || !dataResource.licenseType.startsWith("CC")) {
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
                    }
                }
            }
        }

        [
                dataResourcesWithData:dataResourcesWithData,
                shareable:shareable,
                licenceIssues:licenceIssues,
                notShareable:notShareable,
                providedByGBIF:providedByGBIF,
                linkedToDataProvider: linkedToDataProvider,
                linkedToInstitution: linkedToInstitution,
        ]

    }

    /**
     * Download CSV report of our ability to share resources with GBIF.
     *
     * @return
     */
    def downloadCSV() {
        response.setContentType("text/csv")
        response.setHeader("Content-disposition", "attachment;filename=gbif-healthcheck.csv")
        gbifRegistryService.writeCSVReportForGBIF(response.outputStream)
    }

    def syncAllResources(){
        log.info("Starting all sync resources...checking user has role ${grailsApplication.config.gbifRegistrationRole}")
        def results = [:]
        def errorMessage = ""

        if (authService.userInRole(grailsApplication.config.gbifRegistrationRole)){
            results = gbifRegistryService.syncAllResources()
        } else {
            errorMessage = "User does not have sufficient privileges to perform this."
        }
        [results:results, errorMessage:errorMessage]
    }

    def scan(){

        def apiKey = request.cookies.find { cookie -> cookie.name == API_KEY_COOKIE }
        if (!apiKey){
            // look in the standard place - http apiKey param
            apiKey = params.apiKey
        }
        def keyCheck =  collectoryAuthService.checkApiKey(apiKey.value)

        if (!keyCheck || !keyCheck.valid){
            response.sendError(401)
            return
        }

        DataProvider dataProvider = DataProvider.findByUid(params.uid)
        if (!dataProvider){
            response.sendError(404)
            return
        }

        def resources = dataProvider.resources
        def output = []
        resources.each { DataResource resource ->
            Date lastUpdated = gbifService.getGbifDatasetLastUpdated(resource.guid)
            //get last updated data
            output << [uid:resource.uid,
                       name: resource.name,
                       lastUpdated: resource.lastUpdated,
                       guid: resource.guid,
                       country: resource.repatriationCountry,
                       pubDate: lastUpdated,
                       inSync:  !(lastUpdated > resource.lastUpdated)
            ]
        }

        DataSourceConfiguration configuration = new DataSourceConfiguration()
        configuration.adaptorClass = GbifRepatDataSourceAdapter.class
        configuration.endpoint = new URL(grailsApplication.config.gbifApiUrl)
        configuration.username = grailsApplication.config.gbifApiUser
        configuration.password = grailsApplication.config.gbifApiPassword

        def externalResourceBeans = []

        output.each { res ->
            if (!res.inSync && res.guid){
                res.status = "RELOADING"
                externalResourceBeans << new ExternalResourceBean(
                        uid: res.uid, guid: res.guid, name: res.name, country: res.country, updateMetadata:true, updateConnection:true)
            } else {
                res.status = "IN_SYNC"
            }
        }
        configuration.resources = externalResourceBeans

        def loadGuid = UUID.randomUUID().toString()

        log.info("Reloading process ID " + loadGuid)
        externalDataService.updateFromExternalSources(configuration, loadGuid)

        def fullOutput =
                [loadGuid: loadGuid,
                 trackingUrl: createLink(controller:"manage", action:"externalLoadStatus", params: [loadGuid: loadGuid]),
                 resources: output
                ]
        render(fullOutput as JSON)
    }
}
