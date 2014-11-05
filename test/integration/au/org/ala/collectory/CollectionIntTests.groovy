package au.org.ala.collectory

import grails.test.*

class CollectionIntTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testCreation() {
        def c1 = new Collection(name:'Collection 1', uid:'co01',states: 'ACT',userLastModified:'test')
        c1.validate()
        if (c1.hasErrors()) {
            println "has errors"
            def e = c1.errors
            e.each {println it.toString()}
        } else {
            c1.save(flush:true)
        }
        def c2 = new Collection(name:'Collection 2', uid:'co02',userLastModified:'test').save(flush:true)
        def c3 = new Collection(name:'Collection 3', uid:'co03',userLastModified:'test').save(flush:true)
        def c4 = new Collection(name:'Collection 4', uid:'co04',userLastModified:'test').save(flush:true)

        assertNotNull c1
        assertEquals 'Collection 1', c1.name
        assertEquals 'ACT', c1.states

        assertEquals 4, Collection.list().size()

        def i1 = new Institution(name:'Institution 1', uid:'co03',institutionType:'zoo',userLastModified:'test').save(flush:true)

        assertEquals 1, Institution.list().size()

        i1.addToCollections(c1)
        assertEquals i1, c1.getInstitution()
        assertEquals 1, i1.getCollections().size()

//        assertEquals 5, ProviderGroup.list().size()

        assertEquals 1, Collection.findAllByUid('co03').size()

        assertFalse c1.canBeMapped()

        ProviderGroup pg = c1
        assertFalse pg.canBeMapped()
        
    }
}