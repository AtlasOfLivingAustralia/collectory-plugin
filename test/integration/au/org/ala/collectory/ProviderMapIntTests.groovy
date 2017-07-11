package au.org.ala.collectory

import grails.test.mixin.integration.Integration
import spock.lang.Specification

/**
 * Created by markew
 * Date: Jul 30, 2010
 * Time: 9:17:23 AM
 */
@Integration
class ProviderMapIntTests extends Specification {

    def grailsApplication
    
    ProviderCode code1, code2, code3
    Collection pg1

    def setup() {
        code1 = createIfRequired('ProviderCode', [code:'code1'], ProviderCode.findByCode('code1')) as ProviderCode
        assert code1 != null
        code2 = createIfRequired('ProviderCode', [code:'code2'], ProviderCode.findByCode('code2')) as ProviderCode
        assert code2 != null
        code3 = createIfRequired('ProviderCode', [code:'code3'], ProviderCode.findByCode('code3')) as ProviderCode
        assert code3 != null
        pg1 = createIfRequired('Collection', [uid:"co12", name:'collection1', userLastModified:'test'], Collection.findByUid("co12")) as Collection
    }


    def testContains() {
        expect:
        ([code1,code2,code3].collect{it.code}.contains("code1"))
    }

    def testMatches() {
        when:
        ProviderMap pm = new ProviderMap(collection: pg1)
        pm.addToInstitutionCodes(code1)
        pm.addToCollectionCodes(code2)
        pm.addToCollectionCodes(code3)
        then:
        def cCodes = pm.getCollectionCodes()
        cCodes.size() == 2
        cCodes.collect{it.code}.contains("code2")
        pm.matches("code1", "code2")
        pm.matches("code1", "code3")
        !pm.matches("code2", "code3")
        cleanup:
        pm.discard()
    }

    def testFindMatch() {
        when:
        ProviderMap pm = new ProviderMap(collection: pg1)
        pm.addToInstitutionCodes(code1)
        pm.addToCollectionCodes(code2)
        pm.addToCollectionCodes(code3)
        pm.save(flush:true, failOnError: true)
        then:
        ProviderMap.count() == 1
        def pg = ProviderMap.findMatch("code1", "code2")
        pg != null
        pg.name == "collection1"
        ProviderMap.findMatchUid("code1", "code2") == 'co12'
    }

    /**
     * This shouldn't be necessary as in-mem db is meant to be cleared for each test. However..
     */
    def createIfRequired(domain, props, exists) {
        if (exists) return exists
        def result = grailsApplication.getDomainClass('au.org.ala.collectory.' + domain).newInstance()
        result.properties = props
        result.save(flush:true, failOnError: true)
        return result
    }
}
