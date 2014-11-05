package au.org.ala.collectory

/**
 * Loads institution names and codes from a file scrapped from AFD.
 *
 * Used to lookup institution codes when inserting institutions into the Collectory.
 * NOT used for looking up institution codes within the Collectory.
 */
class InstitutionCodeLoaderService {

    static transactional = false
    static xml = null
    static final String INPUT_FILE = '/data/collectory/bootstrap/institution_codes.xml'

    def lookupInstitutionCode(String institutionName) {
        if (!xml) {
            xml = new XmlSlurper().parse(new File(INPUT_FILE))
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
