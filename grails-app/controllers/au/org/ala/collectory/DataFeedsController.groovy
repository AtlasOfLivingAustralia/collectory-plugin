package au.org.ala.collectory

class DataFeedsController {
    def rifCsService

    /**
     * Produce a RIF-SC XML file
     * TODO: Use the ANDS RIF-SC Java library instead of GSP
     */
    def index = {
        Map resData = [:]
        Map resContentTypes = [:]
        Map resBoundingBoxCoords = [:]
        List dataResources = rifCsService.getDataResources() // cached

        try {
            resData = rifCsService.getResData(dataResources) // TODO use a Bean instead of a Map to transport data
            resContentTypes = resData.get("resContentTypes")

            if (!grailsApplication.config.rifcs?.excludeBounds) {
                resBoundingBoxCoords = resData.get("resBoundingBoxCoords")
            }
        } catch (Exception e) {
            log.error "Failed to retrieve RIF-CS data: ${e.message}", e
            render(status: 503, text: e.message)
            return
        }

        response.contentType = 'text/xml;charset=UTF-8'

        [   providers: rifCsService.getDataProviders(),
            resources: dataResources,
            resContentTypes: resContentTypes,
            resBoundingBoxCoords: resBoundingBoxCoords
        ]
    }

    /**
     * Produce a RSS feed for data resources.
     * @see <a href="https://www.idigbio.org/wiki/index.php/CYWG_iDigBio_DwC-A_Pull_Ingestion">CYWG
     *   iDigBio DwC-A Pull Ingestion</a>
     * Note: I attempted to the use the Grails "feeds"plugin but it didn't support non-standard tags,
     * such as <umllink> and is no longer maintained.
     */
    def rssFeed = {
        List<DataResource> dataResources = rifCsService.getDataResources("dataCurrency") // cached
        String siteUrl = "${grailsApplication.config.grails.serverURL}"

        Map feed = [
                title: "${grailsApplication.config.skin?.orgNameLong} Collections RSS Feed",
                link: "${siteUrl}",
                description: "${grailsApplication.config.skin?.orgNameLong} data resources RSS feed for iDigBio"
        ]

        List items = []

        dataResources.each { dataResource ->
            if (dataResource.status == "dataAvailable") {
                Date dateUpdated = dataResource.dataCurrency ?: dataResource.dateCreated // fall-back to first loaded date
                String downloadUrl = "${grailsApplication.config.biocacheServicesUrl}/occurrences/index/download?sourceTypeId=0&reasonTypeId=9&file=data-resource-${dataResource.uid}&q=data_resource_uid%3A${dataResource.uid}"
                Map entryMap = [
                        title: dataResource.name, // name/title of resource
                        guid: "${siteUrl}/public/showDataResource/${dataResource.uid}", // public resource page URL
                        link: "${raw(downloadUrl)}", // download link for CSV
                        date: dateUpdated, // processed above
                        description: dataResource.pubDescription, // this can be long so might want to clip?
                        emlLink: "${siteUrl}/eml/${dataResource.uid}" // EML link
                    ]
                items.add(entryMap)
            }
        }

        items.sort { a,b -> b.date <=> a.date } // sort with most recent at top as per RSS convention

        response.contentType = 'application/xml;charset=UTF-8'

        [   feed: feed,
            items: items ]

    }
}
