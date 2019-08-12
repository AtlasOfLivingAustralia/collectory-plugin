package au.org.ala.collectory

import au.org.ala.audit.AuditLogEvent
import grails.converters.JSON

class ReportsController {

    def collectoryAuthService

    def index = {
        redirect action: list, params: params
    }

    def list = {
        render(view: "index")
    }

    def home = {}

    def contactsForCouncilMembers = {
        def chafc = []
        Collection.findAllByNetworkMembershipIlike("%CHAFC%",[sort:'name']).each {
            def pc = it.getPrimaryContact()
            chafc << [id: it.uid, name: it.name, email: pc?.contact?.email, contact: pc?.contact?.buildName()]
        }
        def chaec = []
        Collection.findAllByNetworkMembershipIlike("%CHAEC%",[sort:'name']).each {
            def pc = it.getPrimaryContact()
            chaec << [id: it.uid, name: it.name, email: pc?.contact?.email, contact: pc?.contact?.buildName()]
        }
        def chacm = []
        Collection.findAllByNetworkMembershipIlike("%CHACM%",[sort:'name']).each {
            def pc = it.getPrimaryContact()
            chacm << [id: it.uid, name: it.name, email: pc?.contact?.email, contact: pc?.contact?.buildName()]
        }
        [chafc: chafc, chaec: chaec, chacm: chacm]
    }

    def contactsForCollections = {
        def model = DataController.buildContactsModel(Collection.list([sort:'name']))
        [contacts:model]
    }

    def contactsForInstitutions = {
        def model = DataController.buildContactsModel(Institution.list([sort:'name']))
        [contacts:model]
    }

