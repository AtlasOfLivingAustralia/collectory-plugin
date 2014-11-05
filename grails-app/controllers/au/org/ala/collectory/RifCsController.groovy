package au.org.ala.collectory

import grails.converters.deep.JSON
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class RifCsController {

    def index = {
        def resContentTypes = [:]

        def resBoundingBoxCoords = [:]

        for (res in DataResource.list()) {
            def contentTypesJson = res.contentTypes
            if (contentTypesJson != null) {
                def obj = JSON.parse(contentTypesJson)
                resContentTypes[res.uid] = StringUtils.join(obj, ',')
            }

            // call biocache to get the bounding box for the occurrences in the data resource
            def url = grailsApplication.config.biocacheServicesUrl + "/mapping/bounds?q=data_resource_uid:" + res.uid + "%20AND%20geospatial_kosher:true"

            def conn = new URL(url).openConnection()
            try {
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
                    resBoundingBoxCoords[res.uid] = coords
                }

            } catch (SocketTimeoutException e) {
                log.warn "Timed out looking up bounding box. URL= ${url}."
                def error = [error: "Timed out looking up bounding box count."]
                renderAsJson error
            } catch (Exception e) {
                log.warn "Failed to lookup bounding box. ${e.getClass()} ${e.getMessage()} URL= ${url}."
                def error = ["error": "Failed to lookup bounding box. ${e.getClass()} ${e.getMessage()} URL= ${url}."]
                renderAsJson error
            }

        }

        response.contentType = 'text/xml'

        [providers: DataProvider.list([sort: 'uid']), resources: DataResource.list(sort: 'uid'), resContentTypes: resContentTypes, resBoundingBoxCoords: resBoundingBoxCoords]
    }
}
