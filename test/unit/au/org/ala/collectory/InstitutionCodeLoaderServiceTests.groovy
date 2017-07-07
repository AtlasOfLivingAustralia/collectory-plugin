package au.org.ala.collectory

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(InstitutionCodeLoaderService)
class InstitutionCodeLoaderServiceTests extends Specification {
    static doWithConfig = { c ->
        c.institution.codeLoaderURL = InstitutionCodeLoaderServiceTests.getResource("institution-codes-1.xml").toString()
    }

    void testLoading() {
        when:
        def institutions = new XmlSlurper().parse(new URL(service.grailsApplication.config.institution.codeLoaderURL).openStream())
        then:
        institutions != null
        institutions.tr.size() == 2

    }
    
    void testLookup() {
        expect:
        def code = service.lookupInstitutionCode('Australian National University')
        code == "ANU"
    }
}
