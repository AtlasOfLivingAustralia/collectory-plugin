package au.org.ala.collectory

import grails.test.*
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(AdminController)
class AdminControllerTests extends Specification {
    void testIndex() {
        when:
        controller.index()
        then:
        response.redirectedUrl == '/manage'
    }
}
