package au.org.ala.collectory

import org.codehaus.groovy.grails.validation.Validateable
import grails.converters.JSON
import java.text.NumberFormat
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException
import java.text.ParseException

/**
 * A command class for collecting and validating a collection instance.
 *
 * User: markew
 * Date: May 12, 2010
 */

// this annotation wires the validate/errors methods/properties
@Validateable
class CollectionCommand implements Serializable {

    // maps to Collection
    long id                     // the DB id of the collection
    String uid
    long version                // version of the collection
    
    String guid                 // this is not the DB id but a known identifier
                                // such as an LSID or institution code
    String name
    String acronym              //
    String pubDescription       // public description
    String techDescription      // technical description
    String focus                //
    Address address
    String latitude             // decimal latitude as string
    String longitude            // decimal longitude as string
    String state
    String websiteUrl
    au.org.ala.collectory.Image imageRef             // the main image to represent the entity
    String email
    String phone
    String notes

    /* Not used until we implement code editing within edit collection */
    String providerCodes       // a comma-separated list of the codes used for this entity by the owning institution

    List<String> networkMembership    // list of peak body names

    Institution institution     // the owning institution

    List<ContactFor> contacts = []

    List<String> collectionType // type of collection e.g live, preserved, tissue, DNA
    String keywords             // a comma-separated list of keywords
    String active               // see active vocab
    String numRecords           // total number of records held that are able to be digitised
    String numRecordsDigitised  // number of records that are digitised

    String states               // states and territories that are covered by the collection - see state vocab
	String geographicDescription// a free text description of where the data relates to
	String eastCoordinate       // furthest point East for this collection in decimal degrees
	String westCoordinate       // furthest point West for this collection in decimal degrees
	String northCoordinate      // furthest point North for this collection in decimal degrees
	String southCoordinate      // furthest point South for this collection in decimal degrees

	String startDate            // the start date of the period the collection covers
	String endDate	            // the end date of the period the collection covers

	List<String> kingdomCoverage = []      // the higher taxonomy that the collection covers - see kingdom_coverage vocab
                                // a space-separated string that can contain any number of these values:
                                // Animalia Archaebacteria Eubacteria Fungi Plantae Protista
    String scientificNames      // comma-separated list of sci names

    // sub collections modelled as two parallel lists of attributes - name + description
    List<Map> subCollections = []  // name + description for each sub-collection

    // maps to InfoSource
	String webServiceUri
    String webServiceProtocol

    // operational fields
    List<ContactFor> deletedContacts = []

    static transients = ['imageRef']

    static constraints = {
        guid(nullable:true, maxSize:45)
        name(blank:false, maxSize:128)
        acronym(nullable:true, maxSize:45)
        pubDescription(nullable:true, maxSize:2048)
        techDescription(nullable:true, maxSize:2048)
        focus(nullable:true, maxSize:2048)
        address(nullable:true)
        latitude(nullable:true, validator: { dd -> if (!dd) return true
            // must be convertable to a valid BigDecimal
            try { new BigDecimal(dd) } catch (NumberFormatException e) { return ['decimal.invalid'] }
            return true
        })
        longitude(nullable:true, validator: { dd -> if (!dd) return true
            try { new BigDecimal(dd) } catch (NumberFormatException e) { return ['decimal.invalid'] }
            return true
        })
        state(nullable:true, maxSize:45, inList: ['Australian Capital Territory', 'New South Wales', 'Queensland', 'Northern Territory', 'Western Australia', 'South Australia', 'Tasmania', 'Victoria'])
        websiteUrl(nullable:true, maxSize:256)
        imageRef(nullable:true)
        email(nullable:true, maxSize:256)
        phone(nullable:true, maxSize:45)
        notes(nullable:true, maxSize:2048)
        collectionType(nullable: true)
        providerCodes(nullable:true, maxSize:2048)
        networkMembership(nullable: true)

        institution(nullable:true)

        keywords(nullable:true, maxSize:1024)
        active(nullable:true, inList:['Active growth', 'Closed', 'Consumable', 'Decreasing', 'Lost', 'Missing', 'Passive growth', 'Static'])
        numRecords(nullable:true, validator: { ii -> if (!ii) return true
            try { NumberFormat.getIntegerInstance().parse(ii) } catch (ParseException e) { return ['number.invalid'] }
            return true
        })
        numRecordsDigitised(nullable:true, validator: { ii -> if (!ii) return true
            try { NumberFormat.getIntegerInstance().parse(ii) } catch (ParseException e) { return ['number.invalid'] }
            return true
        })
        states(nullable:true)
        geographicDescription(nullable:true)
        webServiceUri(nullable:true)
        webServiceProtocol(nullable:true)
        eastCoordinate(nullable:true, validator: { dd -> if (!dd) return true
            try { new BigDecimal(dd) } catch (NumberFormatException e) { return ['decimal.invalid'] }
            return true
        })
        westCoordinate(nullable:true, validator: { dd -> if (!dd) return true
            try { new BigDecimal(dd) } catch (NumberFormatException e) { return ['decimal.invalid'] }
            return true
        })
        northCoordinate(nullable:true, validator: { dd -> if (!dd) return true
            try { new BigDecimal(dd) } catch (NumberFormatException e) { return ['decimal.invalid'] }
            return true
        })
        southCoordinate(nullable:true, validator: { dd -> if (!dd) return true
            try { new BigDecimal(dd) } catch (NumberFormatException e) { return ['decimal.invalid'] }
            return true
        })
        startDate(nullable:true, maxSize:45)
        endDate(nullable:true, maxSize:45)
        kingdomCoverage(validator: { kc ->
                boolean ok = true
                kc.each {
                    if (!['Animalia', 'Archaebacteria', 'Eubacteria', 'Fungi', 'Plantae', 'Protista'].contains(it)) {
                        ok = false  // return false does not work here!
                    }
                }
                return ok
            })
        scientificNames(nullable:true)
        subCollections()
    }

