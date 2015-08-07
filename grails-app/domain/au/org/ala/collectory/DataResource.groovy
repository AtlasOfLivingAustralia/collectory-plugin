package au.org.ala.collectory

import java.sql.Timestamp

class DataResource extends ProviderGroup implements Serializable {

    static final String ENTITY_TYPE = 'DataResource'
    static final String ENTITY_PREFIX = 'dr'

    static hasMany = [connections: DataConnection]

    static auditable = [ignore: ['version','dateCreated','lastUpdated','userLastModified']]

    static mapping = {
        sort: 'name'
        rights type:'text'
        citation type:'text'
        defaultDarwinCoreValues type:'text'
        harvestingNotes type:'text'
        mobilisationNotes type:'text'
        dataGeneralizations type:'text'
        informationWithheld type:'text'
        permissionsDocument type:'text'
        pubDescription type: "text"
        techDescription type: "text"
        focus type: "text"
        taxonomyHints type: "text"
        notes type: "text"
        networkMembership type: "text"
        connections sort: 'sequence', order: 'desc'

    }

    String rights
    String citation
    String licenseType = "other"
    String licenseVersion
    String resourceType = "records"
    String provenance
    String informationWithheld
    String dataGeneralizations
    String permissionsDocument      // location of the documentation of the right to use
    String permissionsDocumentType = "Other"  // type of document
    boolean riskAssessment = false  // has risk assessment been done (for Data Provider Agreements only)
    boolean filed = false           // has the document been filed (for Data Provider Agreements only)
    String status = "identified"    // integration status (of the integration of the resource into the atlas)
    String harvestingNotes          // may include which components (text, images, etc) can be harvested
    String mobilisationNotes        //
    int harvestFrequency = 0
    Timestamp lastChecked           // when the last check was made for new data
    Timestamp dataCurrency          // the date of production of the most recent data file
    SortedSet<DataConnection> connections = [] as SortedSet<DataConnection>    // The list of data connections, with the most current at the front
    String defaultDarwinCoreValues  // json string containing default values to use for missing DwC fields
    int downloadLimit = 0           // max number of records that can be included in a single download - 0 = no limit
    String contentTypes             // json array of type of content provided by the resource
    boolean publicArchiveAvailable = false  // true if a DwC archive is allowed to be downloaded

    DataProvider dataProvider
    Institution institution         // optional link to the institution whose records are served by this resource

    static constraints = {
        rights(nullable:true)
        citation(nullable:true)
        licenseType(nullable:true, maxSize:45, inList:licenseTypeList)
        licenseVersion(nullable:true, maxSize:45)
        resourceType(maxSize:255, validator: {
            return it in resourceTypeList
        })
        provenance(nullable:true,maxSize:45,inList: provenanceTypesList)
        dataProvider(nullable:true)
        institution(nullable:true)
        dataGeneralizations(nullable:true)
        informationWithheld(nullable:true)
        permissionsDocument(nullable:true)
        permissionsDocumentType(nullable:true, inList: permissionsDocumentTypes)
        status(maxSize:45)
        harvestingNotes(nullable:true)
        mobilisationNotes(nullable:true)
        lastChecked(nullable:true)
        dataCurrency(nullable:true)
        defaultDarwinCoreValues(nullable:true)
        contentTypes(nullable:true, maxSize:2048)
    }

    static transients =  ['creativeCommons']

    static resourceTypeList = ["records", "website", "document", "uploads", "species-list"]
    static creativeCommonsLicenses = ["CC BY", "CC BY-NC", "CC BY-SA", "CC BY-NC-SA"]
    static ccDisplayList = [
        [type:'CC BY',display:'Creative Commons Attribution'],
        [type:'CC BY-NC',display:'Creative Commons Attribution-NonCommercial'],
        [type:'CC BY-SA',display:'Creative Commons Attribution-ShareAlike'],
        [type:'CC BY-NC-SA',display:'Creative Commons Attribution-NonCommercial-ShareAlike'],
        [type:'other',display:'Some other or no license']]
    static licenseTypeList = creativeCommonsLicenses + ["other"]
    static permissionsDocumentTypes = ['','Email','Data Provider Agreement','Web Page','Other']
    static contentTypesList = ['authoritative','behaviour','commercial uses','common names','conservation management',
            'conservation status','description','distribution maps','distribution text','feeding and diet','habitat',
            'human interaction','identification keys','images','lifecycle','molecular','movies','pest management','pest status',
            'point occurrence data','population','references','reproduction','scientific names','sensitive species lists',
            'similar species','sound','species interactions','species list','taxonomy','threats']
    static provenanceTypesList = ['Individual sightings','Published dataset','Draft']
    /**
     * Integration status.
     * identified - Resource has been found but no further contact
     * inProgress - Resource has been contacted and discussions are underway about sharing
     * dataAvailable - Data for the resource has been loaded
     * linksAvailable - Links to the resource are used on atlas pages
     * declined - This resource is not to be harvested / will not be contributing at this time
     */
    static statusList = ['identified','inProgress','dataAvailable','linksAvailable','declined']

