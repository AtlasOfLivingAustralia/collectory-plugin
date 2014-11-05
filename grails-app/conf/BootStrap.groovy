import grails.converters.JSON
import au.org.ala.custom.marshalling.DomainClassWithUidMarshaller

class BootStrap {
    def grailsApplication
    def authenticateService

    def init = { servletContext ->
        // custom marshaller to put UID into the JSON representation of associations
        //JSON.registerObjectMarshaller( new DomainClassWithUidMarshaller(false, grailsApplication), 2)
    }

    def destroy = {
    }
}