package au.org.ala.collectory

import grails.test.*

class InstitutionCodeLoaderServiceTests extends GrailsUnitTestCase {
    def service

    protected void setUp() {
        super.setUp()
        // need to instantiate explicitly in unit tests
        service = new InstitutionCodeLoaderService()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testLoading() {
        def institutions = new XmlSlurper().parse(new File(InstitutionCodeLoaderService.INPUT_FILE))
        assertNotNull institutions
        assertEquals 929, institutions.tr.size()

    }
    
    void testLookup() {
        assertEquals 'ANU', service.lookupInstitutionCode('Australian National University')
    }
}
