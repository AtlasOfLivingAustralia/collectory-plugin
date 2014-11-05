package au.org.ala.collectory

import grails.converters.JSON
import grails.validation.Validateable

import java.text.NumberFormat
import java.text.ParseException

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CollectionController extends ProviderGroupController {

    CollectionController() {
        entityName = "Collection"
        entityNameLower = "collection"
    }

    def idGeneratorService

    def index = {
        redirect(action:"list")
    }

    // list all collections
    def list = {
        if (params.message)
            flash.message = params.message
        params.sort = params.sort ?: "name"
        def colls = Collection.list(params)
        def sortOrder = (params.order == 'desc') ? -1 : 1
        if (params.sort == 'institution') {
            // need to sort by institution name
            colls.sort { a,b ->
                if (!a.institution) return sortOrder
                if (!b.institution) return -1 * sortOrder
                return (a.institution?.name <=> b.institution?.name) * sortOrder
            }
        }
        if (params.sort == 'acronym') {
            // need to sort by name
            colls.sort { a,b ->
                if (!a.acronym) return sortOrder
                if (!b.acronym) return -1 * sortOrder
                return (a.acronym <=> b.acronym) * sortOrder
            }
        }
        if (params.sort == 'keywords') {
            // need to sort by derived classification
            colls.sort { a,b ->
                def aClass = cl.reportClassification(keywords: a.keywords).toString()
                def bClass = cl.reportClassification(keywords: b.keywords).toString()
                if (!aClass) return sortOrder
                if (!bClass) return -1 * sortOrder
                if (aClass == bClass) {
                    // secondary sort on name
                    return a.name <=> b.name
                }
                return (aClass <=> bClass) * sortOrder
            }
        }
        ActivityLog.log username(), isAdmin(), Action.LIST
        [collInstanceList: colls, collInstanceTotal: Collection.count()]
    }

    def myList = {
        // do not paginate this list - unlikely to be large
        // get user's contact id
        def userContact = null
        def user = username()
        if (user) {
            userContact = Contact.findByEmail(user)
            def collectionList = []
            def institutionList = []
            if (userContact) {
                ContactFor.findAllByContact(userContact).each {
                    ProviderGroup pg = ProviderGroup._get(it.entityUid)
                    if (pg?.entityType() == Collection.ENTITY_TYPE) {
                        collectionList << pg
                    } else if (pg?.entityType() == Institution.ENTITY_TYPE) {
                        institutionList << pg
                    }
                }
            }
            ActivityLog.log username(), isAdmin(), Action.MYLIST
            log.info ">>${user} listing my collections and institution"
            render(view: 'myList', model: [collections: collectionList, institutions: institutionList])
        }
    }

    // show a single collection
    def show = {
        log.debug ">entered show with id=${params.id}"
        def collectionInstance = findCollection(params.id)
        if (!collectionInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'collection.label', default: 'Collection'), params.id])}"
            redirect(action: "list")
        } else {
            // show it
            log.debug ">>${username()} showing ${collectionInstance.name}"
            ActivityLog.log username(), isAdmin(), collectionInstance.uid, Action.VIEW
            [instance: collectionInstance, contacts: collectionInstance.getContacts(),
                    changes: getChanges(collectionInstance.uid)]
        }
    }

    /**
     * Return a summary as JSON.
     * Param can be:
     *  1. database id
     *  2. uid
     *  3. lsid
     *  4. acronym
     * @deprecated use /lookup/summary
     */
    def summary = {
        Collection collectionInstance = findCollection(params.id)
        println ">> summary id = " + params.id
        if (collectionInstance) {
            render collectionInstance.buildSummary() as JSON
        } else {
            log.error "Unable to find collection for id = ${params.id}"
            def error = ["error":"unable to find collection for id = " + params.id]
            render error as JSON
        }
    }

    /**
     * Return a summary as JSON.
     * Params are:
     * inst - institution provider code
     * coll - collection provider code
     * @deprecated use /lookup/collection
     */
    def findCollectionFor = {
        def inst = params.inst
        def coll = params.coll
        if (!inst) {
            def error = ["error":"must specify an institution code as parameter inst"]
            render error as JSON
        }
        if (!coll) {
            def error = ["error":"must specify a collection code as parameter coll"]
            render error as JSON
        }
        Collection col = ProviderMap.findMatch(inst, coll)
        if (col) {
            render col.buildSummary() as JSON
        } else {
            def error = ["error":"unable to find collection with inst code = ${inst} and coll code = ${coll}"]
            render error as JSON
        }
    }

    // search for collections using the supplied search term
    def searchList = {
        params.each {log.debug it}
        if (!params.max) params.max = 10
        if (!params.offset) params.offset = 0
        if (!params.sort) params.sort = "name"
        if (!params.order) params.order = "asc"

        log.info ">>${username()} searching for ${params.term}"
        ActivityLog.log username(), isAdmin(), Action.SEARCH, params.term

        def results = Collection.createCriteria().list(max: params.max, offset: params.offset) {
            order(params.sort, params.order)
            or {
                like ('name', "%${params.term}%")
                like ('keywords', "%${params.term}%")
                eq ('acronym', "${params.term}")
            }
        }

        def term = params.term
        def criteria = term ? term : "blank"        // for display purposes
        [providerGroupInstanceList : results, providerGroupInstanceTotal: results.getTotalCount(), criteria: [criteria], term: term]
    }

    /**
     * Returns JSON to indicate whether the collection name exists.
     */
    def nameExists = {
        def name = params.name
        def match = Collection.findByName(name)
        if (match) {
            def result = [found: "true", uid: match.uid]
            render result as JSON
        }
        else {
            def result = [found: "false"]
            render result as JSON
        }
    }

    /** V2 editing ****************************************************************************************************/

    /**
     * Update descriptive attributes that are specific to collections.
     *
     * Called by the base class method for updating descriptions.
     */
    def entitySpecificDescriptionProcessing(collection, params) {
        // special handling for collection type
        collection.collectionType = toJson(params.collectionType)
        params.remove('collectionType')

        // special handling for keywords
        def keywords = params.keywords.tokenize(',')
        def trimmedKeywords = keywords.collect {return it.trim()}
        collection.keywords = toJson(trimmedKeywords)
        params.remove('keywords')

        // special handling for sub-collections
        def names = params.findAll { key, value ->
            key.startsWith('name_') && value
        }
        def subs = names.sort().collect { key, value ->
            def idx = key.substring(5)
            def desc = params."description_${idx}"
            return [name: value, description: desc ? desc : ""]
        }
        def subCollections = []
        subs.each {
            subCollections.add it
        }
        collection.subCollections = subCollections as JSON
        params.remove('subCollections')

    }

    /**
     * Update geo and taxo range attributes & stats
     */
    def updateRange = {
        def collection = Collection.get(params.id)
        if (collection) {
            if (checkLocking(collection,'range')) { return }

            // special handling for numbers
            if (!params.eastCoordinate) { params.eastCoordinate = -1 }
            if (!params.westCoordinate) { params.westCoordinate = -1 }
            if (!params.northCoordinate) { params.northCoordinate = -1 }
            if (!params.southCoordinate) { params.southCoordinate = -1 }
            if (!params.numRecords) { params.numRecords = -1 }
            if (!params.numRecordsDigitised) { params.numRecordsDigitised = -1 }

            // special handling for kingdoms
            collection.kingdomCoverage = toSpaceSeparatedList(params.kingdomCoverage)
            params.remove('kingdomCoverage')
            params.remove('_kingdomCoverage')

            // special handling for sci names
            def scientificNames = params.scientificNames.tokenize(',')
            def trimmedScientificNames = scientificNames.collect {return it.trim()}
            collection.scientificNames = toJson(trimmedScientificNames)
            params.remove('scientificNames')

            collection.properties = params
            collection.userLastModified = username()
            if (!collection.hasErrors() && collection.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'collection.label', default: 'Collection'), collection.uid])}"
                redirect(action: "show", id: collection.id)
            } else {
                render(view: "range", model: [command: collection])
            }
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'collection.label', default: 'Collection'), params.id])}"
            redirect(action: "show", id: params.id)
        }
    }

    def addKeyword = {
        def collection = Collection.get(params.id)
        if (collection) {
            if (checkLocking(collection,'show')) { return }
            def keywords = collection.listKeywords()
            if (!(keywords.contains(params.keyword))) {
                keywords.add params.keyword
                collection.keywords = (keywords as JSON).toString()
            }
            collection.userLastModified = username()
            if (!collection.hasErrors() && collection.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'collection.label', default: 'Collection'), collection.uid])}"
                redirect(action: "show", id: collection.id)
            } else {
                render(view: "show", model: [command: collection])
            }
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'collection.label', default: 'Collection'), params.id])}"
            redirect(action: "show", id: params.id)
        }
    }

    /** end V2 editing ************************************************************************************************/

    /**
     * Get the instance for this entity based on either uid or DB id.
     *
     * @param id UID or DB id
     * @return the entity of null if not found
     */
    protected ProviderGroup get(id) {
        if (id.size() > 2) {
            if (id[0..1] == Collection.ENTITY_PREFIX) {
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
        return Collection.get(dbId)
    }

    private findCollection(id) {
        if (!id) {return null}
        // try lsid
        if (id instanceof String && id.startsWith(ProviderGroup.LSID_PREFIX)) {
            return Collection.findByGuid(id)
        }
        // try uid
        if (id instanceof String && id.startsWith(Collection.ENTITY_PREFIX)) {
            return Collection.findByUid(id)
        }
        // try id
        try {
            NumberFormat.getIntegerInstance().parse(id)
            def result = Collection.read(id)
            if (result) {return result}
        } catch (ParseException e) {}
        // try acronym
        return Collection.findByAcronym(id)
    }

}

/** V2 command classes **/
@Validateable
class BaseCommand {

    long id
    long version
    String name
    String guid
    String acronym
    Institution institution
    List networkMembership
    String websiteUrl

    static constraints = {
        name(blank:false)
        guid(nullable:true)
        acronym(nullable:true)
        institution(nullable:true)
        networkMembership(nullable:true)
        websiteUrl(nullable:true)
    }
}

@Validateable
class LocationCommand {
    long id
    long version
    Address address
    String latitude
    String longitude
    String state
    String email
    String phone

    static constraints = {
        address(nullable:true)
        state(nullable:true)
        email(nullable:true)
        phone(nullable:true)
    }
}

