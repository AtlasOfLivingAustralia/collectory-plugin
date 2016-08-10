/*
 * Copyright (C) 2011 Atlas of Living Australia
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
 */
package au.org.ala.collectory
/**
 * Represents a temporary data set that has been uploaded to the transient biocache.
 */
class TempDataResource {

    String uid              // with the form drtnnnnn (data resource temporary)
    String name             // the label supplied by the user

    // pseudo-contact
    String email            // from the user credentials
    String firstName
    String lastName

    String alaId

    Date dateCreated        // auto filled by grails
    Date lastUpdated

    int numberOfRecords     // determined from upload

    String webserviceUrl    // sandbox.ala.org.au/biocache-service
    String uiUrl            // sandbox.ala.org.au/ala-hub

    String status = 'draft'
    Boolean isContactPublic = false
    String description
    String dataGeneralisations
    String informationWithheld
    String license
    String citation
    String sourceFile
    String prodUid
    String keyFields
    String csvSeparator

    static constraints = {
        uid(maxSize: 20)
        name(nullable: true, maxSize:1024)
        email(nullable: true, maxSize:256)
        alaId(nullable: true, maxSize:256)
        firstName(nullable: true, maxSize: 255)
        lastName(nullable: true, maxSize: 255)
        webserviceUrl(nullable: true, maxSize: 255)
        uiUrl(nullable: true, maxSize: 255)
        description(nullable: true)
        license(nullable: true, inList: ['CCBY3Aus', 'CCBYNC3Aus', 'CCBY4Int', 'CCBYNC4Int', 'CC0'])
        citation(nullable: true)
        sourceFile(nullable: true)
        informationWithheld(nullable: true)
        dataGeneralisations(nullable: true)
        keyFields(nullable: true)
        csvSeparator(nullable: true, maxSize: 10)
        status(nullable: true, inList: ['draft', 'submitted', 'declined', 'dataAvailable', 'queuedForLoading'])
        prodUid(nullable: true, maxSize: 20)
        isContactPublic(nullable: true)
    }

    static auditable = [ignore: ['version','dateCreated','lastUpdated']]

    static transients = ['primaryContact','primaryPublicContact','publicContactsPrimaryFirst','contactsPrimaryFirst','type']

    static mapping = {
        uid index:'uid_idx'
        description type: 'text'
        dataGeneralisations type: 'text'
        informationWithheld type: 'text'
        citation type: 'text'
    }

    def urlForm() {
        return "tempDataResource"
    }

    def buildSummary() {
        return [name:name, uid:uid, email:email, firstName:firstName, lastName:lastName, alaId:alaId,
                dateCreated:dateCreated, lastUpdated:lastUpdated, numberOfRecords:numberOfRecords]
    }

    def makeAbstract(length) {
        return ""
    }


    // all this copied from ProviderGroup to support contacts
    // todo: make this extend providerGroup so all this is inherited

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
     *
     */
    ContactFor addToContacts(Contact contact, String role, boolean isAdministrator, boolean isPrimaryContact,
                             String modifiedBy) {
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
        if (uid) {
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
     * type says if the data is published or in sandbox.
     */
    String getType(){
        if(prodUid){
            return 'Production'
        }

        return 'Draft'
    }

}
