/***************************************************************************
 * Copyright (C) 2010 Atlas of Living Australia
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ***************************************************************************/

package au.org.ala.collectory

import grails.converters.JSON
import grails.util.Holders

/**
 *  Base class for an organisational group in the collectory, such as an
 *  institution or collection.
 *
 *  - based on collectory data model version 4
 *
 *  NOTE: class name changed to ProviderGroup as Group is a reserved word in many persistence languages
 *
 * .@history 2010-04-23 MEW Replaced String location with BigDecimal latitude, longitude and String altitude
 * .@history 2010-05-27 MEW Refactored model
 * .@history 2010-06-02 MEW Renamed providerCodes, changed to list, added internal mirror field
 * .@history 2010-07-02 MEW Replaced providerCodes with ProviderCode and ProviderMap tables
 * .@history 2010-08-02 MEW Refactored using inheritance
 * --further history in SVN comments and release notes--
 */
abstract class ProviderGroup implements Serializable {

    static final int NO_INFO_AVAILABLE = -1
    static final String LSID_PREFIX = 'urn:lsid:'
    static final int ABSTRACT_LENGTH = 250
    // for want of somewhere appropriate to put these:
    //static final String ROLE_ADMIN = 'ROLE_COLLECTION_ADMIN'
    //static final String ROLE_EDITOR = 'ROLE_COLLECTION_EDITOR'
    static final String ROLE_ADMIN = 'ROLE_ADMIN'
    static final String ROLE_EDITOR = 'ROLE_EDITOR'
    // general attributes
    String guid                 // this is not the DB id but a known identifier
                                // such as an LSID or institution code
    String uid                  // ALA assigned identifier for matching across sub-systems
    String name
    String acronym              //
    //String groupType          // what sort of entity this is - eg institution, collection, project
    String pubShortDescription  // public short description
    String pubDescription       // public description
    String techDescription      // technical description
    String focus                //
    Address address
    // Address postalAddress
    BigDecimal latitude = NO_INFO_AVAILABLE     // decimal latitude
    BigDecimal longitude = NO_INFO_AVAILABLE    // decimal longitude
    String altitude             // may include units eg 700m
    String state
    String websiteUrl
    Image logoRef              // identifies the entity's logo within the image store
    Image imageRef             // the main image to represent the entity
    String email
    String phone
    boolean isALAPartner = false
    String notes
    String networkMembership    // a list of names of networks (CHAH, etc) that the group belongs to as JSON list
    String attributions = ''    // list of space-separated uids for attributions
    String taxonomyHints        // JSON object holding hints for taxonomic coverage
    Date dateCreated
    Date lastUpdated
    String userLastModified
    String keywords             // json list of terms
    String gbifRegistryKey      // the entity identifier in the GBIF central registry (used for all GBIF.org API calls)

    static embedded = ['address', 'logoRef', 'imageRef']

    static transients = ['primaryInstitution', 'primaryContact', 'memberOf', 'networkTypes', 'mappable','ALAPartner',
        'primaryPublicContact','publicContactsPrimaryFirst','contactsPrimaryFirst', 'authorised']

    //to be externalised or managed in a DB table or file
    static networkTypes = ["CHAH", "CHAFC", "CHAEC", "CHACM", "CAMD"]

    static statesList = ['Australian Capital Territory', 'New South Wales', 'Queensland', 'Northern Territory', 'Western Australia', 'South Australia', 'Tasmania', 'Victoria']

    static mapping = {
        tablePerHierarchy false
        uid index:'uid_idx'
        pubShortDescription type: "text"
        pubDescription type: "text"
        techDescription type: "text"
        focus type: "text"
        taxonomyHints type: "text"
        notes type: "text"
        networkMembership type: "text"
    }

