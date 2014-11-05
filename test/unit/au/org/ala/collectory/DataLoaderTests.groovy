package au.org.ala.collectory

import grails.test.*
/**
 * Created by IntelliJ IDEA.
 * User: markew
 * Date: Apr 21, 2010
 * Time: 2:53:20 PM
 * To change this template use File | Settings | File Templates.
 */
class DataLoaderTests extends GrailsUnitTestCase {

    def dataLoaderService

    protected void setUp() {
        super.setUp()
        // need to instantiate explicitly in unit tests
        dataLoaderService = new DataLoaderService()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testBuildLocation() {
        def params = ['longitude': "151.0414080000", 'latitude': "-33.7465780000", 'altitude': "700m"]

        assertEquals "Lat: -33.7465780000 Long: 151.0414080000 Alt: 700m", dataLoaderService.buildLocation(params)

        params.latitude = ""
        assertEquals "Long: 151.0414080000 Alt: 700m", dataLoaderService.buildLocation(params)

        params.altitude = ""
        assertEquals "Long: 151.0414080000", dataLoaderService.buildLocation(params)
        
    }

    void testBuildSize() {
        def params = ['numRecords': '-1', 'size': ""]

        params.size = "200 000"
        assertEquals 200000, dataLoaderService.buildSize(params)

        params.size = "200,000"
        assertEquals 200000, dataLoaderService.buildSize(params)

        params.size = "200.000"
        assertEquals 200000, dataLoaderService.buildSize(params)
    }

    void testRecogniseInstitution() {
        assertEquals 'Commonwealth Scientific and Industrial Research Organisation', dataLoaderService.standardiseInstitutionName('CSIRO')
        assertEquals 'Tasmanian Museum and Art Gallery', dataLoaderService.standardiseInstitutionName('Department of Tasmanian Museum and Art Gallery.')
    }

    void testIsALAPartner() {
        assertTrue dataLoaderService.isALAPartner('Commonwealth Scientific and Industrial Research Organisation')
        assertTrue dataLoaderService.isALAPartner('Australian Museum')
    }

    void testMassageInstitutionType() {

        // use local variant of the method as I can't get constraints to mock correctly
        assertNotNull massageInstitutionType('Government (State/Regional)')
        assertNotNull massageInstitutionType('Museum (General)')
        assertNotNull massageInstitutionType('Government (National/Federal)')
        assertNotNull massageInstitutionType('Natural History Museum (Diverse Collections)')
        assertNull massageInstitutionType('Entomology')
    }

    String massageInstitutionType(String bciType) {
        if (bciType) {
            String type = bciType.toLowerCase()
            if (['aquarium', 'archive', 'botanicGarden', 'conservation', 'fieldStation', 'government', 'herbarium', 'historicalSociety', 'horticulturalInstitution', 'independentExpert', 'industry', 'laboratory', 'library', 'management', 'museum', 'natureEducationCenter', 'nonUniversityCollege', 'park', 'repository', 'researchInstitute', 'school', 'scienceCenter', 'society', 'university', 'voluntaryObserver', 'zoo'].contains(type)) {
                return type
            }
            if (type =~ "government") return "government"
            if (type =~ "museum") return "museum"
            println "Failed to massage institution type: ${bciType}"
        }
        return null
    }
}
