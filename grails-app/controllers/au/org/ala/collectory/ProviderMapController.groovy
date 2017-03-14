package au.org.ala.collectory

class ProviderMapController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def collectoryAuthService
/*
 * Access control
 *
 * All methods require EDITOR role.
 * Edit methods require ADMIN or the user to be an administrator for the entity.
 */
    def beforeInterceptor = [action:this.&auth]

    def auth() {
        if (!collectoryAuthService?.userInRole(ProviderGroup.ROLE_EDITOR) && !grailsApplication.config.security.cas.bypass.toBoolean()) {
            render "You are not authorised to access this page."
            return false
        }
    }

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        if (!params.max) params.max = 10
        if (!params.offset) params.offset = 0
//        if (!params.sort) params.sort = "collectionName"
        if (!params.order) params.order = "asc"
        def maps = ProviderMap.withCriteria {
            maxResults(params.max?.toInteger())
            firstResult(params.offset?.toInteger())
//            if (params.sort == 'collectionName') {
//                collection {
//                    order('name', params.order)
//                }
//            } else {
//                order(params.sort, params.order)
//            }
        }
        [providerMapInstanceList: maps, providerMapInstanceTotal: ProviderMap.count(), returnTo: params.returnTo]
    }

    def create = {
        def providerMapInstance = new ProviderMap()
        providerMapInstance.properties = params
        println "createFor = ${params.createFor}"
        if (params.createFor) {
            def pg = Collection._get(params.createFor) as Collection
            if (pg) {
                providerMapInstance.collection = pg
            }
        }
        return [providerMapInstance: providerMapInstance, returnTo: params.returnTo]
    }

    def save = {
        def providerMapInstance = new ProviderMap(params)
        if (providerMapInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'providerMap.label', default: 'ProviderMap'), providerMapInstance.id])}"
            redirect(action: "show", id: providerMapInstance.id, params:[returnTo: params.returnTo])
        }
        else {
            render(view: "create", model: [providerMapInstance: providerMapInstance, returnTo: params.returnTo])
        }
    }

    def show = {
        def providerMapInstance = ProviderMap.get(params.id)
        if (!providerMapInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'providerMap.label', default: 'ProviderMap'), params.id])}"
            redirect(action: "list", params:[returnTo: params.returnTo])
        }
        else {
            [providerMapInstance: providerMapInstance, returnTo: params.returnTo]
        }
    }

    def edit = {
        def providerMapInstance = ProviderMap.get(params.id)
        if (!providerMapInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'providerMap.label', default: 'ProviderMap'), params.id])}"
            redirect(action: "list", params:[returnTo: params.returnTo])
        }
        else {
            if (providerMapInstance) {
                return [providerMapInstance: providerMapInstance, returnTo: params.returnTo]
            } else {
                render "You are not authorised to access this page."
            }
        }
    }

    def update = {
        def providerMapInstance = ProviderMap.get(params.id)
        if (providerMapInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (providerMapInstance.version > version) {
                    
                    providerMapInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'providerMap.label', default: 'ProviderMap')] as Object[], "Another user has updated this ProviderMap while you were editing")
                    render(view: "edit", model: [providerMapInstance: providerMapInstance], params:[returnTo: params.returnTo])
                    return
                }
            }
            providerMapInstance.properties = params
            if (!providerMapInstance.hasErrors() && providerMapInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'providerMap.label', default: 'ProviderMap'), providerMapInstance.id])}"
                redirect(action: "show", id: providerMapInstance.id, params:[returnTo: params.returnTo])
            }
            else {
                render(view: "edit", model: [providerMapInstance: providerMapInstance, returnTo: params.returnTo])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'providerMap.label', default: 'ProviderMap'), params.id])}"
            redirect(action: "list", params:[returnTo: params.returnTo])
        }
    }

    def delete = {
        def providerMapInstance = ProviderMap.get(params.id)
        if (providerMapInstance) {
            if (providerMapInstance.collection.uid) {
                try {
                    // remove collection link
                    providerMapInstance.collection?.providerMap = null
                    // remove code links
                    providerMapInstance.collectionCodes.each {
                        providerMapInstance.removeFromCollectionCodes it
                    }
                    providerMapInstance.institutionCodes.each {
                        providerMapInstance.removeFromInstitutionCodes it
                    }
                    // remove map
                    providerMapInstance.delete(flush: true)
                    flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'providerMap.label', default: 'ProviderMap'), params.id])}"
                    redirect(action: "list", params:[returnTo: params.returnTo])
                }
                catch (org.springframework.dao.DataIntegrityViolationException e) {
                    flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'providerMap.label', default: 'ProviderMap'), params.id])}"
                    redirect(action: "show", id: params.id, params:[returnTo: params.returnTo])
                }
            } else {
                render "You are not authorised to access this page."
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'providerMap.label', default: 'ProviderMap'), params.id])}"
            redirect(action: "list", params:[returnTo: params.returnTo])
        }
    }
}
