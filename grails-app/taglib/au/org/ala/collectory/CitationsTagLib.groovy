package au.org.ala.collectory

import groovy.json.JsonSlurper

class CitationsTagLib {

    static namespace = 'citations'

    def grailsApplication

    def gbifLink = { attrs ->
        def gbifUrl = """${grailsApplication.config.gbif.citation.lookup}${attrs.gbifRegistryKey}"""
        try {
            if(grailsApplication.config.gbif.citations.enabled.toBoolean()) {
                def js = new JsonSlurper()
                def data = js.parse(new URL(gbifUrl))
                if (data.count) {
                    out << """<a class="btn btn-default" href="${grailsApplication.config.gbif.citation.search}${attrs.gbifRegistryKey}"><span class="glyphicon glyphicon glyphicon-pencil"> </span> ${data.count} ${g.message(code:"citations.available", default:"citations for these data")}</a>"""
                }
            }
        } catch (Exception e){
            log.error("Problem retrieving citation count from GBIF" + e.getMessage(), e)
        }
    }
}
