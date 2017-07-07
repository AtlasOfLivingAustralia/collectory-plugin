package au.org.ala.collectory

/**
 * Loads institution names and codes from a file scrapped from AFD.
 *
 * Used to lookup institution codes when inserting institutions into the Collectory.
 * NOT used for looking up institution codes within the Collectory.
 */
class InstitutionCodeLoaderService {
    def grailsApplication

    static transactional = false
    static xml = null

    def lookupInstitutionCode(String institutionName) {
        if (!xml) {
            InputStream is = new URL(grailsApplication.config.institution.codeLoaderURL).openStream()
            xml = new XmlSlurper().parse(is)
            is.close()
        }

        String code = null
        xml.tr.each {
            String name = it.td[1]
            if (name.startsWith(institutionName)) {
                code = it.td[0]
            }
        }
        return code
    }
}
