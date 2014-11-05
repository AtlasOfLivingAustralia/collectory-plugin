package au.org.ala.collectory

/**
 * Created by markew
 * Date: Jul 30, 2010
 * Time: 9:17:23 AM
 */
class ProviderMapIntTests extends GroovyTestCase {

    def grailsApplication
    
    ProviderCode code1, code2, code3
    Collection pg1

    protected void setUp() {
        super.setUp()
        code1 = createIfRequired('ProviderCode', [code:'code1'], ProviderCode.findByCode('code1')) as ProviderCode
        assertNotNull code1
        code2 = createIfRequired('ProviderCode', [code:'code2'], ProviderCode.findByCode('code2')) as ProviderCode
        assertNotNull code2
        code3 = createIfRequired('ProviderCode', [code:'code3'], ProviderCode.findByCode('code3')) as ProviderCode
        assertNotNull code3

        pg1 = createIfRequired('Collection', [uid:"co12", name:'collection1', userLastModified:'test'], Collection.findByUid("co12")) as Collection

    }

    protected void tearDown() {
        super.tearDown()
    }

    void testContains() {
        assertTrue ([code1,code2,code3].collect{it.code}.contains("code1"))
    }

    void testMatches() {
        ProviderMap pm = new ProviderMap(collection: pg1)
        pm.addToInstitutionCodes(code1)
        pm.addToCollectionCodes(code2)
        pm.addToCollectionCodes(code3)

        def cCodes = pm.getCollectionCodes()
        assertEquals 2, cCodes.size()
        assertTrue cCodes.collect{it.code}.contains("code2")

        assertTrue pm.matches("code1", "code2")
        assertTrue pm.matches("code1", "code3")
        assertFalse pm.matches("code2", "code3")

        pm.discard()
    }

    void testFindMatch() {
        ProviderMap pm = new ProviderMap(collection: pg1)
        pm.addToInstitutionCodes(code1)
        pm.addToCollectionCodes(code2)
        pm.addToCollectionCodes(code3)
        pm.save(flush:true)

        assertEquals 1, ProviderMap.count()
        def pg = ProviderMap.findMatch("code1", "code2")
        assertNotNull pg
        assertEquals "collection1", pg.name
        assertEquals 'co12', ProviderMap.findMatchUid("code1", "code2")
    }

    // def targetObjects = propertyClass."findBy${idName}"(idValue)

    /**
     * This shouldn't be necessary as in-mem db is meant to be cleared for each test. However..
     */
    def createIfRequired(domain, props, exists) {
        if (exists) return exists
        def result = grailsApplication.getDomainClass('au.org.ala.collectory.' + domain).newInstance()
        result.properties = props
        result.save(flush:true)
        if (result.hasErrors()) {
            result.errors.each { println it }
        }
        println result
        return result
    }
}
