package au.org.ala.collectory

import grails.converters.JSON
import org.springframework.web.multipart.MultipartFile
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class InstitutionController extends ProviderGroupController {

    InstitutionController() {
        entityName = "Institution"
        entityNameLower = "institution"
    }

    def scaffold = Institution

    def list = {
        if (params.message)
            flash.message = params.message
        params.max = Math.min(params.max ? params.int('max') : 50, 100)
        params.sort = params.sort ?: "name"
        [institutionInstanceList: Institution.list(params),
                institutionInstanceTotal: Institution.count()]
    }

    def show = {
        def institutionInstance = get(params.id)
        if (!institutionInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'institution.label', default: 'Institution'), params.id])}"
            redirect(action: "list")
        }
        else {
            log.debug "Ala partner = " + institutionInstance.isALAPartner
            ActivityLog.log username(), isAdmin(), institutionInstance.uid, Action.VIEW

            [instance: institutionInstance, contacts: institutionInstance.getContacts(), changes: getChanges(institutionInstance.uid)]
        }
    }

    /** V2 editing ****************************************************************************************************/

    // All in base class!!

    /** end V2 editing ************************************************************************************************/

    def delete = {
        def providerGroupInstance = get(params.id)
        if (providerGroupInstance) {
            if (isAdmin()) {
                /* need to remove it as a parent from all children otherwise they will be deleted */
                def collections = providerGroupInstance.collections as List
                collections.each {
                    providerGroupInstance.removeFromCollections it
                    it.userLastModified = username()
                    it.save()  // necessary?
                }
                // remove contact links (does not remove the contact)
                ContactFor.findAllByEntityUid(providerGroupInstance.uid).each {
                    it.delete()
                }
                // now delete
                try {
                    ActivityLog.log username(), isAdmin(), params.id as long, Action.DELETE
                    providerGroupInstance.delete(flush: true)
                    flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'providerGroup.label', default: 'ProviderGroup'), params.id])}"
                    redirect(action: "list")
                }
                catch (org.springframework.dao.DataIntegrityViolationException e) {
                    flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'providerGroup.label', default: 'ProviderGroup'), params.id])}"
                    redirect(action: "show", id: params.id)
                }
            } else {
                render("You are not authorised to access this page.")
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'providerGroup.label', default: 'ProviderGroup'), params.id])}"
            redirect(action: "list")
        }
    }

    /**
     * Get the instance for this entity based on either uid or DB id.
     *
     * @param id UID or DB id
     * @return the entity of null if not found
     */
    protected ProviderGroup get(id) {
        if (id.size() > 2) {
            if (id[0..1] == Institution.ENTITY_PREFIX) {
                return ProviderGroup._get(id)
            }
        }
        // else must be long id
        long dbId
        try {
            dbId = Long.parseLong(id)
        } catch (NumberFormatException e) {
            return null
        }
        return Institution.get(dbId)
    }
    
}