    static constraints = {
        guid(nullable:true, maxSize:256)
        uid(blank:false, maxSize:20)
        name(blank:false, maxSize:1024)
        acronym(nullable:true, maxSize:45)
        pubShortDescription(nullable:true, maxSize:100)
        pubDescription(nullable:true)
        techDescription(nullable:true)
        focus(nullable:true)
        address(nullable:true)
        latitude(max:360.0, min:-360.0, scale:10)
        longitude(max:360.0, min:-360.0, scale:10)
        altitude(nullable:true)
        state(nullable:true, maxSize:45)
        websiteUrl(nullable:true, maxSize:256)
        logoRef(nullable:true)
        imageRef(nullable:true)
        email(nullable:true, maxSize:256)
        phone(nullable:true, maxSize:200)
        isALAPartner()
        notes(nullable:true)
        networkMembership(nullable:true, maxSize:256)
        attributions(nullable:true, maxSize:256)
        taxonomyHints(nullable:true)
        keywords(nullable:true)
        gbifRegistryKey(nullable:true, maxSize:36)
    }

    /**
     * Adds a contact for this group using the supplied relationship attributes
     *
     * Contact relationships are handled statically because the relationship has attributes.
     *
     * @param contact the contact
     * @param role the role this contact has for this group
     * @param isAdministrator whether this contact is allowed to administer this group
     * @param isPrimaryContact whether this contact is the one that should be displayed as THE contact
     * @param modifiedBy the user that made the change
     * @return the ContactFor created
     */
    ContactFor addToContacts(Contact contact, String role, boolean isAdministrator, boolean isPrimaryContact, String modifiedBy) {
        // safety net - if there is no id we can't do this - will happen if the save fails without detection
        if (dbId() == null) {
            return
        }
        def cf = new ContactFor()
        cf.contact = contact
        cf.entityUid = uid
        cf.role = role?.empty ? null : role
        cf.administrator = isAdministrator
        cf.primaryContact = isPrimaryContact
        cf.userLastModified = modifiedBy
        cf.save(flush: true)
        if (cf.hasErrors()) {
            cf.errors.each {println it.toString()}
        }
        return cf
    }

    /**
     * Gets a list of contacts along with their role and admin status for this group
     *
     */
    List<ContactFor> getContacts() {
        // handle this being called before it has been saved (and therefore doesn't have an id - and can't have contacts)
        if (dbId()) {
            return ContactFor.findAllByEntityUid(uid)
        } else {
            []
        }
    }

    /**
     * Return the contact that should be displayed for this group.
     *
     * @return primary ContactFor (contains the contact and the role for this collection)
     */
    ContactFor getPrimaryContact() {
        List<ContactFor> list = getContacts()
        switch (list.size()) {
            case 0: return null
            case 1: return list[0]
            default:
                ContactFor result = null
                for (cf in list) {
                    if (cf.primaryContact)  // definitive (as long as there is only one primary)
                        return cf
                }
                if (!result) result = list[0]  // just take one
                return result
        }
    }

    /**
     * Return the contact that should be displayed for this group filtered
     * to only include those with the 'public' attribute.
     *
     * @return primary ContactFor (contains the contact and the role for this collection)
     */
    ContactFor getPrimaryPublicContact() {
        List<ContactFor> list = getContacts()
        switch (list.size()) {
            case 0: return null
            case 1: return list[0]
            default:
                ContactFor result = null
                for (cf in list) {
                    if (cf.primaryContact && cf.contact.publish)  // definitive (as long as there is only one primary)
                        return cf
                }
                // filter for publish then take the first
                if (!result) result = list.findAll({it.contact.publish})[0]  // just take one
                return result
        }
    }

    /**
     * Returns the best available primary contact by using inheritance and related entities.
     *
     * Sub-classes override for their particular relationships.
     * @return
     */
    ContactFor inheritPrimaryContact() {
        return getPrimaryContact()
    }

    /**
     * Returns the best available primary contact by using inheritance and related entities
     * filtered to only include those with the 'public' attribute.
     *
     * Sub-classes override for their particular relationships.
     * @return
     */
    ContactFor inheritPrimaryPublicContact() {
        return getPrimaryPublicContact()
    }

