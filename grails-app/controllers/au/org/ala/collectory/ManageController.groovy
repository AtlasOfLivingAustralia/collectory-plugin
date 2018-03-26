package au.org.ala.collectory

import au.org.ala.audit.AuditLogEvent
import au.org.ala.collectory.resources.DataSourceLoad
import au.org.ala.collectory.resources.gbif.GbifDataSourceAdapter

class ManageController {

    def collectoryAuthService
    def externalDataService
    def gbifService

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
    def loadExternalResources = {
        DataSourceConfiguration configuration = new DataSourceConfiguration(
                guid: UUID.randomUUID().toString(),
                name: '',
                description: '',
                adaptorClass: GbifDataSourceAdapter.class,
                endpoint: new URL(grailsApplication.config.gbifApiUrl + '/'),
                username: '',
                password: '',
                country: Locale.default.getCountry(),
                recordType: 'OCCURRENCE',
                defaultDatasetValues: [:],
                keyTerms: [],
                resources: []
        )
        def adaptor = configuration.createAdaptor()
        render(view: "externalLoad",
               model: [
                    configuration: configuration,
                    countryMap: adaptor.countryMap,
                    datasetTypeMap: adaptor.datasetTypeMap,
                    adaptors: externalDataService.ADAPTORMAP,
                    dataProviders: DataProvider.all.sort { it.name }
                ]
        )
    }

    /**
     * Search for resources that may be loaded from an external source
     */
    def searchForResources() {
        log.debug "Searching for resources from external source: ${params}"
        DataSourceConfiguration configuration = new DataSourceConfiguration(params)
        def dataResources = DataResource.all.findAll({ dr -> dr.resourceType == 'records' }).sort({ it.name })
        def resources = externalDataService.searchForDatasets(configuration)
        configuration.resources = resources
        def dataProvider = null
        if (configuration.dataProviderUid){
            dataProvider = DataProvider.findByUid(configuration.dataProviderUid)
        }
        render(view: 'externalLoadReview',
               model: [
                       loadGuid: UUID.randomUUID().toString(),
                       dataResources: dataResources,
                       dataProvider: dataProvider,
                       configuration: configuration
               ]
        )
    }

    /**
     * Update from an externbal source
     * <p>
     * The web pade
     */
    def updateFromExternalSources() {
        log.debug "Update resources from external source: ${params}"
        DataSourceConfiguration configuration = new DataSourceConfiguration(params)
        externalDataService.updateFromExternalSources(configuration, params.loadGuid)
        redirect(action: 'externalLoadStatus', params: [loadGuid: params.loadGuid])
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
     *
     * Display the load status for the supplied load
     */
    def externalLoadStatus(){
        DataSourceLoad load = externalDataService.getStatusInfoFor(params.loadGuid)
        [load :load, refreshInterval: externalDataService.POLL_INTERVAL]
    }

    /**
     * Landing page for self-service management of entities.
     * 
     * @param show = user will display user login/cookie/roles details
     */
    def list = {

        def view = "list"
        if(collectoryAuthService.isAdmin()){
            view = "adminList"
        }




        // find the entities the user is allowed to edit
        def entities = collectoryAuthService.authorisedForUser(collectoryAuthService.username()).sorted

        log.debug("user ${collectoryAuthService.username()} has ${request.getUserPrincipal()?.attributes}")

        // get their contact details in case needed
        def contact = Contact.findByEmail(collectoryAuthService.username())


        render(view: view, model: [entities: entities, user: contact])
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
