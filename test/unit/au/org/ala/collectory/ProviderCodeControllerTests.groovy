package au.org.ala.collectory



import org.junit.*
import grails.test.mixin.*
import spock.lang.Specification

@TestFor(ProviderCodeController)
@Mock(ProviderCode)
class ProviderCodeControllerTests extends Specification {

    def populateValidParams(params) {
        assert params != null
        params.code = "code-1"
    }

    void testIndex() {
        when:
        controller.index()
        then:
        response.redirectedUrl == "/providerCode/list"
    }

    void testList() {
        when:
        def model = controller.list()
        then:
        model.providerCodeInstanceList.size() == 0
        model.providerCodeInstanceTotal == 0
    }

    void testCreate() {
        when:
        def model = controller.create()
        then:
        model.providerCodeInstance != null
    }

    void testSave1() {
        when:
        request.method = 'POST'
        controller.save()
        then:
        model.providerCodeInstance != null
        view == '/providerCode/create'
    }

    void testSave2() {
        when:
        populateValidParams(params)
        request.method = 'POST'
        controller.save()
        then:
        response.redirectedUrl == '/providerCode/show/1'
        controller.flash.message != null
        ProviderCode.count() == 1
    }

    void testShow1() {
        when:
        controller.show()
        then:
        assert flash.message != null
        assert response.redirectedUrl == '/providerCode/list'
    }

    void testShow2() {
        when:
        populateValidParams(params)
        def providerCode = new ProviderCode(params)
        providerCode.save(flush: true, failOnError: true)
        params.id = providerCode.id
        def model = controller.show()
        then:
        model != null
        model.providerCodeInstance == providerCode
    }

    void testEdit1() {
        when:
        controller.edit()
        then:
        flash.message != null
        response.redirectedUrl == '/providerCode/list'
    }

    void testEdit2() {
        when:
        populateValidParams(params)
        def providerCode = new ProviderCode(params)
        providerCode.save(flush: true, failOnError: true)
        params.id = providerCode.id
        def model = controller.edit()
        then:
        model.providerCodeInstance == providerCode
    }

    void testUpdate1() {
        when:
        request.method = 'POST'
        controller.update()
        then:
        flash.message != null
        response.redirectedUrl == '/providerCode/list'
    }


    void testUpdate2() {
        when:
        request.method = 'POST'
        populateValidParams(params)
        def providerCode = new ProviderCode(params)
        providerCode.save(flush: true, failOnError: true)
        // test invalid parameters in update
        params.id = providerCode.id
        params.code = null
        //TODO: add invalid values to params object
        controller.update()
        then:
        view == "/providerCode/edit"
        model.providerCodeInstance != null
    }

    void testUpdate3() {
        when:
        request.method = 'POST'
        populateValidParams(params)
        def providerCode = new ProviderCode(params)
        providerCode.save(flush: true, failOnError: true)
        populateValidParams(params)
        params.id = providerCode.id
        controller.update()
        then:
        response.redirectedUrl == "/providerCode/show/$providerCode.id"
        flash.message != null
    }

    void testUpdate4() {
        when:
        request.method = 'POST'
        populateValidParams(params)
        def providerCode = new ProviderCode(params)
        providerCode.save(flush: true, failOnError: true)
        populateValidParams(params)
        params.id = providerCode.id
        params.version = -1
        controller.update()
        then:
        view == "/providerCode/edit"
        model.providerCodeInstance != null
        model.providerCodeInstance.errors.getFieldError('version')
    }


    void testDelete1() {
        when:
        request.method = 'POST'
        controller.delete()
        then:
        flash.message != null
        response.redirectedUrl == '/providerCode/list'
    }

    void testDelete2() {
        when:
        request.method = 'POST'
        populateValidParams(params)
        def providerCode = new ProviderCode(params)
        providerCode.save(flush: true, failOnError: true)
        params.id = providerCode.id
        controller.delete()
        then:
        ProviderCode.count() == 0
        ProviderCode.get(providerCode.id) == null
        response.redirectedUrl == '/providerCode/list'
    }
}
