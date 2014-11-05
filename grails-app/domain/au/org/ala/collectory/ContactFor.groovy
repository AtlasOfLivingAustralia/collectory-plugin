package au.org.ala.collectory
/*  Represents a contact in the context of a specific group, eg institution,
 *  collection or dataset.
 *
 *  - based on collectory data model version 5
 */

class ContactFor implements Serializable {

    Contact contact
    String entityUid
    String role
    boolean administrator = false
    boolean primaryContact = false
    boolean notify = false

    Date dateCreated = new Date()
    Date dateLastModified = new Date()
    String userLastModified

    static auditable = [ignore: ['version','dateCreated','lastUpdated','userLastModified']]

    ContactFor () {}

    ContactFor (Contact contact, String entityUid, String role, boolean isAdministrator, boolean isPrimaryContact) {
        this.contact = contact
        this.entityUid = entityUid
        this.role = role
        this.administrator = isAdministrator
        this.primaryContact = isPrimaryContact
    }
    
    static mapping = {
        contact index: 'contact_id_idx'
        entityUid index: 'entity_uid_idx'
    }

    static constraints = {
        contact()
        entityUid(blank:false)
        role(nullable:true, maxSize:128)
        dateCreated()
        dateLastModified()
        userLastModified(maxSize:256)
        // could constrain primaryContact to only one for an entity
    }

    def print() {
        ["Contact id: " + contact.id,
         "Entity uid: " + entityUid,
         "Role: " + role,
         "isAdmin: " + administrator,
         "isPrimary: " + primaryContact,
         "notify: " + notify]
    }

    void setPrimaryContact(boolean value) {
        this.primaryContact = value
    }
}