    static List<String> collectionTypes() {[
            "archival",
            "art",
            "audio",
            "cellcultures",
            "electronic",
            "facsimiles",
            "fossils",
            "genetic",
            "living",
            "observations",
            "preserved",
            "products",
            "taxonomic",
            "texts",
            "tissue",
            "visual"]}

    static networkTypes = ["CHAH", "CHAFC", "CHAEC", "CHACM", "CAMD"]

    static List<String> kingdoms() {
        return ['Animalia', 'Archaebacteria', 'Eubacteria', 'Fungi', 'Plantae', 'Protista']
    }

    void addAsContact(Contact contact) {
        addAsContact contact, null, false
    }

    void addAsContact(Contact contact, String role, boolean administrator) {
        // create a temp id so we have a handle - unique within this contact list, and negative so we  can detect later
        // note: this contrivance shows we should generate ids in the app rather than in the database
        long id = -2
        contacts.each {
            id = Math.min(id, it.id - 1)
        }
        if (contact) {
            ContactFor cf = new ContactFor(contact:contact, entityUid:this.uid)
            cf.id = id
            if (role) cf.role = role
            cf.administrator = administrator
            contacts << cf
            // also remove from the deleted list in case we removed it in this flow
            deletedContacts.remove(deletedContacts.find{it.contact.id == contact.id})
        }
    }

    void removeAsContact(int contactForId) {
        // add to deleted
        deletedContacts << contacts.find{it.id == contactForId}
        // remove from contacts
        contacts.remove(contacts.find{it.id == contactForId})
    }

    void removeAsContact(Contact contact) {
        // move from contacts list to deletedContacts
        if (contact) {
            // first add to deleted
            deletedContacts << contacts.find{it.contact.id == contact.id}
            // then remove from contacts
            contacts.remove(contacts.find{it.contact.id == contact.id})
            /*// clone the list as we want to modify the real list
            new ArrayList<ContactFor>(contacts).each {
                if (it.contact.id == contact.id) {
                    contacts.remove(it)
                    deletedContacts << it
                }
            }*/
        }
    }

    void bindSubCollections(params) {
        def names = params.findAll { key, value ->
            key.startsWith('name_') && value
        }
        def subs = names.sort().collect { key, value ->
            def index = key.substring(5)
            def desc = params."description_${index}"
            return [name: value, description: desc ? desc : ""]
        }
        this.subCollections = []
        subs.each {
            this.subCollections.add it
        }
    }

