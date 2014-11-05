package au.org.ala.collectory
import grails.converters.JSON
import org.springframework.web.context.request.RequestContextHolder

class AuthService {

    static transactional = false

    def grailsApplication

    def username() {
        return (RequestContextHolder.currentRequestAttributes()?.getUserPrincipal()?.attributes?.email)?:'not available'
    }

    def isAdmin() {
        return grailsApplication.config.security.cas.bypass ||
                RequestContextHolder.currentRequestAttributes()?.isUserInRole(ProviderGroup.ROLE_ADMIN)
    }

    protected boolean userInRole(role) {
        return grailsApplication.config.security.cas.bypass ||
                RequestContextHolder.currentRequestAttributes()?.isUserInRole(role) ||
                isAdmin()
    }

    protected boolean isAuthorisedToEdit(uid) {
        if (grailsApplication.config.security.cas.bypass || isAdmin()) {
            return true
        } else {
            def email = RequestContextHolder.currentRequestAttributes()?.getUserPrincipal()?.attributes?.email
            if (email) {
                return ProviderGroup._get(uid)?.isAuthorised(email)
            }
        }
        return false
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
                    entities.put it.entityUid, [uid: pg.uid, name: pg.name]
                    if (it.dateLastModified > latestMod) { latestMod = it.dateLastModified }
                }
                // add children
                pg.children().each { child ->
                    // children() now seems to return some internal class resources
                    // so make sure they are PGs
                    if (child instanceof ProviderGroup) {
                        def ch = ProviderGroup._get(child.uid)
                        if (ch) {
                            entities.put ch.uid, [uid: ch.uid, name: ch.name]
                        }
                    }
                }
            }
        }
        return [sorted: entities.values().sort { it.name }, keys:entities.keySet().sort(), latestMod: latestMod]
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