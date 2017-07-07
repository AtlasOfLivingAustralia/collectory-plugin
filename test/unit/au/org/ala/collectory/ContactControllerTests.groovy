package au.org.ala.collectory

import au.org.ala.audit.AuditLogEvent
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.springframework.http.HttpMethod
import spock.lang.Specification

@TestFor(ContactController)
@Mock([Contact, CollectoryAuthService, AuditLogEvent, ActivityLog, ContactFor])
class ContactControllerTests extends Specification {
    static USERNAME = "fjnurke"

    Contact contact

    def setup() {
        controller.collectoryAuthService = Mock(CollectoryAuthService)
        controller.collectoryAuthService.userInRole(_) >> true
        controller.collectoryAuthService.username() >> USERNAME
        contact = new Contact(
            title: "Dr",
            firstName: "Lemmy",
            lastName: "Caution",
            phone: "0262465909",
            mobile: "0419468551",
            email: "lemmy.caution@csiro.au",
            notes: "to be treated with exaggerated respect",
            publish: true,
            userLastModified: 'test')
    }

    def testIndex() {
        when:
        controller.index()
        then:
        response.redirectedUrl == "/contact/list"
    }


    def testName() {
        when:
        contact.save(flush:true, failOnError: true)
        controller.params.id = 1
        controller.name()
        then:
        response.contentAsString == 'Dr Lemmy Caution'
    }


    def testList() {
        when:
        contact.save(flush:true, failOnError: true)
        request.contentType = JSON_CONTENT_TYPE
        def list = controller.list()
        then:
        list.contactInstanceList != null
        list.contactInstanceList.size() == 1
        list.contactInstanceList[0].title == contact.title
    }

    def testCreate() {
        when:
        controller.params.lastName = contact.lastName
        controller.params.returnTo = '/contact/list'
        def ct = controller.create()
        then:
        ct.contactInstance != null
        ct.contactInstance.lastName == contact.lastName
        ct.contactInstance.userLastModified == USERNAME
        ct.returnTo == '/contact/list'
    }

    def testSave() {
        when:
        controller.params.title = contact.title
        controller.params.firstName = contact.firstName
        controller.params.lastName = contact.lastName
        controller.params.email = contact.email
        request.method = 'POST'
        controller.save()
        then:
        Contact.count() == 1
        def ct = Contact.list()[0]
        ct.title == contact.title
        ct.firstName == contact.firstName
        ct.lastName == contact.lastName
        ct.userLastModified == USERNAME
        response.redirectedUrl == "/contact/show/${ct.id}"
    }

    def testShow() {
        when:
        contact = contact.save(flush: true, failOnError: true)
        controller.params.id = contact.id
        def show = controller.show()
        then:
        show.contactInstance != null
        show.contactInstance.id == contact.id
        show.contactInstance.title == contact.title
    }


    def testEdit() {
        when:
        contact = contact.save(flush: true, failOnError: true)
        controller.params.id = contact.id
        def edit = controller.edit()
        then:
        edit.contactInstance != null
        edit.contactInstance.id == contact.id
        edit.contactInstance.title == contact.title
    }

    def testUpdate() {
        when:
        def newFirstName = contact.firstName + " - 1"
        contact = contact.save(flush: true, failOnError: true)
        controller.params.id = contact.id
        controller.params.firstName = newFirstName
        request.method = 'POST'
        controller.update()
        then:
        Contact.count() == 1
        def ct = Contact.list()[0]
        ct.title == contact.title
        ct.firstName == newFirstName
        ct.lastName == contact.lastName
        ct.userLastModified == USERNAME
    }

    def testDelete() {
        when:
        contact = contact.save(flush: true, failOnError: true)
        controller.params.id = contact.id
        request.method = 'POST'
        controller.delete()
        then:
        Contact.count() == 0
        def ct = Contact.list()[0]
        response.redirectedUrl == '/contact/list'
    }

    def testShowProfile() {
        when:
        contact = contact.save(flush: true, failOnError: true)
        controller.params.userEmail = contact.email
        def profile = controller.showProfile()
        then:
        profile.contact != null
        profile.contact.email == contact.email
        profile.contactRels != null
        profile.contactRels.size() == 0

    }

}