    /**
     * Return all contacts for this group with the primary contact listed first.
     *
     * @return list of ContactFor (contains the contact and the role for this collection)
     */
    List<ContactFor> getContactsPrimaryFirst() {
        List<ContactFor> list = getContacts()
        if (list.size() > 1) {
                for (cf in list) {
                    if (cf.primaryContact) {
                        // move it to the top
                        Collections.swap(list, 0, list.indexOf(cf))
                        break
                    }
                }
        }
        return list
    }

    /**
     * Return all contacts for this group with the primary contact listed first filtered
     * to only include those with the 'public' attribute.
     *
     * @return list of ContactFor (contains the contact and the role for this collection)
     */
    List<ContactFor> getPublicContactsPrimaryFirst() {
        List<ContactFor> list = getContacts().findAll {it.contact.publish}
        if (list.size() > 1) {
                for (cf in list) {
                    if (cf.primaryContact) {
                        // move it to the top
                        Collections.swap(list, 0, list.indexOf(cf))
                        break
                    }
                }
        }
        return list
    }

    /**
     * Deletes the linkage between the contact and this group
     */
    void deleteFromContacts(Contact contact) {
        ContactFor.findByEntityUidAndContact(uid, contact)?.delete()
    }

    /**
     * Add an external identifier to this object
     *
     * @param identifier The identifier
     * @param source The identifier source (eg. 'GBIF')
     * @param link A link to the oreiginal source
     * @return
     */
    ExternalIdentifier addExternalIdentifier(String identifier, String source, String link) {
        ExternalIdentifier ext = new ExternalIdentifier(entityUid: uid, identifier: identifier, source: source, uri: link)

        ext.save(flush: true)
        if (ext.hasErrors()) {
            ext.errors.each { println it.toString() }
        }
        return ext
    }

    /**
     * Get the external identifiers associated with this entity
     *
     * @return The external identifiers
     */
    List<ExternalIdentifier> getExternalIdentifiers() {
        if (id != null) {
            return ExternalIdentifier.findAllByEntityUid(uid)
        } else {
            return []
        }
    }

    /**
     * Remove an external identifier
     *
     * @param identifier
     */
    void deleteExternalIdentifier(ExternalIdentifier identifier) {
        ExternalIdentifier.findByEntityUidAndIdentifierAndSource(uid, identifier.identifier, identifier.source)?.delete()
    }

    /**
     * Determines whether the person with the specified email has the rights to edit this entity.
     * A person is authorised to edit if:
     * 1) they are a contact for this entity with administrator privilege,
     * 2) they are a contact for the parent of this entity with administrator privilege.
     *
     * @param email the email (username) of the person to check
     * @return true if the person has rights to edit
     */
    boolean isAuthorised(email) {
        // get contact
        Contact c = Contact.findByEmail(email)
        if (c) {
            ContactFor cf = ContactFor.findByContactAndEntityUid(c, this.uid)
            if (cf?.administrator) {
                return true
            } else {
                // check parent
                return parent()?.isAuthorised(email)
            }
        }
        return false
    }

    /**
     * Returns a truncated name.
     */
    String toString() {
        return name.substring(0, Math.min(60, name.size()))
    }

    boolean isMemberOf(String network) {
        if (!networkMembership) {
            return false
        }
        return (networkMembership =~ network)
    }

    /**
     * Trims the passed string to the specified length breaking at word boundaries and adding an ellipsis if trimmed.
     */
    def trimLength = {trimString, stringLength ->

        String concatenateString = "..."
        List separators = [".", " "]

        if (stringLength && (trimString?.length() > stringLength)) {
            trimString = trimString.substring(0, stringLength - concatenateString.length())
            String separator = separators.findAll{trimString.contains(it)}?.min{trimString.lastIndexOf(it)}
            if(separator){
                trimString = trimString.substring(0, trimString.lastIndexOf(separator))
            }
            trimString += concatenateString
        }
        return trimString
    }

    /**
     * Returns descriptive text trimmed to the default abstract length.
     *
     * @return abstract
     */
    String makeAbstract() {
        makeAbstract(ABSTRACT_LENGTH)
    }

