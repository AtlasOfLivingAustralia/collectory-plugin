package au.org.ala.collectory

import grails.test.*
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(ContactFor)
@Mock(Contact)
class ContactForTests extends Specification {

    Contact contact
    ContactFor cf

    protected void setup() {
        contact = new Contact(id: 4, lastName: "Caution", userLastModified: 'fred')
        contact.save(flush: true, failOnError: true)
        cf = new ContactFor(
            contact: contact,
            entityUid: 'in27',
            role: "Manager",
            administrator: true,
            primaryContact: true,
            userLastModified: 'fred'
        )
        cf.save(flush: true, failOnError: true)

    }

    void testPrint() {
        expect:
        [ "Contact id: ${contact.id}",
          "Entity uid: in27",
          "Role: Manager",
          "isAdmin: true",
          "isPrimary: true",
          "notify: false"
        ] == cf.print()
    }
}
