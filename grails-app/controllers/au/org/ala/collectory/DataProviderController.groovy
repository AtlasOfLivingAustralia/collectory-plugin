package au.org.ala.collectory

class DataProviderController extends ProviderGroupController {

    def gbifRegistryService
    def authService

    DataProviderController() {
        entityName = "DataProvider"
        entityNameLower = "dataProvider"
    }

    def index = {
        redirect(action:"list")
    }

    // list all entities
    def list = {
        if (params.message) {
            flash.message = params.message
        }
        params.max = Math.min(params.max ? params.int('max') : 10000, 10000)
        params.sort = params.sort ?: "name"
        ActivityLog.log username(), isAdmin(), Action.LIST
        [instanceList: DataProvider.list(params), entityType: 'DataProvider', instanceTotal: DataProvider.count()]
    }

    def show = {
        def instance = get(params.id)
        if (!instance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'dataProvider.label', default: 'Data Provider'), params.id])}"
            redirect(action: "list")
        }
        else {
            log.debug "Ala partner = " + instance.isALAPartner
            ActivityLog.log username(), isAdmin(), instance.uid, Action.VIEW

            [instance: instance, contacts: instance.getContacts(), changes: getChanges(instance.uid)]
        }
    }

    def editConsumers = {
        def pg = get(params.id)
        if (!pg) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: "${entityNameLower}.label", default: entityNameLower), params.id])}"
            redirect(action: "list")
        } else {
            // are they allowed to edit
            if (collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN) || grailsApplication.config.security.cas.bypass.toBoolean()) {
                render(view: '../dataResource/consumers', model:[command: pg, source: params.source])
            } else {
                render("You are not authorised to edit these properties.")
            }
        }
    }

    def updateConsumers = {
        def pg = get(params.id)
        def newConsumers = params.consumers.tokenize(',')
        def oldConsumers = pg.listConsumers()
        // create new links
        newConsumers.each {
            if (!(it in oldConsumers)) {
                def dl = new DataLink(consumer: it, provider: pg.uid).save()
                auditLog(pg, 'INSERT', 'consumer', '', it, dl)
                log.info "created link from ${pg.uid} to ${it}"
            }
        }
        // remove old links - NOTE only for the variety (collection or institution) that has been returned
        oldConsumers.each {
            if (!(it in newConsumers) && it[0..1] == params.source) {
                log.info "deleting link from ${pg.uid} to ${it}"
                def dl = DataLink.findByConsumerAndProvider(it, pg.uid)
                auditLog(pg, 'DELETE', 'consumer', it, '', dl)
                dl.delete()
            }
        }
        flash.message =
            "${message(code: 'default.updated.message', args: [message(code: "${pg.urlForm()}.label", default: pg.entityType()), pg.uid])}"
        redirect(action: "show", id: pg.uid)
    }

    def delete = {
        def instance = get(params.id)
        if (instance) {
            if (isAdmin()) {
                /* need to remove it as a parent from all children otherwise they will be deleted */
                def resources = instance.resources as List
                resources.each {
                    instance.removeFromResources it
                    it.userLastModified = username()
                    it.save()  // necessary?
                }
                // remove contact links (does not remove the contact)
                ContactFor.findAllByEntityUid(instance.uid).each {
                    it.delete()
                }
                // now delete
                try {
                    ActivityLog.log username(), isAdmin(), params.id as long, Action.DELETE
                    instance.delete(flush: true)
                    flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'dataProvider.label', default: 'dataProvider'), params.id])}"
                    redirect(action: "list")
                }
                catch (org.springframework.dao.DataIntegrityViolationException e) {
                    flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'dataProvider.label', default: 'dataProvider'), params.id])}"
                    redirect(action: "show", id: params.id)
                }
            } else {
                render("You are not authorised to access this page.")
            }
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'dataProvider.label', default: 'dataProvider'), params.id])}"
            redirect(action: "list")
        }
    }

    def updateAllGBIFRegistrations = {
        gbifRegistryService.updateAllRegistrations()
        flash.message = "${message(code: 'dataProvider.gbif.updateAll', default: 'Updating all GBIF registrations as a background task (please be patient).')}"
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
                    log.info("[User ${authService.getUserId()}] has selected to update ${instance.uid} in GBIF...")
                    Boolean syncDataResources = params.syncDataResources?:"false".toBoolean()
                    Boolean syncContacts  = params.syncContacts?:"false".toBoolean()
                    gbifRegistryService.updateRegistration(instance, syncContacts, syncDataResources)

                    flash.message = "${message(code: 'dataProvider.gbif.update.success', default: 'GBIF Registration Updated')}"
                } else {
                    flash.message = "User does not have sufficient privileges to perform this. ${grailsApplication.config.gbifRegistrationRole} role required"
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e)
                flash.message = "${e.getMessage()}"
            }

            redirect(action: "show", id: params.id)
        }
    }

    /**
     * Register this data provider as an Organisation with GBIF.
     */
    def registerGBIF = {
        log.info("REGISTERING data partner ${collectoryAuthService.username()}")

        if(authService.userInRole(grailsApplication.config.gbifRegistrationRole)) {
            DataProvider instance = get(params.id)
            if (instance) {
                try {
                    log.info("REGISTERING ${instance.uid}, triggered by user: ${collectoryAuthService.username()}")
                    if (collectoryAuthService.userInRole(grailsApplication.config.gbifRegistrationRole)) {
                        log.info("[User ${authService.getUserId()}] has selected to register ${instance.uid} in GBIF...")

                        Boolean syncDataResources = params.syncDataResources?:"false".toBoolean()
                        Boolean syncContacts  = params.syncContacts?:"false".toBoolean()

                        gbifRegistryService.register(instance, syncContacts, syncDataResources)
                        flash.message = "${message(code: 'dataProvider.gbif.register.success', default: 'Successfully Registered in GBIF')}"
                        instance.save()
                    } else {
                        log.info("REGISTERING FAILED for ${instance.uid}, triggered by user: ${collectoryAuthService.username()} - user not in role")
                        flash.message = "You don't have permission to do register this data partner."
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e)
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
            if (id[0..1] == DataProvider.ENTITY_PREFIX) {
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
        return DataProvider.get(dbId)
    }

}