    /**
     * Returns descriptive text trimmed to the lesser of the first newline and the specified length.
     *
     * @return abstract
     */
    String makeAbstract(int length) {
        // accumulate text
        String chunk = ""
        if (pubDescription) {
            chunk = pubDescription
        } else if (techDescription) {
            chunk = techDescription
        } else if (focus) {
            chunk = focus
        }
        // break at first newline
        def chunks = chunk.tokenize('\n')
        if (chunks.size()) {
            chunk = chunks[0]
        }
        // add second token if first is short
        if (chunk.size() < 40 && chunks.size() > 1) {
            chunk += " " + chunks[1]
        }
        // trim if still too long
        if (chunk.size() < length) {
            return (chunk) ? chunk : ""
        } else {
            return trimLength(chunk, length)
        }
    }

    /**
     * Returns the identifier part of a link that is optimised for permanence.
     * Should always be the UID.
     *
     * @return an identifier
     */
    String generatePermalink() {
        if (uid) {
            return uid
        }
        if (guid?.startsWith(LSID_PREFIX)) {
            return guid
        }
        if (acronym) {
            return acronym
        }
        return id
    }

    /**
     * Returns list of name/url for where the information about this collection was sourced.
     * @return list of Attribution
     */
    List<Attribution> getAttributionList() {
        def uids = attributions.tokenize(' ')
        List<Attribution> list = []
        uids.each {
            def att = Attribution.findByUid(it as String)
            if (att) {
                list << att
            }
        }
        return list
    }

    /**
     * Adds the specified attribution to this group.
     * @param attributionUid
     */
    void addAttribution(String attributionUid) {
        attributions = attributions ?: ""
        if (!hasAttribution(attributionUid)) {
            attributions += (attributions?' ':'') + attributionUid
        }
    }

    boolean hasAttribution(String attributionUid) {
        return attributions =~ /\b${attributionUid}\b/
    }

    void removeAttribution(String attributionUid) {
        def uids = attributions.tokenize(' ')
        uids.remove attributionUid
        attributions = uids.join(' ')
    }

    /*
     * Injects common group attributes into the summary object.
     */
    protected ProviderGroupSummary init(ProviderGroupSummary pgs) {
        pgs.id = dbId()
        pgs.uid = uid
        pgs.uri = buildUri()
        pgs.name = name
        pgs.acronym = acronym
        pgs.shortDescription = makeAbstract()
        if (guid?.startsWith('urn:lsid:')) {
            pgs.lsid = guid
        }
        pgs.taxonomyCoverageHints = JSONHelper.taxonomyHints(taxonomyHints)
        return pgs
    }

    /**
     * Returns the entity type, one of:
     * Collection, Institution, DataProvider, DataResource, DataHub
     *
     * @param uid
     * @return
     */
    static String entityTypeFromUid(String uid) {
        if (!uid) {return ""}
        switch (uid[0..1]) {
            case Institution.ENTITY_PREFIX: return Institution.ENTITY_TYPE
            case Collection.ENTITY_PREFIX: return Collection.ENTITY_TYPE
            case DataProvider.ENTITY_PREFIX: return DataProvider.ENTITY_TYPE
            case DataResource.ENTITY_PREFIX: return DataResource.ENTITY_TYPE
            case DataHub.ENTITY_PREFIX: return DataHub.ENTITY_TYPE
        }
    }

    /**
     * Returns the form that can be used in url path, ie as a controller name, one of:
     * collection, institution, dataProvider, dataResource, dataHub
     *
     * @param entityType short class name of entity
     * @return
     */
    static String urlFormOfEntityType(String entityType) {
        return entityType[0..0].toLowerCase() + entityType[1..-1]
    }

    /**
     * Returns the form that can be used in url path, eg as a controller name, one of:
     * collection, institution, dataProvider, dataResource, dataHub - based on the uid.
     *
     * @param uid
     */
    static String urlFormFromUid(String uid) {
        return urlFormOfEntityType(entityTypeFromUid(uid))
    }

