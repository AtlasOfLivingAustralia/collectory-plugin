package au.org.ala.collectory



import org.junit.*
import grails.test.mixin.*

@TestFor(ProviderCodeController)
@Mock(ProviderCode)
class ProviderCodeControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/providerCode/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.providerCodeInstanceList.size() == 0
        assert model.providerCodeInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.providerCodeInstance != null
    }

    void testSave() {
        controller.save()

        assert model.providerCodeInstance != null
        assert view == '/providerCode/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/providerCode/show/1'
        assert controller.flash.message != null
        assert ProviderCode.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/providerCode/list'

        populateValidParams(params)
        def providerCode = new ProviderCode(params)

        assert providerCode.save() != null

        params.id = providerCode.id

        def model = controller.show()

        assert model.providerCodeInstance == providerCode
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/providerCode/list'

        populateValidParams(params)
        def providerCode = new ProviderCode(params)

        assert providerCode.save() != null

        params.id = providerCode.id

        def model = controller.edit()

        assert model.providerCodeInstance == providerCode
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/providerCode/list'

        response.reset()

        populateValidParams(params)
        def providerCode = new ProviderCode(params)

        assert providerCode.save() != null

        // test invalid parameters in update
        params.id = providerCode.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/providerCode/edit"
        assert model.providerCodeInstance != null

        providerCode.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/providerCode/show/$providerCode.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        providerCode.clearErrors()

        populateValidParams(params)
        params.id = providerCode.id
        params.version = -1
        controller.update()

        assert view == "/providerCode/edit"
        assert model.providerCodeInstance != null
        assert model.providerCodeInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/providerCode/list'

        response.reset()

        populateValidParams(params)
        def providerCode = new ProviderCode(params)

        assert providerCode.save() != null
        assert ProviderCode.count() == 1

        params.id = providerCode.id

        controller.delete()

        assert ProviderCode.count() == 0
        assert ProviderCode.get(providerCode.id) == null
        assert response.redirectedUrl == '/providerCode/list'
    }
}
