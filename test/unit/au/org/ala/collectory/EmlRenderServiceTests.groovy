package au.org.ala.collectory

import grails.test.*

class EmlRenderServiceTests extends GrailsUnitTestCase {

    def service = new EmlRenderService()

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testService() {
        String eml = service.emlForResource()
        println eml
        assertNotNull eml
    }
}
