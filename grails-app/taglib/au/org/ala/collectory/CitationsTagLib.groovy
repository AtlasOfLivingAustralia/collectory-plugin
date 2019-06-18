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
                    out << """<a class="btn btn-default" href="${grailsApplication.config.gbif.citation.search}${attrs.gbifRegistryKey}">&nbsp;<span class="glyphicon glyphicon-bullhorn"></span>&nbsp; ${data.count} ${g.message(code:"citations.available", default:"citations for these data")}</a>"""
                }
            }
        } catch (Exception e){
            log.error("Problem retrieving citation count from GBIF" + e.getMessage(), e)
        }
    }

    /**
     * Convert the gbifDoi value to a DOI URL (link)
     *
     * @attr gbifDoi REQUIRED the gbifDoi value
     */
    def doiLink = { attrs, body ->
        String gbifDoi = attrs.gbifDoi as String
        String doiUrl

        if (gbifDoi.startsWith("doi")) {
            // Old GBIF DOI API used a "doi:" prefix
            doiUrl = "https://${gbifDoi.replaceAll('doi:', 'doi.org/')}"
        } else {
            // New GBIF DOI API provides the DOI path only
            doiUrl = "https://doi.org/${gbifDoi}"
        }

        out << doiUrl
    }
}
