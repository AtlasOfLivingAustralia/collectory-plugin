package au.org.ala.collectory
/*
 * Tests the interaction between contacts and ProviderGroups/Infosources
 */
import grails.test.*
import grails.test.mixin.integration.Integration
import spock.lang.Specification

@Integration
class ContactGroupTests extends Specification {
    Contact pete
    Contact mark
    ProviderGroup group

    protected void setup() {
        pete = new Contact(firstName: "Peter", lastName: "Flemming", publish: true, email: "pete@csiro.au", userLastModified: "test").save(flush: true, failOnError: true)
        mark = new Contact(firstName: "Mark", lastName: "Woolston", publish: true, userLastModified: "test").save(flush: true, failOnError: true)
        group = new Institution(guid: "ABC", uid:'in13', name: "XYZ", userLastModified: "test").save(flush: true, failOnError: true)
        // clear any existing contacts
        def cfs = ContactFor.list()
        cfs.each {
            it.delete(flush:true)
        }
   }

    def testLinks1() {
        setup:
        assert Contact.count() == 2
        assert ContactFor.count() == 0
        when:
        // create a contact link
        def ncf = new ContactFor(contact: pete, entityUid: group.uid, role: "Manager", administrator: true, primaryContact: true, userLastModified: "test")
        ncf.save(flush: true, failOnError: true)
        then:
        ContactFor.count() == 1
        def contacts = ContactFor.findAll()
        contacts.size() == 1
        ContactFor cf = contacts.get(0)
        cf.contact == ncf.contact
        cf.role == ncf.role
    }

    def testLinks2() {
        setup:
        assert Contact.count() == 2
        assert ContactFor.count() == 0
        when:
        // create a contact link
        def ncf1 = new ContactFor(contact: pete, entityUid: group.uid, role: "Manager", administrator: true, primaryContact: true, userLastModified: "test")
        ncf1.save(flush: true, failOnError: true)
        def ncf2 = new ContactFor(contact: mark, entityUid: group.uid, role: "Asst Manager", administrator: true, primaryContact: false, userLastModified: "test")
        ncf2.save(flush: true, failOnError: true)
        then:
        def ecf = ContactFor.findAllByEntityUid(group.uid)
        ecf.size() == 2
    }

    def testGroupContactsManualAdd1() {
        when:
        def ncf1 = new ContactFor(contact: pete, entityUid: group.uid, role: "Manager", administrator: true, primaryContact: true, userLastModified: "test")
        ncf1.save(flush: true)
        def ncf2 = new ContactFor(contact: mark, entityUid: group.uid, role: "Asst Manager", administrator: true, primaryContact: false, userLastModified: "test")
        ncf2.save(flush: true)
        then:
        group.getContacts().size() == 2
    }

    def testGroupContactsManualAdd2() {
        when:
        def ncf1 = new ContactFor(contact: pete, entityUid: group.uid, role: "Manager", administrator: true, primaryContact: true, userLastModified: "test")
        ncf1.save(flush: true)
        def ncf2 = new ContactFor(contact: mark, entityUid: group.uid, role: "Asst Manager", administrator: true, primaryContact: false, userLastModified: "test")
        ncf2.save(flush: true)
        group.deleteFromContacts(mark)
        group.deleteFromContacts(pete)
        then:
        group.getContacts().size() == 0
        ContactFor.count() == 0
    }

    def testGroupContactsUsingAddContact() {
        when:
        group.addToContacts(mark, "Project Officer", false, false, 'test')
        group.addToContacts(pete, "Manager", true, true, 'test')
        then:
        ContactFor.count() == 2
        group.getContacts().size() == 2
        List<ContactFor> contacts = group.getContacts()
        contacts.sort {item -> item.contact.id}
        contacts[0].contact.firstName == "Peter"
        contacts[0].role == "Manager"
        contacts[0].administrator
        contacts[0].primaryContact
        contacts[1].contact.firstName == "Mark"
        contacts[1].role == "Project Officer"
        !contacts[1].administrator
        !contacts[1].primaryContact

    }

    // tests to see if the dynamic wiring in integration tests breaks the isAdministrator call
    def testIsAdministrator() {
        when:
        def ncf1 = new ContactFor(contact: pete, entityUid: group.uid, role: "Manager", administrator: true, primaryContact: true, userLastModified: "test")
        ncf1.save(flush: true, failOnError: true)
        then:
        ContactFor.count() == 1
        ContactFor cf = ContactFor.findByContact(pete)
        cf != null
        cf.contact.firstName == 'Peter'
        cf.administrator

    }

    void testSearchByUser() {
        when:
        def ncf1 = new ContactFor(contact: pete, entityUid: group.uid, role: "Manager", administrator: true, primaryContact: true, userLastModified: "test")
        ncf1.save(flush: true, failOnError: true)
        then:
        ContactFor.count() == 1
        def userContact = Contact.findByEmail("pete@csiro.au")
        userContact != null
        def collections = ContactFor.findAllByContact(pete).collect({ ProviderGroup._get(it.entityUid) }).findAll({ it != null }) as Set
        collections.size() == 1
    }
}
