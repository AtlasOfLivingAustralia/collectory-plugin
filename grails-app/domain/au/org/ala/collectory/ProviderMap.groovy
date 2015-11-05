package au.org.ala.collectory

/**
 * Maps provider codes to provider groups.
 */
class ProviderMap implements Serializable {

    Institution institution
    Collection collection

    static auditable = [ignore: ['version', 'dateCreated', 'lastUpdated']]

    // has many collection codes and institution codes
    static hasMany = [collectionCodes: ProviderCode, institutionCodes: ProviderCode]

    boolean matchAnyCollectionCode = false
    boolean exact = true
    String warning
    Date dateCreated
    Date lastUpdated

    static constraints = {
        warning(nullable: true)
        collection(nullable: true)
        institution(nullable: true)
    }

    static mapping = {
        sort: 'collection'
        sort: 'institution'
    }

    boolean matches(String institutionCode, String collectionCode) {
        return institutionCodes*.code.contains(institutionCode) &&
                (matchAnyCollectionCode || collectionCodes*.code.contains(collectionCode))
    }

    /**
     * Return the group that matches this combination of codes.
     *
     * Assumes both codes are mandatory.
     * Both must match unless matchAnyCollectionCode is true.
     *
     * @param institutionCode
     * @param collectionCode
     * @return
     */
    static ProviderGroup findMatch(String institutionCode, String collectionCode) {
        if (!institutionCode || !collectionCode) {
            return null
        }
        def pm = ProviderMap.executeQuery("select distinct m from ProviderMap m left join m.institutionCodes ic left join m.collectionCodes cc " +
                "where ic.code = :inst and (cc.code = :coll or m.matchAnyCollectionCode = true)",
                [inst: institutionCode, coll: collectionCode])
        if (pm && pm.size() > 0) {
            if( pm[0].collection){
                return pm[0].collection
            } else {
                return pm[0].institution
            }
        }
        return null
    }

    static String findMatchUid(String institutionCode, String collectionCode) {
        return ProviderMap.findMatch(institutionCode, collectionCode)?.uid
    }
}