    /**
     * Returns the form that can be used in plain text, one of:
     * collection, institution, data provider, data resource, data hub
     *
     * @param uid
     * @return
     */
    static String textFormOfEntityType(String uid) {
        String entityType = entityTypeFromUid(uid)
        String result = ""
        entityType.each {
            if (Character.isUpperCase(it[0] as Character)) {
                result += " " + it.toLowerCase()
            } else {
                result += it
            }
        }
        return result
    }

    /**
     * Returns the instance identified by the uid.
     *
     * @param uid
     * @return
     */
    static ProviderGroup _get(String uid) {
        if (!uid || uid.size() < 3) {return null}
        switch (uid[0..1]) {
            case Institution.ENTITY_PREFIX: return Institution.findByUid(uid)
            case Collection.ENTITY_PREFIX: return Collection.findByUid(uid)
            case DataProvider.ENTITY_PREFIX: return DataProvider.findByUid(uid)
            case DataResource.ENTITY_PREFIX: return DataResource.findByUid(uid)
            case DataHub.ENTITY_PREFIX: return DataHub.findByUid(uid)
            default: return null
        }
    }

    /**
     * Returns the instance identified by the uid.
     *
     * @param uid
     * @return
     */
    static ProviderGroup _get(String id, String entityType) {
        try {
            switch (entityType.toLowerCase()) {
                case Institution.ENTITY_TYPE.toLowerCase(): return Institution.findById(id)
                case Collection.ENTITY_TYPE.toLowerCase(): return Collection.findById(id)
                case DataProvider.ENTITY_TYPE.toLowerCase(): return DataProvider.findById(id)
                case DataResource.ENTITY_TYPE.toLowerCase(): return DataResource.findById(id)
                case DataHub.ENTITY_TYPE.toLowerCase(): return DataHub.findById(id)
                default: return null
            }
        } catch (Exception e){
            return null
        }
    }

    /**
     * Returns a summary object that extends ProviderGroupSummary and is specific to the type of entity.
     * @return summary object
     */
    abstract ProviderGroupSummary buildSummary()

    /**
     * Returns a list of UIDs of data providers and data resources that contribute records to the entity.
     * @return
     */
    def List<String> listProviders() {
        DataLink.findAllByConsumer(this.uid).collect {it.provider}
    }

    /**
     * Returns a list of UIDs of institutions and collections that consume records from the entity.
     * @return
     */
    def List<String> listConsumers() {
        DataLink.findAllByProvider(this.uid).collect {it.consumer}
    }

    /**
     * Returns taxonomy hints as a list of maps where each contains a rank and a name.
     *
     * @return List<Map>
     */
    def listTaxonomyHints() {
        def result = []
        if (taxonomyHints) {
            JSON.parse(taxonomyHints).coverage?.each {
                def key = it.keySet().iterator().next()
                result << [rank: key, name: it[key]]
            }
        }
        return result
    }

    /**
     * Returns taxonomic range as a list of taxon names.
     *
     * @return List<String>
     */
    def listTaxonomicRange() {
        def result = []
        if (taxonomyHints) {
            JSON.parse(taxonomyHints).range?.each {
                result << it
            }
        }
        return result
    }

    /**
     * Returns the rank at which to initially display taxonomic breakdowns.
     *
     * @return hint or null if no hint declared
     */
    def startingRankHint() {
        if (taxonomyHints) {
            def hint = JSON.parse(taxonomyHints).startRank
            return hint ?: null
        }
        return null
    }

    boolean canBeMapped() {
        if (latitude != 0.0 && latitude != -1 && longitude != 0.0 && longitude != -1) {
            return true
        }
        return false
    }

    /*
     * The database id is not injected into this class but the subclass that is actually mapped to
     * the database. Therefore all references to the id from this base class must use this method
     * (which is implemented in the subclass) to access the database id.
     */
    abstract long dbId()

    abstract String entityType()

    /**
     * Returns type with lower first char suitable for urls, eg institution, dataProvider
     * @return
     */
    String urlForm() {
        return ProviderGroup.urlFormOfEntityType(entityType())
    }

