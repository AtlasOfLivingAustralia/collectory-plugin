package au.org.ala.collectory

import grails.test.*

class ContactTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()
        mockDomain(Contact)
        mockForConstraintsTests(Contact)
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testConstraints() {

        def contact = new Contact(
            title: "Dr",
            firstName: "contact",
            lastName: "Woolston",
            phone: "0262465909",
            mobile: "0419468551",
            email: "contact.woolston@csiro.au",
            notes: "to be treated with exaggerated respect",
            publish: true,
            userLastModified: 'test')

        contact.validate()
        if (contact.hasErrors()) {
            println contact.errors
            fail "contact has errors"
        }        

        // test validation
        def testContact = new Contact()
        assertFalse testContact.validate()
        assertEquals "nullable", testContact.errors["userLastModified"]
    }

    void testTitle() {

        def testContact = new Contact(title: "Dr", firstName: "Lemmy", lastName: "Caution", userLastModified: 'test')
        assertTrue testContact.validate()
        if (testContact.hasErrors())
            println testContact.errors

        def badContact = new Contact(title: "Archbishop", firstName: "Lemmy", lastName: "Caution", userLastModified: 'test')
        assertFalse badContact.validate()
        if (badContact.hasErrors())
            println badContact.errors
        assertEquals "inList", badContact.errors['title']
    }

    void testEmail() {

        def testContact = new Contact(firstName: "Lemmy", lastName: "Caution", email: "contact@csiro.au", userLastModified: 'test')
        assertTrue testContact.validate()

        def badContact = new Contact(firstName: "Lemmy", lastName: "Caution", email: "contact.csiro", userLastModified: 'test')
        badContact.validate()
        if (badContact.hasErrors())
            println badContact.errors
        assertEquals "email", badContact.errors['email']
    }

    void testparseName() {
        def contact = new Contact()
        contact.parseName("Dr Lemmy Caution")
        assertEquals "Dr", contact.title
        assertEquals "Lemmy", contact.firstName
        assertEquals "Caution", contact.lastName

        //log contact.toString()

        contact = new Contact()
        contact.parseName("Lemmy Caution")
        assertNull contact.title
        assertEquals "Lemmy", contact.firstName
        assertEquals "Caution", contact.lastName

        contact = new Contact()
        contact.parseName("Lemmy A. Caution")
        assertEquals "", contact.title
        assertEquals "Lemmy A.", contact.firstName
        assertEquals "Caution", contact.lastName

        contact = new Contact()
        contact.parseName("Dr_Lemmy_Caution")
        assertNull contact.title
        assertNull contact.firstName
        assertEquals "Dr_Lemmy_Caution", contact.lastName

        contact = new Contact()
        contact.parseName("")
        assertNull contact.title
        assertNull contact.firstName
        assertNull contact.lastName

        contact = new Contact()
        contact.parseName("Mr Lemmy Alphaville Caution")
        assertEquals "Mr", contact.title
        assertEquals "Lemmy Alphaville", contact.firstName
        assertEquals "Caution", contact.lastName

    }
}
