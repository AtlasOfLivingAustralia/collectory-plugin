package au.org.ala.collectory

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(DataLinkController)
@Mock(DataLink)
class DataLinkControllerTests extends Specification {
    void testList() {
        when:
        def list = controller.list()
        then:
        list != null
    }
}