    /**
     * Returns the uri to the data representation of this entity.
     * @return
     */
    String buildUri() {
        return Holders.config.grails.serverURL + "/ws/" + urlForm() + "/" + uid
    }

    /**
     * Returns the url to the public representation of this entity in the collectory.
     * @return
     */
    String buildPublicUrl() {
        return Holders.config.grails.serverURL + "/public/show/" + uid
    }

    /**
     * Returns the url to the logo image for this entity.
     * @return
     */
    def buildLogoUrl() {
        return logoRef?.file ? Holders.config.grails.serverURL + "/data/" + urlForm() + "/" + URLEncoder.encode(logoRef.file, 'UTF-8') :
            ""
    }

    /**
     * Returns the parent entity if one exists else null.
     *
     * This method should be overridden by subclasses for their specific relationships.
     * @return the parent if any
     */
    def parent() {
        return null
    }

    /**
     * Returns a list of child entities.
     *
     * This method should be overridden by subclasses for their specific relationships.
     * @return list of ProviderGroup
     */
    def children() {
        return []
    }

    /**
     * Returns the entity's address or the address of a parent or related entity.
     *
     * This method should be overridden by subclasses for their specific relationships.
     * @return the best available address
     */
    def resolveAddress() {
        return address
    }

    /**
     * Returns the entity responsible for publishing this resource.
     *
     * This method should be overridden by subclasses for their specific relationships.
     * @return the best available creator
     */
    def createdBy() {
        return this
    }

    /**
     * Entities are the same if they have the same uid.
     *
     * @param obj to compare
     * @return true if same
     */
    def boolean equals(Object obj) {
        return obj instanceof ProviderGroup && uid == obj?.uid
    }
}

/**
 * Standardised form of an address.
 *
 * Used 'in-line' in ProviderGroup, ie does not create a separate table.
 */
//class Address implements Serializable {
//    static final long serialVersionUID = 1L;//1681261914339207268L;
//
//    String street           // includes number eg 186 Tinaroo Creek Road
//    String postBox          // eg PO Box 2104
//    String city
//    String state            // full name eg Queensland
//    String postcode
//    String country
//
//    static transients = ['empty']
//
//    static constraints = {
//        street(nullable:true)
//        postBox(nullable:true)
//        city(nullable:true)
//        state(nullable:true)
//        postcode(nullable:true)
//        country(nullable:true)
//    }
//
//    boolean isEmpty() {
//        return [street, postBox, city, state, postcode, country].every {!it}
//        //return !(street || postBox || city || state || postcode || country)
//    }
//
//    List<String> nonEmptyAddressElements(includePostal) {
//        def fields = ['street','city','state','postcode']
//        if (includePostal) {fields << 'postBox'}
//        List<String> elements = []
//        fields.each {
//            if (this."${it}") {
//                elements << this."${it}"
//            }
//        }
//        return elements
//    }
//
//    String buildAddress() {
//        return nonEmptyAddressElements(false).join(" ")
//    }
//
//    def String toString() {
//        return nonEmptyAddressElements(true).join(" ")
//    }
//
//    def boolean equals(Object obj) {
//        return obj instanceof Address &&
//                street == obj.street &&
//                postBox == obj.postBox &&
//                city == obj.city &&
//                state == obj.state &&
//                postcode == obj.postcode &&
//                country == obj.country
//    }
//}

/**
 * Standardised form of an image reference.

 *
 * Used 'in-line' in ProviderGroup, ie does not create a separate table.
 */
//class Image implements Serializable {
//    static final long serialVersionUID = 1L;
//
//    String file
//    String caption
//    String attribution
//    String copyright
//
//    static constraints = {
//        file(blank:false)
//        caption(nullable:true)
//        attribution(nullable:true)
//        copyright(nullable:true)
//    }
//
//    def String toString() {
//        return ([file,caption,attribution,copyright].findAll {it}).join(", ")
//    }
//
//    def boolean equals(Object obj) {
//        return obj instanceof Image && file == obj.file && caption == obj.caption &&
//                attribution == obj.attribution && copyright == obj.copyright
//    }
//
//
//}