    boolean load(long collectionId) {
        Collection collectionInstance = Collection.get(collectionId)
        if (!collectionInstance) {
            return false
        }

        // would be nice to load named props in one go but doesn't seem to work
        //println "coll props = " + collectionInstance.properties['guid', 'name']
        //this.properties['guid', 'name'] = collectionInstance.properties.entrySet()
        //println "cmd props = " + this.properties['guid', 'name']

        // load from Collection
        id = collectionId
        uid = collectionInstance.uid
        version = collectionInstance.version
        guid = collectionInstance.guid
        name = collectionInstance.name
        acronym = collectionInstance.acronym
        pubDescription = collectionInstance.pubDescription
        techDescription = collectionInstance.techDescription
        focus = collectionInstance.focus
        if (collectionInstance.address) {
            address = collectionInstance.address
        } else {
            // need an address object otherwise new address params will not be bound
            address = new Address()
        }
        latitude = loadBigDecimal(collectionInstance.latitude)
        longitude = loadBigDecimal(collectionInstance.longitude)
        state = collectionInstance.state
        websiteUrl = collectionInstance.websiteUrl
        imageRef = collectionInstance.imageRef
        email = collectionInstance.email
        phone = collectionInstance.phone
        notes = collectionInstance.notes
        //providerCodes = toCSVString(collectionInstance.providerCodes)
        networkMembership = toList(collectionInstance.networkMembership)

        institution = collectionInstance.institution

        contacts = collectionInstance.getContacts()

        collectionType = toList(collectionInstance.collectionType)
        keywords = toCSVString(collectionInstance.keywords)
        active = collectionInstance.active
        numRecords = loadInt(collectionInstance.numRecords)
        numRecordsDigitised = loadInt(collectionInstance.numRecordsDigitised)
        states = collectionInstance.states
        geographicDescription = collectionInstance.geographicDescription
        eastCoordinate = loadBigDecimal(collectionInstance.eastCoordinate)
        westCoordinate = loadBigDecimal(collectionInstance.westCoordinate)
        northCoordinate = loadBigDecimal(collectionInstance.northCoordinate)
        southCoordinate = loadBigDecimal(collectionInstance.southCoordinate)
        startDate = collectionInstance.startDate
        endDate = collectionInstance.endDate
        if (collectionInstance.kingdomCoverage) {
            kingdomCoverage = collectionInstance.kingdomCoverage.split(" ")
        }
        scientificNames = toCSVString(collectionInstance.scientificNames)
        loadSubCollections(collectionInstance.subCollections)

        // load from InfoSource
//        InfoSource infosource = collectionInstance.infoSource
//        if (infosource) {
//            webServiceUri = infosource.getWebServiceUri()
//            webServiceProtocol = infosource.getWebServiceProtocol()
//        }

        return true
    }

    /**
     * Saves the command object to its constituent domain objects.
     *
     * @param the owning collection
     * @param user the user that modified the data
     * @return the id of the created collection or an error code
     */
    long save(String user) {
        def collectionInstance = Collection.get(id)
        if (!collectionInstance) {
            return 0
        }
        collectionInstance.refresh()  // make sure we have the freshest version number
        if (collectionInstance.version > this.version) {
            return -2  // locking failure
        }
        return updateFromCommand(collectionInstance, user)
    }

    /**
     * Saves the command object by creating its constituent domain objects.
     *
     * @param the owning collection
     * @param user the user that modified the data
     * @return the id of the created collection
     */
    long create(String user, String uid) {
        // provider group
        def collectionInstance = new Collection()
        collectionInstance.uid = uid
        return updateFromCommand(collectionInstance, user)
    }

