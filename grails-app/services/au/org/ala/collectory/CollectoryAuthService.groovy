package au.org.ala.collectory
import grails.converters.JSON
import org.springframework.web.context.request.RequestContextHolder

class CollectoryAuthService{

    static transactional = false

    def grailsApplication
    def authService

    def username() {
        def username = 'not available'
        if(RequestContextHolder.currentRequestAttributes()?.getUserPrincipal()?.attributes?.email)
            username = RequestContextHolder.currentRequestAttributes()?.getUserPrincipal()?.attributes?.email
        else {
            if(authService)
                username = authService.email
        }

        return (username) ? username : 'not available'
    }

    /**
     * A user is an ADMIN if they have either the ROLE_ADMIN or ROLE_COLLECTION_ADMIN roles.
     *
     * @return
     */
    def isAdmin() {
        def adminFlag = false
        if(grailsApplication.config.security.cas.bypass.toBoolean())
            adminFlag = true
        else {
            if(authService) {
                adminFlag = authService.userInRole(ProviderGroup.ROLE_ADMIN) || authService.userInRole(ProviderGroup.ROLE_COLLECTION_ADMIN)
            }
        }
        return adminFlag
    }

    /**
     * A user is an EDITOR if they have either the ROLE_ADMIN or ROLE_COLLECTION_ADMIN roles.
     *
     * @return
     */
    def isEditor() {
        def adminFlag = false
        if(grailsApplication.config.security.cas.bypass.toBoolean()) {
            adminFlag = true
        } else {
            if(authService) {
                adminFlag = authService.userInRole(ProviderGroup.ROLE_COLLECTION_EDITOR) ||
                        authService.userInRole(ProviderGroup.ROLE_ADMIN) ||
                        authService.userInRole(ProviderGroup.ROLE_COLLECTION_ADMIN)
            }
        }
        return adminFlag
    }

    def getRoles(){
        def roles = []
        ProviderGroup.COLLECTORY_ROLES.each {
            if(authService.userInRole(it)){
                roles << it
            }
        }
        roles
    }

    protected boolean userInRole(role) {
        def roleFlag = false
        if(grailsApplication.config.security.cas.bypass.toBoolean())
            roleFlag = true
        else {
            if(authService != null) {
                roleFlag = authService.userInRole(role)
            }
        }

        return roleFlag || isAdmin()
    }

    /**
     * Returns a list of entities that the specified user is authorised to edit.
     *
     * Note that more than one contact may correspond to the user's email address. In this
     * case, the result is a union of the lists for each contact.
     *
     * @param email
     * @return a map holding entities, a list of their uids and the latest modified date
     */
    def authorisedForUser(String email) {
        def contacts = Contact.findAllByEmail(email)
        switch (contacts.size()) {
            case 0: return [sorted: [], keys: [], latestMod: null]
            case 1: return authorisedForUser(contacts[0])
            default:
                def result = [sorted: [], keys: [], latestMod: null]
                contacts.each {
                    def oneResult = authorisedForUser(it)
                    result.sorted += oneResult.sorted
                    result.keys += oneResult.keys
                    if (oneResult.latestMod > result.latestMod) { result.latestMod = oneResult.latestMod }
                }
                return result
        }
    }

    /**
     * If a logged in user is an administrator for a data resource then they can edit.
     * Likewise, if they are the administrator of a provider or institution they can edit
     * an institution/provider metadata and any resources underneath that institution/provider.
     *
     * @param userId
     * @param instance A dataresource, collection, provider or institution
     * @return
     */
    def isUserAuthorisedEditorForEntity(userId, instance){
        def authorised = false
        def reason = ""
        if(instance) {
            def contacts = instance.getContacts()
            contacts.each {
                if (it.contact.userId == userId && it.administrator) {
                    //CAS contact
                    authorised = true
                    reason = "User is an administrator for ${instance.entityType()} : ${instance.uid} : ${instance.name}"
                }
            }
        }

        if(instance instanceof DataResource){
            if(instance.getInstitution()){
                //check institution contacts
                def contacts = instance.getInstitution().getContacts()
                contacts.each {
                    if (it.contact.userId == userId && it.administrator) {
                        //CAS contact
                        authorised = true
                        reason = "User is an administrator for parent entity ${instance.entityType()} : ${instance.id} : ${instance.name}"
                    }
                }
            }
            if(instance.getDataProvider()){
                //check data provider contacts
                //check institution contacts
                def contacts = instance.getDataProvider().getContacts()
                contacts.each {
                    if (it.contact.userId == userId && it.administrator) {
                        //CAS contact
                        authorised = true
                        reason = "User is an administrator for parent entity ${instance.entityType()} : ${instance.id} : ${instance.name}"
                    }
                }
            }
        }
        [authorised:authorised, reason:reason]
    }

    /**
     * Returns a list of entities that the specified contact is authorised to edit.
     *
     * @param contact
     * @return a map holding entities, a list of their uids and the latest modified date
     */
    def authorisedForUser(Contact contact) {
        // get list of contact relationships
        def latestMod = null
        def entities = [:]  // map by uid to remove duplicates
        ContactFor.findAllByContact(contact).each {
            if (it.administrator) {
                def pg = ProviderGroup._get(it.entityUid)
                if (pg) {
                    entities.put it.entityUid, [uid: pg.uid, name: pg.name, entityType: pg.entityType()]
                    if (it.dateLastModified > latestMod) { latestMod = it.dateLastModified }
                }
                // add children
                pg.children().each { child ->
                    // children() now seems to return some internal class resources
                    // so make sure they are PGs
                    if (child instanceof ProviderGroup) {
                        def ch = ProviderGroup._get(child.uid)
                        if (ch) {
                            entities.put ch.uid, [uid: ch.uid, name: ch.name, entityType: ch.entityType()]
                        }
                    }
                }
            }
        }
        [sorted: entities.values().sort { it.name }, keys:entities.keySet().sort(), latestMod: latestMod]
    }

    def checkApiKey(key) {
        // try the preferred api key store first
        if(grailsApplication.config.security.apikey.checkEnabled.toBoolean()){
            def url = grailsApplication.config.security.apikey.serviceUrl + key
            def conn = new URL(url).openConnection()
            if (conn.getResponseCode() == 200) {
                return JSON.parse(conn.content.text as String)
            } else {
                log.info "Rejected change using key ${key}"
                return [valid:false]
            }
        } else {
            return [valid:true]
        }
    }
}
