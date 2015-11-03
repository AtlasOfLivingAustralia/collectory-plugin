package au.org.ala.collectory

import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent

class ManageController {

    def collectoryAuthService, gbifService

    /**
     * Landing page for self-service management of entities.
     *
     * This is not cas-enabled so we must use the helper cookie to determine whether the user is logged in.
     * If the user is logged in, redirect to the cas-enabled 'list' action, so we can get roles.
     * Only users who are NOT logged in will see the 'index' page.
     *
     * @param noRedirect if present will override the redirect (for testing purposes only)
     */
    def index = {
        // forward if logged in
        if ((AuthenticationCookieUtils.cookieExists(request, AuthenticationCookieUtils.ALA_AUTH_COOKIE) || grailsApplication.config.security.cas.bypass.toBoolean()) && !params.noRedirect) {
            redirect(action: 'list')
        }
    }

    /**
     * Renders the view that allows a user to load all the gbif resources for a country
     */
    def gbifLoadCountry = {
        //get country list
        render(view: "gbifLoadCountry", model: ['pubMap': gbifService.getPublishingCountriesMap()])
    }

    /**
     * Submits the task required to load the GBIF resources for a country
     * country - the country to load
     * gbifUsername - the username used to instantiate a download
     * gbifPassword - the password for the supplied gbif user
     * @return
     */
    def loadAllGbifForCountry(){
        log.debug("Loading resources from GBIF: " + params)
        if(params.gbifUsername && params.gbifPassword){
            Boolean reloadExistingResources = false
            if (params.reloadExistingResources) {
                reloadExistingResources = true
            }
            Integer maxResources = params.maxResources ? params.getInt("maxResources") : null
            gbifService.loadResourcesFor(
                    params.country,
                    params.gbifUsername,
                    params.gbifPassword,
                    maxResources,
                    reloadExistingResources)
            redirect(action: 'gbifCountryLoadStatus', params: [country:params.country])
        }
    }

    /**
     *
     * @return
     */
    def loadDataset() {
        log.debug("Loading resources from GBIF: " + params)
        if (params.guid && params.gbifUsername && params.gbifPassword) {
            gbifService.getGbifDataset(
                    params.guid,
                    params.gbifUsername,
                    params.gbifPassword)
            redirect(action: 'gbifDatasetLoadStatus', model: ['datasetKey': params.guid], params: ['datasetKey': params.guid])
        }
    }

    /**
     *
     * Display the load status for the supplied country
     * country - the country to supply the status for
     * @return
     */
    def gbifDatasetLoadStatus(){
        log.debug('key->'+params.datasetKey)
        def gbifSummary = gbifService.getDatasetKeyStatusInfoFor(params.datasetKey)
        log.debug(gbifSummary)
        [gbifSummary:gbifSummary,'datasetKey':params.datasetKey]
    }

    /**
     *
     * @return
     */
    def gbifDatasetDownload() {
        log.debug('Dataset id ' + params.id)
        def dr = DataResource.findByUid(params.id)
        render(view: "gbifDatasetDownload", model: ['uid': dr.uid, 'guid' : dr.guid])
    }

    /**
     *
     * Display the load status for the supplied country
     * country - the country to supply the status for
     * @return
     */
    def gbifCountryLoadStatus(){
        def gbifSummary = gbifService.getStatusInfoFor(params.country)
        [country: params.country, gbifSummary:gbifSummary]
    }

    /**
     * Landing page for self-service management of entities.
     * 
     * @param show = user will display user login/cookie/roles details
     */
    def list = {
        // find the entities the user is allowed to edit
        def entities = collectoryAuthService.authorisedForUser(collectoryAuthService.username()).sorted

        log.debug("user ${collectoryAuthService.username()} has ${request.getUserPrincipal()?.attributes}")

        // get their contact details in case needed
        def contact = Contact.findByEmail(collectoryAuthService.username())

        [entities: entities, user: contact, show: params.show]
    }

    def show = {
        // assume it's a collection for now
        def instance = ProviderGroup._get(params.id)
        if (!instance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'collection.label', default: 'Collection'), params?.id])}"
            redirect(controller: "manage", action: "list")
        } else {
            [instance: instance, changes: getChanges(instance.uid)]
        }
    }

    def getChanges(uid) {
        // get audit records
        return AuditLogEvent.findAllByUri(uid,[sort:'lastUpdated',order:'desc',max:20])
    }
}