    long updateFromCommand(Collection collectionInstance, String user) {
        // TODO: should use a service call to transactionalise
        collectionInstance.properties['guid', 'name', 'acronym', 'focus',
                'pubDescription', 'techDescription', 'notes', 'institution',
                'websiteUrl', 'imageRef', 'state', 'email', 'phone', 'parents',
                'active', 'states', 'geographicDescription', 'startDate', 'endDate'] = this.properties
        if (address && !address.isEmpty()) {
            collectionInstance.address = address
        }
        ['longitude', 'latitude'].each {
            collectionInstance."${it}" = this."${it}" ? toBigDecimal(this."${it}") : ProviderGroup.NO_INFO_AVAILABLE // set value where null -> -1
        }
        //collectionInstance.providerCodes = toJSON(this.providerCodes)
        collectionInstance.networkMembership = toJSON(this.networkMembership)
        collectionInstance.collectionType = toJSON(this.collectionType)
        collectionInstance.kingdomCoverage = this.kingdomCoverage?.join(" ")
        ['numRecords', 'numRecordsDigitised'].each {
            collectionInstance."${it}" = this."${it}" ? toInt(this."${it}") : ProviderGroup.NO_INFO_AVAILABLE // set value where null -> -1
        }
        ['eastCoordinate', 'westCoordinate', 'northCoordinate', 'southCoordinate'].each {
            collectionInstance."${it}" = this."${it}" ? toBigDecimal(this."${it}") : ProviderGroup.NO_INFO_AVAILABLE // set value where null -> -1
        }
        collectionInstance.keywords = toJSON(this.keywords)
        collectionInstance.scientificNames = toJSON(this.scientificNames)
        collectionInstance.subCollections = toJSON(this.subCollections)

        collectionInstance.userLastModified = user

        // if creating a new collection we need to save first so we know the collection id for foreign keys
        if (!this.id) {
            try {
                collectionInstance.save(flush:true)
                if (collectionInstance.hasErrors()) {
                    collectionInstance.errors.each {println it}
                    return 0
                }
            } catch (HibernateOptimisticLockingFailureException e) {
                discardAll(collectionInstance)
                return -2
            }
        }

        // save changes to contacts
        contacts.each {
            // save only the new ones
            if (!it.id || it.id < 0) {
                it.userLastModified = user
                if (!it.entityUid) it.entityUid = collectionInstance.uid     // may only have id once collection has been saved
                it.save()
                if (it.hasErrors()) {
                    it.errors.each {println it}
                    return 0
                }
            }
        }

        // delete the link record for any contacts that were removed
        deletedContacts.each {
            ContactFor.executeUpdate("delete ContactFor where id = ?",[it.id])
        }

        // update infosource
        /*def infosourceChanged = false
        InfoSource infosource = collectionInstance.infoSource
        if (infosource == null) {
            // create new if there are some values to save
            if (webServiceUri || webServiceProtocol) {
                // create new infosource
                infosource = new InfoSource(title: "created for " + collectionInstance.name)
                infosource.setWebServiceUri webServiceUri
                infosource.setWebServiceProtocol webServiceProtocol
                infosource.addToCollections(collectionInstance)
                infosourceChanged = true
                collectionInstance.infoSource = infosource
            }
        } else if (infosource.getWebServiceUri() != webServiceUri || infosource.getWebServiceProtocol() != webServiceProtocol) {
            // infosources may serve many collections, therefore we don't want to modify it
            // unless this is its only collection
            // does the infosource have other collections?
            if (infosource.collections.size() > 1) {
                // it does - so make a clone to host our modifications
                collectionInstance.infoSource = new InfoSource(infosource.properties)
                // swap to the clone
                infosource = collectionInstance.infoSource
                infosource.title << " modified for " + collectionInstance.name
                infosource.collections = []
                infosource.addToCollections(collectionInstance)
            }
            // modify it
            infosource.setWebServiceUri webServiceUri
            infosource.setWebServiceProtocol webServiceProtocol
            infosourceChanged = true
        }
        if (infosourceChanged) {
            infosource.dateLastModified = new Date()
            infosource.userLastModified = user
            try {
                infosource.save(flush:true)
            } catch (HibernateOptimisticLockingFailureException e) {
                discardAll(collectionInstance)
                return -2
            }
            if (infosource.hasErrors()) {
                infosource.errors.each {println it}
                return 0
            }
        }*/

        try {
            collectionInstance.save(flush:true)
        } catch (HibernateOptimisticLockingFailureException e) {
            discardAll(collectionInstance)
            return -2
        }
        if (collectionInstance.hasErrors()) {
            collectionInstance.errors.each {println it}
            return 0
        }

        return collectionInstance.id
    }

    void discardAll(Collection collectionInstance) {
//        collectionInstance.infoSource?.discard()
        collectionInstance.institution.discard()
        collectionInstance.discard()
    }

    List<String> toList(String json) {
        if (json) {
            return JSON.parse(json).collect { it.toString() }
        } else {
            return []
        }
    }

    String toCSVString(String json) {
        if (json) {
            def list = JSON.parse(json)
            //return list.join(',')  // why doesn't this work?
            String str = ''
            list.each{(str) ? (str += "," + it) : (str += it)}
            return str
        }
        return null
    }

    String toJSON(String csvs) {
        if (csvs) {
            List codes = csvs?.tokenize('[, ]')
            return (codes as JSON).toString()
        }
        return null
    }

    String toJSON(list) {
        if (list == null || list.size() == 0)
            return null
        return (list as JSON).toString()
    }

    String loadInt(int ii) {
        if (ii == ProviderGroup.NO_INFO_AVAILABLE) {
            return ""
        } else {
            return ii?.toString()
        }
    }

    String loadBigDecimal(BigDecimal bd) {
        if (bd == ProviderGroup.NO_INFO_AVAILABLE) {
            return ""
        } else {
            return bd?.toString()
        }
    }

    int toInt(String value) throws NumberFormatException {
        return NumberFormat.getIntegerInstance().parse(value)
    }

    BigDecimal toBigDecimal(String value) throws NumberFormatException {
        return new BigDecimal(value)
    }

    void loadSubCollections(String subs) {
        if (subs) {
            JSON.parse(subs).each {
                subCollections << [name: it.name, description: it.description]
            }
        }
    }

}
