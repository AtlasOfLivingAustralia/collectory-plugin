package au.org.ala.collectory

import groovyx.net.http.ContentType

class RifCsController {
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
}
