package au.org.ala.collectory

import grails.converters.deep.JSON
import grails.plugin.cache.Cacheable
import grails.transaction.Transactional
import org.apache.commons.lang.StringUtils

@Transactional
class RifCsService {
    def grailsApplication

    /**
     * Get content types and bounding boxes for data resources
     *
     * @return
     */
    @Cacheable('longTermCache')
    Map getResData(List<DataResource> dataResources) {
        log.debug "starting getResData()"
        Map resData = [:]
        Map resContentTypes = [:]
        Map resBoundingBoxCoords = [:]

        for (res in dataResources) {
            // content types
            def contentTypesJson = res.contentTypes
            if (contentTypesJson != null) {
                def obj = JSON.parse(contentTypesJson)
                Map tempMap = [:]
                resContentTypes[res.uid] = StringUtils.join(obj, ',')
                //resData.put("resContentTypes", tempMap)
            }

            // call biocache to get the bounding box for the occurrences in the data resource
            def url = grailsApplication.config.biocacheServicesUrl + "/mapping/bounds?q=data_resource_uid:" + res.uid + "%20AND%20geospatial_kosher:true"

            try {
                def conn = new URL(url).openConnection()
                Map tempMap2 = [:]
                conn.setConnectTimeout(10000)
                conn.setReadTimeout(50000)
                def bboxJson = conn.content.text
                def coords = JSON.parse(bboxJson)
                def minLong = coords[0]
                def minLat = coords[1]
                def maxLong = coords[2]
                def maxLat = coords[3]

                // If all the bounding box coordinates are zero, do not output a bounding box for this data resource. The
                // data resource either has no occurrence records associated with it, or has no records with valid location information.
                if (!(minLong == 0 && minLat == 0 && maxLong == 0 && maxLat == 0)) {
                    log.debug "resBoundingBoxCoords => ${res.uid}"
                    resBoundingBoxCoords[res.uid] = coords
                    //resData.put("resBoundingBoxCoords", tempMap2)
                }
            } catch (Exception e) {
                log.warn "Error looking up data resource bounding box in biocache - ${e.localizedMessage}"
            }

        }

        resData.put("resContentTypes", resContentTypes)
        resData.put("resBoundingBoxCoords", resBoundingBoxCoords)
        //log.debug "resData = ${resData}"

        resData
    }

    @Cacheable('longTermCache')
    def getDataProviders() {
        DataProvider.list([sort: 'uid'])
    }

    @Cacheable('longTermCache')
    def getDataResources(String sort) {
        String sortField = "${sort?:'uid'}"
        DataResource.list([sort: sortField])
    }
}
