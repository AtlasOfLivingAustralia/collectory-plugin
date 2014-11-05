package au.org.ala.collectory

import grails.test.*

class ProviderMapTests extends GrailsUnitTestCase {

    ProviderMap map
    ProviderCode code1
    ProviderCode code2
    ProviderCode code3
    ProviderGroup pg1

    protected void setUp() {
        super.setUp()
        mockDomain ProviderCode
        mockDomain ProviderMap
        code1 = new ProviderCode(code:'code1')
        code2 = new ProviderCode(code:'code2')
        code3 = new ProviderCode(code:'code3')
        map = new ProviderMap()
        map.addToInstitutionCodes(code1)
        map.addToCollectionCodes(code2)
        map.addToCollectionCodes(code3)
        pg1 = new Collection(id:12, name:'pg name')
        map.collection = pg1
        map.save()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testContains() {
        assertTrue ([code1,code2,code3].collect{it.code}.contains("code1"))
    }
    
    void testMatches() {
        def cCodes = map.getCollectionCodes()
        assertEquals 2, cCodes.size()
        assertTrue cCodes*.code.contains("code2")

        assertTrue map.matches("code1", "code2")
        assertTrue map.matches("code1", "code3")
        assertFalse map.matches("code2", "code3")
    }

}
