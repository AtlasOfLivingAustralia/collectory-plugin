package au.org.ala.collectory

/**
 * Records user activity such as login and editing.
 */
class ActivityLog implements Serializable {

    Date timestamp                              // time of the event
    String user                                 // username
    String entityUid                            // id of the affected record if any
    boolean contactForEntity = false            // are they a contact for the record
    boolean administratorForEntity = false      // are they an administrator of the record
    String action                               // what did they do
    boolean admin = false                       // true if the user is sys admin (used to differentiate 'real' users in stats)

    static transients = ['Actions']
    
    static constraints = {
        timestamp()
        user(blank:false)
        entityUid(nullable:true)
        contactForEntity()
        administratorForEntity()
        action(blank:false)
    }

    static void log(params) {
        def al = new ActivityLog(params)
        al.timestamp = new Date()
        al.errors.each {println it}
        al.save(flush:true)
    }

    /**
     * Logs a simple action by a user, eg login, logout.
     * @param user
     * @param action
     */
    static void log(String user, boolean isAdmin, Action action) {
        //def a = Actions.valueOf(Actions.class, action)
        //def actionText = a ? a.toString() : action
        def al = new ActivityLog(timestamp: new Date(), user: user, admin: isAdmin, action: action.toString())
        al.errors.each {println it}
        al.save(flush:true)
    }

    /**
     * Logs an action that is not associated with a database entity, eg list all.
     * @param user
     * @param action
     * @param item
     */
    static void log(String user, boolean isAdmin, Action action, String item) {
        def al = new ActivityLog(timestamp: new Date(), user: user, admin: isAdmin, action: action.toString() + " " + item)
        al.validate()
        if (al.hasErrors()) {
            al.errors.each {println it}
        }
        al.save(flush:true)
    }

    /**
     * Logs an action taken on a ProviderGroup-type entity, eg create, edit.
     * @param user
     * @param uid
     * @param action
     */
    static void log(String user, boolean isAdmin, String uid, Action action) {
        ProviderGroup pg = ProviderGroup._get(uid)
        if (!pg) {
            log(user, isAdmin, action, " entity with uid = ${uid}")
            return
        }
        boolean isContact = false
        boolean isEntityAdmin = false
        Contact c = Contact.findByEmail(user)
        if (c) {
            ContactFor cf = ContactFor.findByContactAndEntityUid(c, uid)
            if (cf) {
                isContact = true
                isEntityAdmin = cf.isAdministrator()
            }
        }
        new ActivityLog(timestamp: new Date(), user: user, admin: isAdmin,
                entityUid: uid, contactForEntity:isContact,
                administratorForEntity: isEntityAdmin, action: action.toString()).save(flush:true)
    }

    /**
     * This form is used for logging actions on non-ProviderGroup types such as Contact
     * @param user the user making the change
     * @param id the db id of the contact
     * @param action the action taken
     */
    static void log(String user, boolean isAdmin, long id, Action action) {
        new ActivityLog(timestamp: new Date(), user: user, admin: isAdmin,
                entityUid: id as String, action: action.toString()).save(flush:true)
    }

    String toString() {
        def adm = admin ? " (admin)" : ""
        if (entityUid) {
            "${timestamp}: ${user}${adm} ${action} ${ProviderGroup._get(entityUid)?.name}"
        } else {
            "${timestamp}: ${user}${adm} ${action}"
        }
    }

}
