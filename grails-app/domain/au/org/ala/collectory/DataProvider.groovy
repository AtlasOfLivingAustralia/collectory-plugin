package au.org.ala.collectory

class DataProvider extends ProviderGroup implements Serializable {

    static final String ENTITY_TYPE = 'DataProvider'
    static final String ENTITY_PREFIX = 'dp'

    static auditable = [ignore: ['version','dateCreated','lastUpdated','userLastModified']]

    static hasMany = [resources: DataResource]

    String hiddenJSON // web service only (non-UI) JSON; used by fieldcapture to store project data

    String gbifCountryToAttribute      // the 3 digit iso code of the country to attribute in GBIF

    static mapping = {
        sort: 'name'
        hiddenJSON type: "text"
        pubShortDescription type: "text"
        pubDescription type: "text"
        techDescription type: "text"
        focus type: "text"
        taxonomyHints type: "text"
        notes type: "text"
        networkMembership type: "text"
    }

    static constraints = {
        hiddenJSON(nullable:true, blank: false)
        keywords(nullable:true)
        gbifCountryToAttribute(nullable:true, maxSize: 3)
    }

    /**
     * Returns a summary of the data provider including:
     * - id
     * - name
     * - acronym
     * - lsid if available
     * - description
     * - provider codes for matching with biocache records
     *
     * @return CollectionSummary
     */
    DataProviderSummary buildSummary() {
        DataProviderSummary dps = init(new DataProviderSummary()) as DataProviderSummary
        // safety
        if (resources) {
            def list = []
            def unused = resources.toString()  // workaround for odd problem where resources don't seem
                                               // to exist unless they are touched directly - lazy loading??
            resources.each { res ->
                if (res.hasProperty('uid')) {
                    list << [res.uid, res.name]
                } else {
                    log.error("problem accessing resources for uid = " + uid)
                }
            }
            dps.resources = list
        }
        def consumers = listConsumers()
        consumers.each {
            def pg = ProviderGroup._get(it)
            if (pg) {
                if (it[0..1] == 'co') {
                    dps.relatedCollections << [uid: pg.uid, name: pg.name]
                } else {
                    dps.relatedInstitutions << [uid: pg.uid, name: pg.name]
                }
            }
        }
        return dps
    }

    /**
     * Return the first related institution address if the provider does not have one.
     * @return
     */
    @Override def resolveAddress() {
        if (super.resolveAddress()) {
            return super.resolveAddress()
        }
        else {
            def pg = listConsumers().find {
                def related = _get(it)
                return related && related.resolveAddress()
            }
            if (pg) {
                return _get(pg).resolveAddress()
            }
            else {
                return null
            }
        }
    }

    /**
     * Returns the best available primary contact.
     * @return
     */
    @Override
    ContactFor inheritPrimaryContact() {
        if (getPrimaryContact()) {
            return getPrimaryContact()
        }
        else {
            for (con in listConsumers()) {
                def related = _get(con)
                if (related.inheritPrimaryContact()) {
                    return related.inheritPrimaryContact()
                }
            }
            return null
        }
    }

    /**
     * Returns the best available primary contact that can be published.
     * @return
     */
    @Override
    ContactFor inheritPrimaryPublicContact() {
        if (getPrimaryPublicContact()) {
            return getPrimaryPublicContact()
        }
        else {
            for (con in listConsumers()) {
                def related = _get(con)
                if (related.inheritPrimaryPublicContact()) {
                    return related.inheritPrimaryPublicContact()
                }
            }
            return null
        }
    }

    @Override
    def children() {
        return resources
    }

    long dbId() {
        return id;
    }

    String entityType() {
        return ENTITY_TYPE;
    }
}
