package au.org.ala.collectory

import org.jdom.Document
import org.jdom.Element
import org.jdom.Namespace
import org.jdom.output.XMLOutputter

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

        render(feedType:"rss", feedVersion:"2.0") {
            title = "${grailsApplication.config.skin?.orgNameLong} Collections RSS Feed"
            link = "${siteUrl}"
            description = "${grailsApplication.config.skin?.orgNameLong} data resources RSS feed for iDigBio"

            dataResources.each { dataResource ->
                if (dataResource.status == "dataAvailable") {

                    //Namespace ns = new Namespace("ipt", "http://ipt.gbif.org/")
                    //Element emlOjb = new Element("eml", ns).setText("${siteUrl}/eml/${dataResource.uid}")
                    Element emlObj = new Element("emllink").setText("${siteUrl}/eml/${dataResource.uid}")
                    Element idObj = new Element("id").setText("${siteUrl}/public/showDataResource/${dataResource.uid}")
                    //Element root = new Element("root").addContent([emlObj,idObj])
                    //Document doc = new Document(root);
                    //log.debug "doc = " + new XMLOutputter().outputString(doc);
                    //feed.setForeignMarkup(Arrays.asList(emlOjb, idObj));
                    //List foreignObj = [idObj, emlObj]

                    Map entryMap = [
                            title: dataResource.name,
                            foreignMarkup: Arrays.asList(idObj, emlObj),
                            uri:  "${siteUrl}/public/showDataResource/${dataResource.uid}"
                            //description: dataResource.pubDescription,
                    ]

                    //log.debug "entryMap = ${entryMap}"

                    entry(entryMap) {
                        // link to CSV download
                        link = "${raw(downloadUrlPrefix)}${dataResource.guid}"
                        // publication date - "EEEEE, MMMMM dd, yyyy hh:mm a"
                        //publishedDate = new SimpleDateFormat("EEEEE, MMMMM dd, yyyy hh:mm a").parse(dataResource.dataCurrency).toTimestamp()  // new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(dataCurrency).toTimestamp()
                        publishedDate = new Date(dataResource.dataCurrency?.getTime()?:1)
                        // other properties
                        //foreignMarkup = [emlObj, idObj]

//                        // ID - http://collections.ala.org.au/public/showDataResource/
//                        guid = "${siteUrl}/public/showDataResource/${dataResource.uid}"
//                        // Description
//                        description = "${dataResource.pubDescription}"
//                        // EML link - http://collections.ala.org.au/eml/dr820
//                        emllink = "${siteUrl}/eml/${dataResource.uid}"

                    }

                }

            }
        }
    }
}
