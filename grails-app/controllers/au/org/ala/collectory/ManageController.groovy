package au.org.ala.collectory

import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent

class ManageController {

    def authService , gbifService

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
        if ((AuthenticationCookieUtils.cookieExists(request, AuthenticationCookieUtils.ALA_AUTH_COOKIE) ||  grailsApplication.config.security.cas.bypass)
            && !params.noRedirect) {
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
        log.debug(params)
        if(params.gbifUsername && params.gbifPassword){
            Integer maxResources = params.maxResources ? params.getInt("maxResources") : null
            gbifService.loadResourcesFor(params.country,params.gbifUsername, params.gbifPassword, maxResources)
            redirect(action: 'gbifCountryLoadStatus', params: [country:params.country])
        }
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
        def entities = authService.authorisedForUser(authService.username()).sorted
        println "user ${authService.username()} has ${request.getUserPrincipal()?.attributes}"
        log.debug("user ${authService.username()} has ${request.getUserPrincipal()?.attributes}")

        // get their contact details in case needed
        def contact = Contact.findByEmail(authService.username())

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
