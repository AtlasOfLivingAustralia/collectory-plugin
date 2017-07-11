package au.org.ala.collectory

import grails.test.mixin.TestFor
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification

@Integration
class CollectionIntTests extends Specification {
    void testCreation() {
        when:
        def c1 = new Collection(name:'Collection 1', uid:'co01',states: 'ACT',userLastModified:'test')
        c1.save(flush:true, failOnError: true)
        def c2 = new Collection(name:'Collection 2', uid:'co02',userLastModified:'test').save(flush:true, failOnError: true)
        def c3 = new Collection(name:'Collection 3', uid:'co03',userLastModified:'test').save(flush:true, failOnError: true)
        def c4 = new Collection(name:'Collection 4', uid:'co04',userLastModified:'test').save(flush:true, failOnError: true)
        def i1 = new Institution(name:'Institution 1', uid:'co03',institutionType:'zoo',userLastModified:'test').save(flush:true, failOnError: true)
        i1.addToCollections(c1)
        then:
        !c1.hasErrors()
        c1 != null
        c1.name == 'Collection 1'
        c1.states == 'ACT'
        Collection.list().size() == 4


        Institution.list().size() == 1

        c1.getInstitution() == i1
        i1.getCollections().size() == 1
        Collection.findAllByUid('co03').size() == 1
        !c1.canBeMapped()

        ProviderGroup pg = c1
        !pg.canBeMapped()
    }
}