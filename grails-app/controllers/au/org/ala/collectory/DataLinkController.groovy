package au.org.ala.collectory

class DataLinkController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    static scaffold = true
    
    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 15, 1000)
        if (params.consumer) {
            def links = DataLink.findAllByConsumer(params.consumer)
            [dataLinkInstanceList: links, dataLinkInstanceTotal: links.size(), consumer: params.consumer, returnTo: params.consumer]
        } else if (params.provider) {
            def links = DataLink.findAllByProvider(params.provider)
            [dataLinkInstanceList: links, dataLinkInstanceTotal: links.size(), provider: params.provider, returnTo: params.provider]
        } else {
            [dataLinkInstanceList: DataLink.list(params), dataLinkInstanceTotal: DataLink.count(), returnTo: params.provider]
        }
    }

    def create = {
        println "consumer = ${params.consumer}"
        println "provider = ${params.provider}"
        def dataLinkInstance = new DataLink()
        dataLinkInstance.properties = params
        return [dataLinkInstance: dataLinkInstance, returnTo: params.returnTo]
    }

    def save = {
        def dataLinkInstance = new DataLink(params)
        if (dataLinkInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'dataLink.label', default: 'DataLink'), dataLinkInstance.id])}"
            redirect(action: "show", id: dataLinkInstance.id, params: [returnTo: params.returnTo])
        }
        else {
            render(view: "create", model: [dataLinkInstance: dataLinkInstance, returnTo: params.returnTo])
        }
    }

    def show = {
        def dataLinkInstance = DataLink.get(params.id)
        if (!dataLinkInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'dataLink.label', default: 'DataLink'), params.id])}"
            redirect(action: "list")
        }
        else {
            [dataLinkInstance: dataLinkInstance, returnTo: params.returnTo]
        }
    }

    def edit = {
        def dataLinkInstance = DataLink.get(params.id)
        if (!dataLinkInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'dataLink.label', default: 'DataLink'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [dataLinkInstance: dataLinkInstance, returnTo: params.returnTo]
        }
    }

    def update = {
        def dataLinkInstance = DataLink.get(params.id)
        if (dataLinkInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (dataLinkInstance.version > version) {
                    
                    dataLinkInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'dataLink.label', default: 'DataLink')] as Object[], "Another user has updated this DataLink while you were editing")
                    render(view: "edit", model: [dataLinkInstance: dataLinkInstance, returnTo: params.returnTo])
                    return
                }
            }
            dataLinkInstance.properties = params
            if (!dataLinkInstance.hasErrors() && dataLinkInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'dataLink.label', default: 'DataLink'), dataLinkInstance.id])}"
                redirect(action: "show", id: dataLinkInstance.id, params:[returnTo: params.returnTo])
            }
            else {
                render(view: "edit", model: [dataLinkInstance: dataLinkInstance, returnTo: params.returnTo])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'dataLink.label', default: 'DataLink'), params.id])}"
            redirect(action: "list", params:[returnTo: params.returnTo])
        }
    }

    def delete = {
        def dataLinkInstance = DataLink.get(params.id)
        if (dataLinkInstance) {
            try {
                dataLinkInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'dataLink.label', default: 'DataLink'), params.id])}"
                redirect(action: "list", params:[returnTo: params.returnTo])
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'dataLink.label', default: 'DataLink'), params.id])}"
                redirect(action: "show", id: params.id, params:[returnTo: params.returnTo])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'dataLink.label', default: 'DataLink'), params.id])}"
            redirect(action: "list", params:[returnTo: params.returnTo])
        }
    }
}
