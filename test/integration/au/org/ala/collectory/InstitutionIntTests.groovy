package au.org.ala.collectory

import grails.test.*

class InstitutionIntTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testCollections() {
        def inst = new Institution(name:'institution',userLastModified:'test')
        inst.validate()
        if (inst.hasErrors()) {
            inst.errors.each {
                println it.toString()
            }
        } else {
            inst.save()
        }
        def c1 = new Collection(name:'Collection 1',userLastModified:'test')
        //assertFalse c1.hasErrors()
        inst.addToCollections(c1)

        assertEquals 1, inst.getCollections().size()
        assertEquals inst, c1.getInstitution()
    }
}
