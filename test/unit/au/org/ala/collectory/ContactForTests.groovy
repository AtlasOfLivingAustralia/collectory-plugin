package au.org.ala.collectory

import grails.test.*

class ContactForTests extends GrailsUnitTestCase {

    ContactFor cf

    protected void setUp() {
        super.setUp()

        cf = new ContactFor(
            contact: [id: 4, lastName: "Caution"] as Contact,  // mock list as Contact class 
            entityUid: 'in27',
            role: "Manager",
            administrator: true,
            primaryContact: true)

    }

    protected void tearDown() {
        super.tearDown()
    }

    void testPrint() {
        assertEquals([
            "Contact id: 4",
            "Entity uid: in27",
            "Role: Manager",
            "isAdmin: true",
            "isPrimary: true"]
            , cf.print())
    }
}
