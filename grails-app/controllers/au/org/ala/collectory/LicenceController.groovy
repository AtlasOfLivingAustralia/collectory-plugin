package au.org.ala.collectory

import grails.converters.JSON

/**
 * Simple webservice providing support licences in the system.
 */
class LicenceController {

    def index() {
        response.setContentType("application/json")
        render (Licence.findAll().collect { [name:it.name, url:it.url] } as JSON)
    }
}
