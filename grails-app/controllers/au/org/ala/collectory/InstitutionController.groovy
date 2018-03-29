package au.org.ala.collectory

import grails.converters.JSON
import org.springframework.web.multipart.MultipartFile

class InstitutionController extends ProviderGroupController {

    def authService

    InstitutionController() {
        entityName = "Institution"
        entityNameLower = "institution"
    }

    def scaffold = Institution
    def gbifRegistryService

    def list = {
        if (params.message)
            flash.message = params.message
        params.max = Math.min(params.max ? params.int('max') : 1000, 5000)
        params.sort = params.sort ?: "name"

        if(params.q){
            def results = Institution.findAllByNameLikeOrAcronymLike('%' + params.q + '%', '%' + params.q + '%')
            [institutionInstanceList: results,
             institutionInstanceTotal: results.size()]
        } else {
            [institutionInstanceList: Institution.list(params),
             institutionInstanceTotal: Institution.count()]
        }
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

    def updateAllGBIFRegistrations = {
        gbifRegistryService.updateAllRegistrations()
        flash.message = "${message(code: 'institution.gbif.updateAll', default: 'Updating all GBIF registrations as a background task (please be patient).')}"
        redirect(action: "list")
    }

    def updateGBIFDetails = {
        def pg = get(params.id)
        genericUpdate pg, 'gbif'
    }

    /**
     * This will update the GBIF Registry with the metadata and contacts for the data provider.
     */
    def updateGBIF = {
        def instance = get(params.id)
        if (instance) {
            try {
                if(authService.userInRole(grailsApplication.config.gbifRegistrationRole)) {
                    Boolean syncDataResources = params.syncDataResources?:"false".toBoolean()
                    Boolean syncContacts  = params.syncContacts?:"false".toBoolean()

                    gbifRegistryService.updateRegistration(instance, syncContacts, syncDataResources)
                    flash.message = "${message(code: 'institution.gbif.update.success', default: 'GBIF Registration Updated')}"
                } else {
                    flash.message = "User does not have sufficient privileges to perform this. ${grailsApplication.config.gbifRegistrationRole} role required"
                }
            } catch (Exception e) {
                flash.message = "${e.getMessage()}"
            }

            redirect(action: "show", id: params.id)
        }
    }

    /**
     * Register this institution with GBIF.
     */
    def registerGBIF = {
        log.info("REGISTERING data partner ${collectoryAuthService.username()}")

        if(authService.userInRole(grailsApplication.config.gbifRegistrationRole)) {
            def instance = get(params.id)
            if (instance) {
                try {
                    log.info("REGISTERING ${instance.uid}, triggered by user: ${collectoryAuthService.username()}")
                    if (collectoryAuthService.userInRole(grailsApplication.config.gbifRegistrationRole)) {
                        Boolean syncDataResources = params.syncDataResources?:"false".toBoolean()
                        Boolean syncContacts  = params.syncContacts?:"false".toBoolean()

                        gbifRegistryService.register(instance, syncContacts, syncDataResources)
                        flash.message = "${message(code: 'institution.gbif.register.success', default: 'Successfully registered in GBIF')}"
                        instance.save()
                    } else {
                        log.info("REGISTERING FAILED for ${instance.uid}, triggered by user: ${collectoryAuthService.username()} - user not in role")
                        flash.message = "You don't have permission to do register this data partner."
                    }
                } catch (Exception e) {
                    flash.message = "${e.getMessage()}"
                }

                redirect(action: "show", id: params.id)
            }
        } else {
            flash.message = "User does not have sufficient privileges to perform this. ${grailsApplication.config.gbifRegistrationRole} role required"
            redirect(action: "show", id: params.id)
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