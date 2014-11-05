package au.org.ala.collectory

import grails.converters.JSON
import java.text.SimpleDateFormat
import au.org.ala.collectory.resources.PP
import au.org.ala.collectory.resources.DarwinCoreFields

class DataResourceController extends ProviderGroupController {

    def metadataService

    DataResourceController() {
        entityName = "DataResource"
        entityNameLower = "dataResource"
    }

    def index = {
        redirect(action:"list")
    }

    // list all entities
    def list = {
        if (params.message)
            flash.message = params.message
        params.max = Math.min(params.max ? params.int('max') : 50, 100)
        params.sort = params.sort ?: "name"
        ActivityLog.log username(), isAdmin(), Action.LIST
        [instanceList: DataResource.list(params), entityType: 'DataResource', instanceTotal: DataResource.count()]
    }

    def show = {
        def instance = get(params.id)
        if (!instance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'dataResource.label', default: 'Data resource'), params.id])}"
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
            if (authService.isAdmin()) {
                render(view: 'consumers', model:[command: pg, source: params.source])
            } else {
                render("You are not authorised to edit these properties.")
            }
        }
    }

    boolean isCollectionOrArray(object) {
        [Collection, Object[]].any { it.isAssignableFrom(object.getClass()) }
    }

    def updateContribution = {
        def pg = get(params.id)

        // process connection parameters
        def protocol = params.remove('protocol')
        def cp = [:]
        if (protocol) {
            cp.protocol = protocol
        }
        def profile = metadataService.getConnectionProfile(protocol)
        profile.params.each {pp ->
            if (pp.type == 'boolean') {
                // the presence of the param indicates it is checked
                cp."${pp.paramName}" = params.containsKey(pp.paramName)
            }
            else if (params."${pp.paramName}") {
                if (pp.paramName == 'termsForUniqueKey') {
                    cp."${pp.paramName}" = params."${pp.paramName}".tokenize(', ')
                } else if (pp.type == 'delimiter') {
                    def str = params."${pp.paramName}"
                    str = str.replaceAll('HT', PP.HT_CHAR)
                    str = str.replaceAll('LF', PP.LF_CHAR)
                    str = str.replaceAll('VT', PP.VT_CHAR)
                    str = str.replaceAll('FF', PP.FF_CHAR)
                    str = str.replaceAll('CR', PP.CR_CHAR)
                    cp."${pp.paramName}" = str
                } else if(pp.paramName == 'url') {
                    if(isCollectionOrArray(params.url)){
                        def normalised = []
                        params.url.each {
                           if(it.trim().length() > 0){
                             normalised << it.trim()
                           }
                        }
                        cp.url = normalised.toSet()
                    } else {
                        cp.url = params.url
                    }
                } else {
                    cp."${pp.paramName}" = params."${pp.paramName}"
                }
            }
        }
        params.connectionParameters = (cp as JSON).toString()

        // process dates
        def lastChecked = params.remove('lastChecked')
        if (lastChecked) {
            pg.lastChecked = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(lastChecked).toTimestamp()
        }
        def dataCurrency = params.remove('dataCurrency')
        if (dataCurrency) {
            pg.dataCurrency = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(dataCurrency).toTimestamp()
        }

        // process default DwC values
        def ddv = [:]
        DarwinCoreFields.fields.each {
            if (params[it.name]) {
                ddv[it.name] = params[it.name]
            }
            if (params.otherKey && params.otherValue) {
                ddv[params.otherKey] = params.otherValue
            }
        }
        params.defaultDarwinCoreValues = (ddv as JSON).toString()

        // update
        genericUpdate pg, 'contribution'
    }

    def updateRights = {
        def pg = get(params.id)
        genericUpdate pg, 'rights'
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

    /**
     * Update descriptive attributes that are specific to resources.
     *
     * Called by the base class method for updating descriptions.
     */
    @Override def entitySpecificDescriptionProcessing(Object pg, Object params) {
    }

    /**
     * Get the instance for this entity based on either uid or DB id.
     *
     * @param id UID or DB id
     * @return the entity of null if not found
     */
    protected ProviderGroup get(id) {
        if (id.size() > 2) {
            if (id[0..1] == DataResource.ENTITY_PREFIX) {
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
        return DataResource.get(dbId)
    }

}
