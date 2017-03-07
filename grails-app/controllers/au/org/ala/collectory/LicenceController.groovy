package au.org.ala.collectory

import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

/**
 * Simple webservice providing support licences in the system.
 */
class LicenceController {

    def index() {
        response.setContentType("application/json")
        render (Licence.findAll().collect { [name:it.name, url:it.url] } as JSON)
    }

    def list() {
        if (params.message)
            flash.message = params.message
        params.max = Math.min(params.max ? params.int('max') : 50, 100)
        params.sort = params.sort ?: "name"
        [instanceList: Licence.list(params), entityType: 'Licence', instanceTotal: Licence.count()]
    }

    def create() {
        [licenceInstance: new Licence(params)]
    }

    def save() {
        def licenceInstance = new Licence(params)
        if (!licenceInstance.save(flush: true)) {
            render(view: "create", model: [licenceInstance: licenceInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'licence.label', default: 'Licence'), licenceInstance.id])
        redirect(action: "show", id: licenceInstance.id)
    }

    def show(Long id) {
        def licenceInstance = Licence.get(id)
        if (!licenceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'licence.label', default: 'Licence'), id])
            redirect(action: "list")
            return
        }

        [licenceInstance: licenceInstance]
    }

    def edit(Long id) {
        def licenceInstance = Licence.get(id)
        if (!licenceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'licence.label', default: 'Licence'), id])
            redirect(action: "list")
            return
        }

        [licenceInstance: licenceInstance]
    }

    def update(Long id, Long version) {
        def licenceInstance = Licence.get(id)
        if (!licenceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'licence.label', default: 'Licence'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (licenceInstance.version > version) {
                licenceInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'licence.label', default: 'Licence')] as Object[],
                        "Another user has updated this Licence while you were editing")
                render(view: "edit", model: [licenceInstance: licenceInstance])
                return
            }
        }

        licenceInstance.properties = params

        if (!licenceInstance.save(flush: true)) {
            render(view: "edit", model: [licenceInstance: licenceInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'licence.label', default: 'Licence'), licenceInstance.id])
        redirect(action: "show", id: licenceInstance.id)
    }

    def delete(Long id) {
        def licenceInstance = Licence.get(id)
        if (!licenceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'licence.label', default: 'Licence'), id])
            redirect(action: "list")
            return
        }

        try {
            licenceInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'licence.label', default: 'Licence'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'licence.label', default: 'Licence'), id])
            redirect(action: "show", id: id)
        }
    }
}