    def data = {
        ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), Action.REPORT, 'data'
        [reports: new ReportCommand('data')]
    }

    def activity = {
        ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), Action.REPORT, 'activity'
        [reports: new ReportCommand('activity')]
    }

    def membership = {
        ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), Action.REPORT, 'membership'
        [reports: new ReportCommand('membership')]
    }

    def collections = {
        def simple = params.simple ?: 'false'
        ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), Action.REPORT, 'collections'
        [collections: Collection.list([sort:'name']), simple: simple]
    }

    def institutions = {
        def simple = params.simple ?: 'false'
        ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), Action.REPORT, 'institutions'
        [institutions: Institution.list([sort:'name']), simple: simple]
    }

    def providers = {
        ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), Action.REPORT, 'providers'
    }

    def resources = {
        ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), Action.REPORT, 'resources'
    }

    def contacts = {
        ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), Action.REPORT, 'contacts'
    }

    def codes = {
        [codeSummaries: (ProviderMap.list().collect { it.collection.buildSummary() }).sort {it.name}]
    }

    def duplicateContacts = {
        // find duplicate emails
        def dupEmails = []
        Contact.executeQuery("select email, count(*) as ct from Contact group by email").each {
            if (it[1] > 1 && it[0] != null) {
                dupEmails << [email: it[0], contacts: Contact.findAllByEmail(it[0])]
            }
        }
        // find duplicate names
        def dupNames = []
        Contact.executeQuery("select firstName, lastName, count(*) as ct from Contact group by firstName, lastName").each {
            if (it[2] > 1 && it[0] != null && it[1] != null) {
                dupNames << [firstName: it[0], lastName: it[1], contacts: Contact.findAllByLastNameAndFirstName(it[1], it[0])]
            }
        }
        [dupEmails: dupEmails, dupNames: dupNames]
    }

    /**
     * List records from the audit log in desc date order.
     *
     * @param offset for pagination control
     * @param who search term for the actor field
     * @param what search term for the propertyName and uri (uid) field
     * @param next return the next page of logs
     * @param reset resets the search criteria and pagination
     */
    def changes = {
        // set page size
        def pageSize = 100

        // offset controls where to start in the list
        int offset = params.offset?.toInteger() ?: 0
        if (params.next) {
            offset = offset + pageSize
        }
        else {
            // reset to start if doing any action other than next
            offset = 0
        }

        // build search criteria and retrieve next page
        def who = params.who ?: ""
        def what = params.what ?: ""
        if (params.reset) {
            // clear criteria
            who = ""; what = ""; offset = 0
        }
        def c = AuditLogEvent.createCriteria()
        def list = c {
            if (who) {
                like('actor','%' + who + '%')
            }
            if (what) {
                or {
                    like ('uri', '%' + what + '%')
                    like ('propertyName', '%' + what + '%')
                }
            }
            maxResults(pageSize)
            order('lastUpdated','desc')
            firstResult(offset)
        }

        [changes: list, offset:offset, who:who, what:what]
    }

    def notifications = {
        [notices: ActivityLog.findAllByUser('notify-service', [sort:'timestamp',order:'desc',max:100])]
    }

    def classification = {
        ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), Action.REPORT, 'classifications'
        def list = Collection.list([sort:'name'])
        def plants = 0; def fauna = 0; def entomology = 0; def microbes = 0
        list.each {
            if (Classification.matchKeywords(it.keywords, 'plants')) { plants++ }
            if (Classification.matchKeywords(it.keywords, 'fauna')) { fauna++ }
            if (Classification.matchKeywords(it.keywords, 'entomology')) { entomology++ }
            if (Classification.matchKeywords(it.keywords, 'microbes')) { microbes++ }
        }
        [collections: list, plants: plants, fauna: fauna, entomology: entomology, microbes: microbes]
    }

    def taxonomicHints = {
        ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), Action.REPORT, 'taxonomic hints'
        [collections: Collection.list([sort:'name'])]
    }

    def collectionTypes = {
        ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.userInRole(ProviderGroup.ROLE_ADMIN), Action.REPORT, 'collection types'
        [collections: Collection.list([sort:'name'])]
    }

    def harvesters = {
        withFormat {
            json {
                def list = DataResource.list(sort:'name').collect {[
                        uid: it.uid, name: it.name, status: it.status, harvestingFrequency: it.harvestFrequency,
                        lastChecked: it.lastChecked, dataCurrency: it.dataCurrency,
                        connectionParameters: it.connectionParameters ? JSON.parse(it.connectionParameters) : null]}
                render list as JSON
            }
            html {
                [resources: DataResource.list(sort:'name')]
            }
        }
    }

    def rights = {
        withFormat {
            json {
                def list = DataResource.list(sort:'name').collect {[
                        uid: it.uid, name: it.name, rights: it.rights, licenseType: it.licenseType,
                        licenseVersion: it.licenseVersion, permissionsDocument: it.permissionsDocument,
                        permissionsDocumentType: it.permissionsDocumentType, filed: it.filed,
                        riskAssessment: it.riskAssessment]}
                render list as JSON
            }
            html {
                [resources: DataResource.list(sort:'name')]
            }
        }
    }

    def attributions = {
        def collAttributions = []
        Collection.list([sort: 'name']).each {
            ProviderGroupSummary pgs = it.buildSummary()
            List<Attribution> attribs = it.getAttributionList()
            def ats = new Attributions(pgs, attribs)
            collAttributions << ats
        }
        def instAttributions = Institution.list([sort: 'name']).collect {
            ProviderGroupSummary pgs = it.buildSummary()
            List<Attribution> attribs = it.getAttributionList()
            new Attributions(pgs, attribs)
        }
        [collAttributions: collAttributions, instAttributions: instAttributions]
    }

    def missingRecords = {
        def mrs = []
        Collection.list([sort: 'name']).each {
            if (it.numRecordsDigitised > 0) {
                // find the number of biocache records
                def url = grailsApplication.config.biocacheServicesUrl + "/occurrences/collections/" + it.generatePermalink() + ".json?pageSize=0"
                def count = 0
                def conn = new URL(url).openConnection()
                conn.setConnectTimeout 3000
                try {
                    def json = conn.content.text
                    count = JSON.parse(json)?.totalRecords
                    if (count == null) { count = 0 } // safety in case json structure changes
                } catch (Exception e) {
                    log.error "Failed to lookup record count. ${e.getClass()} ${e.getMessage()} URL= ${url}."
                }
                // compare to num digistised
                if (count == 0 || (count / it.numRecordsDigitised) < 0.7) {
                    mrs << [collection:it.buildSummary(), biocacheCount: count, claimed: it.numRecordsDigitised]
                }
            }
        }
        [mrs: mrs]
    }

    def collectionSpecimenData = {
        def results = []
        Collection.list([sort: 'name']).each {
            def rec = new Records()
            // find the number of biocache records
            def url = grailsApplication.config.biocacheServicesUrl + "/occurrences/searchForUID.JSON?pageSize=0&q=" + it.uid
            def count = 0
            def conn = new URL(url).openConnection()
            conn.setConnectTimeout 3000
            try {
                def json = conn.content.text
                rec.numBiocacheRecords = JSON.parse(json)?.searchResult?.totalRecords
            } catch (Exception e) {
                log.error "Failed to lookup record count. ${e.getClass()} ${e.getMessage()} URL= ${url}."
            }
            rec.name = it.name
            rec.uid = it.uid
            rec.acronym = it.acronym
            rec.numRecords = it.numRecords
            rec.numRecordsDigitised = it.numRecordsDigitised
            results << rec
        }
        [statistics: results]
    }

    def providerRecordsData = {
        def results = []
        DataProvider.list([sort: 'name']).each {
            def rec = new Records()
            // find the number of biocache records
            def url = grailsApplication.config.biocacheServicesUrl + "/occurrences/searchForUID.JSON?pageSize=0&q=" + it.uid

            def count = 0
            def conn = new URL(url).openConnection()
            conn.setConnectTimeout 3000
            try {
                def json = conn.content.text
                rec.numBiocacheRecords = JSON.parse(json)?.searchResult?.totalRecords
            } catch (Exception e) {
                log.error "Failed to lookup record count. ${e.getClass()} ${e.getMessage()} URL= ${url}."
            }
            rec.name = it.name
            rec.uid = it.uid
            rec.acronym = it.acronym
            results << rec
        }
        [statistics: results]
    }

    def dataLinks = {
        [links: DataLink.list([sort:'provider'])]
    }
    
    class ReportCommand {
        int totalCollections
        int totalInstitutions
        int totalDataProviders
        int totalDataResources
        int totalDataHubs
        int totalContacts
        int totalLogons

        def collectionsWithType
        def collectionsWithFocus
        def collectionsWithKeywords
        def collectionsWithProviderCodes
        def collectionsWithGeoDescription
        def collectionsWithNumRecords
        def collectionsWithNumRecordsDigitised
        def collectionsWithDescriptions

        def collectionsWithoutContacts
        def collectionsWithoutEmailContacts
        def institutionsWithoutContacts
        def institutionsWithoutEmailContacts

        def collectionsWithInfosource

        def totalLogins
        def uniqueLogins
        def uniqueLoginList
        def supplierLogins
        def uniqueSupplierLogins
        def curatorViews
        def curatorPreviews
        def curatorEdits
        def adminViews
        def adminPreviews
        def adminEdits
        def lastLogin
        def latestActivity

        def partners
        def chahMembers
        def chaecMembers
        def chafcMembers
        def amrrnMembers
        def camdMembers

        def execQueryCollection = { query ->
            def answer = Collection.executeQuery(query)
            if (answer) {
                return answer[0]
            } else {
                return null
            }
        }

        def execQueryInstitution = { query ->
            def answer = Institution.executeQuery(query)
            if (answer) {
                return answer[0]
            } else {
                return null
            }
        }

        def countNotNull = { field ->
            def query = "select count(*) from Collection as pg where "
            if (field instanceof List) {
                field.eachWithIndex { it, i ->
                    if (i > 0) {query += " and "}
                    query += "pg.${it} <> NULL"
                }
            } else {
                query += "pg.${field} <> NULL"
            }
            def answer = execQueryCollection(query)
            if (!answer) answer = 0
            return answer
        }

        def countNotUnknown = { field ->
            def query = "select count(*) from Collection as pg where pg.${field} <> -1"
            def answer = execQueryCollection(query)
            if (!answer) answer = 0
            return answer
        }

        ReportCommand(String set) {
            switch (set) {
                case 'data':
                totalCollections = Collection.count()
                totalInstitutions = Institution.count()
                totalDataProviders = DataProvider.count()
                totalDataResources = DataResource.count()
                totalDataHubs = DataHub.count()
                totalContacts = Contact.count()

                /* sql: select count(*) from collectory.provider_group
                    where not exists (select * from collectory.contact_for
                    where contact_for.entity_id = provider_group.id)
                    and provider_group.group_type = 'Collection';*/
                collectionsWithoutContacts = execQueryCollection("select count(*) from Collection as pg \
                    where not exists (select id from ContactFor as cf \
                    where cf.entityUid = pg.uid)")

                institutionsWithoutContacts = execQueryInstitution("select count(*) from Institution as pg \
                    where not exists (select id from ContactFor as cf \
                    where cf.entityUid = pg.uid)")

                collectionsWithoutEmailContacts = execQueryCollection("select count(*) from Collection as pg \
                    where not exists (select id from ContactFor as cf \
                    where cf.entityUid = pg.uid \
                    and cf.contact.email <> '')")

                institutionsWithoutEmailContacts = execQueryInstitution("select count(*) from Institution as pg \
                    where not exists (select id from ContactFor as cf \
                    where cf.entityUid = pg.uid \
                    and cf.contact.email <> '')")

                collectionsWithType = countNotNull("collectionType")
                collectionsWithFocus = countNotNull("focus")
                collectionsWithKeywords = countNotNull("keywords")
                collectionsWithProviderCodes = ProviderMap.count()
                collectionsWithGeoDescription = countNotNull("geographicDescription")
                collectionsWithNumRecords = countNotUnknown("numRecords")
                collectionsWithNumRecordsDigitised = countNotUnknown("numRecordsDigitised")
                collectionsWithDescriptions = countNotNull(["pubDescription", "techDescription"])
                break

                case 'activity':
                curatorViews = ActivityLog.executeQuery("select count(*) from ActivityLog where action='${Action.VIEW.toString()}'" +
                        " and administratorForEntity = 1 and not admin = 1")[0]
                curatorPreviews = ActivityLog.executeQuery("select count(*) from ActivityLog where action='${Action.PREVIEW.toString()}'" +
                        " and administratorForEntity = 1 and not admin = 1")[0]
                curatorEdits = ActivityLog.executeQuery("select count(*) from ActivityLog where action='${Action.EDIT_SAVE.toString()}'" +
                        " and administratorForEntity = 1 and not admin = 1")[0]
                adminViews = ActivityLog.executeQuery("select count(*) from ActivityLog where action='${Action.VIEW.toString()}'" +
                        " and administratorForEntity = 1 and admin = 1")[0]
                adminPreviews = ActivityLog.executeQuery("select count(*) from ActivityLog where action='${Action.PREVIEW.toString()}'" +
                        " and administratorForEntity = 1 and admin = 1")[0]
                adminEdits = ActivityLog.executeQuery("select count(*) from ActivityLog where action='${Action.EDIT_SAVE.toString()}'" +
                        " and administratorForEntity = 1 and admin = 1")[0]
                latestActivity = ActivityLog.list([sort: 'timestamp', order:'desc', max:10])
                break
                
                case 'membership':
                partners = Institution.findAllByIsALAPartner(true)

                chahMembers = Collection.findAllByNetworkMembershipIlike("%CHAH%",[sort:'name']) +
                        Institution.findAllByNetworkMembershipIlike("%CHAH%",[sort:'name'])
                chaecMembers = Collection.findAllByNetworkMembershipIlike("%CHAEC%",[sort:'name']) +
                        Institution.findAllByNetworkMembershipIlike("%CHAEC%",[sort:'name'])
                chafcMembers = Collection.findAllByNetworkMembershipIlike("%CHAFC%",[sort:'name']) +
                        Institution.findAllByNetworkMembershipIlike("%CHAFC%",[sort:'name'])
                amrrnMembers = Collection.findAllByNetworkMembershipIlike("%CHACM%",[sort:'name']) +
                        Institution.findAllByNetworkMembershipIlike("%CHACM%",[sort:'name'])
                camdMembers = Collection.findAllByNetworkMembershipIlike("%CAMD%",[sort:'name']) +
                        Institution.findAllByNetworkMembershipIlike("%CAMD%",[sort:'name'])
                break

            }
        }
    }

    class Attributions {
        ProviderGroupSummary pgs
        List<Attribution> attribs

        Attributions(ProviderGroupSummary pgs, List<Attribution> attribs) {
            this.pgs = pgs
            this.attribs = attribs
        }
    }

}

class Records {
    String name
    String uid
    String acronym
    int numRecords = -1
    int numRecordsDigitised = -1
    int numBiocacheRecords = -1
}
