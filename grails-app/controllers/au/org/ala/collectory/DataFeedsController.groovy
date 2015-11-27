package au.org.ala.collectory

import org.jdom.Element

class DataFeedsController {
    def rifCsService

    def index = {
        Map resData = [:]
        Map resContentTypes = [:]
        Map resBoundingBoxCoords = [:]
        List dataResources = rifCsService.getDataResources() // cached

        try {
            resData = rifCsService.getResData(dataResources) // TODO use a Bean instead of a Map to transport data
            resContentTypes = resData.get("resContentTypes")
            resBoundingBoxCoords = resData.get("resBoundingBoxCoords")
        } catch (Exception e) {
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

    def rssFeed = {
        List<DataResource> dataResources = rifCsService.getDataResources() // cached
        // http://biocache.ala.org.au/ws/occurrences/index/download?q=data_resource_uid%3Adr968&email=nick.dosremedios@csiro.au&sourceTypeId=0&reasonTypeId=9&file=data-resource-dr968&extra=dataResourceUid,dataResourceName.p,occurrenceStatus
        String downloadUrlPrefix = "${grailsApplication.config.biocacheServicesUrl}/occurrences/index/download?sourceTypeId=0&reasonTypeId=9&file=data-resource-dr968&q=data_resource_uid%3A"  // drcode appended
        String siteUrl = "${grailsApplication.config.grails.serverURL}"

        Map feed = [
                title: "${grailsApplication.config.skin?.orgNameLong} Collections RSS Feed",
                link: "${siteUrl}",
                description: "${grailsApplication.config.skin?.orgNameLong} data resources RSS feed for iDigBio"
        ]

        List items = []

        dataResources.each { dataResource ->
            if (dataResource.status == "dataAvailable") {
                Date dateUpdt = new Date(dataResource.dataCurrency?.getTime() ?: 1)
                //String pubDate =
                Map entryMap = [
                        title: dataResource.name, // name/title of resource
                        guid: "${siteUrl}/public/showDataResource/${dataResource.uid}", // public resource page URL
                        link: "${raw(downloadUrlPrefix)}${dataResource.guid}", // download link for CSV
                        date: dateUpdt, // some have a null dataCurrency value so fail-over to Jan 1 1970
                        description: dataResource.pubDescription, // causes errors due to GString ?? removing for now,
                        emlLink: "${siteUrl}/eml/${dataResource.uid}" // EML link
                    ]
                items.add(entryMap)
            }
        }

        response.contentType = 'application/xml;charset=UTF-8'

        [   feed: feed,
            items: items ]

    }
}
