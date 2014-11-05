package au.org.ala.collectory

import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import org.springframework.core.io.Resource

class SkinningTagLib {
    static namespace = 'sk'

    def grailsApplication
    def GroovyPagesTemplateEngine groovyPagesTemplateEngine

    /**
     * Site header
     */
    def header = { attrs ->
        Map opts = [orgNameLong: grailsApplication.config.skin.orgNameLong, username: "Fred Bare"]
        outputExternalFragment(grailsApplication.config.skin.headerUrl, opts)
    }

    /**
     * Site footer
     */
    def footer = { attrs ->
        outputExternalFragment(grailsApplication.config.skin.footerUrl, [:])
    }

    private outputExternalFragment(String fileUrl, Map options) {
        log.debug "fileUrl = ${fileUrl}"
        Resource res = applicationContext.getResource(fileUrl)
        String sourceJsp = ""
        try {
            sourceJsp = res.getURL().text
        } catch (Exception ex) {
            log.error "Error reading resource: ${ex.localizedMessage}", ex
        }

        groovyPagesTemplateEngine.createTemplate(sourceJsp, fileUrl).make(options).writeTo(out)
    }
}