    boolean canBeMapped() {
        return false;
    }

    /**
     * Returns a summary of the data provider including:
     * - id
     * - name
     * - acronym
     * - lsid if available
     * - description
     * - data provider name, id and uid
     *
     * @return CollectionSummary
     */
    DataResourceSummary buildSummary() {
        DataResourceSummary drs = init(new DataResourceSummary()) as DataResourceSummary
        drs.dataProvider = dataProvider?.name
        drs.dataProviderId = dataProvider?.id
        drs.dataProviderUid = dataProvider?.uid
        drs.downloadLimit = downloadLimit

        drs.hubMembership = listHubMembership().collect { [uid: it.uid, name: it.name] }
        def consumers = listConsumers()
        consumers.each {
            def pg = ProviderGroup._get(it)
            if (pg) {
                if (it[0..1] == 'co') {
                    drs.relatedCollections << [uid: pg.uid, name: pg.name]
                } else {
                    drs.relatedInstitutions << [uid: pg.uid, name: pg.name]
                }
            }
        }
        // for backward compatibility
        if (drs.relatedInstitutions) {
            drs.institution = drs.relatedInstitutions[0].name
            drs.institutionUid = drs.relatedInstitutions[0].uid
        }
        return drs
    }

    /**
     * Returns a list of all hubs this resource belongs to.
     *
     * @return list of DataHub
     */
    List listHubMembership() {
        DataHub.list().findAll {it.isDataResourceMember(uid)}
    }

    /**
     * True if this resource uses a CC license.
     * @return
     */
    boolean isCreativeCommons() {
        return licenseType in creativeCommonsLicenses
    }

    /**
     * True if this resource provides records for any number of collections.
     * @return
     */
    boolean hasMappedCollections() {
        return listConsumers().size() as boolean
    }

    /**
     * Return the provider's address if the resource does not have one. If dp has no address try related entities.
     * @return
     */
    @Override def resolveAddress() {
        def addr = super.resolveAddress() ?: dataProvider?.resolveAddress()
        if (!addr) {
            def pg = listConsumers().find {
                def related = _get(it)
                return related && related.resolveAddress()
            }
            if (pg) {
                addr = _get(pg).resolveAddress()
            }
        }
        return addr
    }

    /**
     * Returns the entity that is responsible for creating this resource - the data provider if there is one.
     * @return
     */
    @Override def createdBy() {
        return dataProvider ? dataProvider.createdBy() : super.createdBy()
    }

    /**
     * Return the provider's logo if the resource does not have one.
     * @return
     */
    @Override def buildLogoUrl() {
        if (logoRef) {
            return super.buildLogoUrl()
        }
        else {
            return dataProvider?.buildLogoUrl()
        }
    }

    /**
     * Returns the best available primary contact.
     * @return
     */
    @Override
    ContactFor inheritPrimaryContact() {
        return getPrimaryContact() ?: dataProvider?.inheritPrimaryContact()
    }

    /**
     * Returns the best available primary contact that can be published.
     * @return
     */
    @Override
    ContactFor inheritPrimaryPublicContact() {
        return getPrimaryPublicContact() ?: dataProvider?.inheritPrimaryPublicContact()
    }

    @Override
    def parent() {
        return dataProvider
    }

    long dbId() {
        return id;
    }

    String entityType() {
        return ENTITY_TYPE;
    }

    String shortProviderName(int len) {
        return dataProvider?.name?.length() > len ? dataProvider.name[0..len] + ".." : dataProvider?.name
    }

    String shortProviderName() {
        return shortProviderName(30)
    }

    /**
     * Get the most recent data connection
     *
     * @return The most recent connection or null for none
     */
    DataConnection getCurrentConnection() {
        connections && connections.size() > 0 ? connections.first() : null
    }

    /**
     * Add a connection to the connection list.
     * <p>
     * The new connection is given a sequence number one greater than the current set
     * and inserted into the front of the list.
     *
     * @param connection The new connection
     */
    void addConnection(DataConnection connection) {
        connection.dataResource = this
        connection.sequence = connections.inject(0, { curr, conn -> Math.max(curr, conn.sequence) }) + 1
        connections.add(connection)
    }


}
