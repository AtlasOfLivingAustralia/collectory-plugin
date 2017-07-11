package au.org.ala.collectory

import grails.test.mixin.integration.Integration
import spock.lang.Specification

@Integration
class InstitutionIntTests extends Specification {
    void testCollections() {
        when:
        def inst = new Institution(uid: 'in1000', name:'institution',userLastModified:'test')
        inst.save(flush: true, failOnError: true)
        def c1 = new Collection(uid: 'co1000', name:'Collection 1',userLastModified:'test')
        inst.addToCollections(c1)
        then:
        inst.getCollections().size() == 1
        c1.getInstitution() == inst
    }
}
