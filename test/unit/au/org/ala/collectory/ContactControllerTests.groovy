package au.org.ala.collectory

import grails.test.*

class ContactControllerTests extends ControllerUnitTestCase {

    protected void setUp() {
        super.setUp()
        mockDomain Contact
        def contact = new Contact(
            title: "Dr",
            firstName: "Lemmy",
            lastName: "Caution",
            phone: "0262465909",
            mobile: "0419468551",
            email: "lemmy.caution@csiro.au",
            notes: "to be treated with exaggerated respect",
            publish: true,
            userLastModified: 'test').save(flush:true)
        if (contact.hasErrors()) { println it }
        assertEquals 1, contact.id
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testName() {
        controller.params.id = 1
        controller.name()
        assertEquals 'Dr Lemmy Caution', controller.response.getContentAsString()
    }

    void testList() {
        controller.list()
        println "response = " + controller.response.getContentAsString()
    }
}
