package au.org.ala.collectory

/**
 * Maps provider codes to provider groups.
 */
class ProviderMap implements Serializable {

    Collection collection

    static auditable = [ignore: ['version','dateCreated','lastUpdated']]

    // has many collection codes and institution codes
    static hasMany = [collectionCodes: ProviderCode, institutionCodes: ProviderCode]

    boolean matchAnyCollectionCode = false
    boolean exact = true
    String warning
    Date dateCreated
    Date lastUpdated

    static constraints = {
        warning(nullable:true)
    }

    static mapping = {
        sort: 'collection'
    }

    /*String toString() {
        return collection.name
    }*/

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
    static Collection findMatch(String institutionCode, String collectionCode) {
        if (!institutionCode || !collectionCode) {return null}
        def pm = ProviderMap.executeQuery("select distinct m from ProviderMap m left join m.institutionCodes ic left join m.collectionCodes cc " +
                "where ic.code = :inst and (cc.code = :coll or m.matchAnyCollectionCode = true)",
                [inst:institutionCode, coll:collectionCode])
        if (pm && pm.size() > 0) {
            return pm[0].collection
        }
        return null
    }

    static String findMatchUid(String institutionCode, String collectionCode) {
        return ProviderMap.findMatch(institutionCode, collectionCode)?.uid
    }


    /**
     * Very indirect way of looking up an institution.
     *
     * We can add explicit institution code lookup if it's required.
     * @param institution code the code to search for
     * @return an institution or null
     */
/*    static ProviderGroup findInstitution(String institutionCode) {
        if (!institutionCode) {return null}
        // look for any collection that has this institution code
        def pm = ProviderMap.executeQuery("select distinct m from ProviderMap m left join m.institutionCodes ic " +
                "where ic.code = :inst", [inst:institutionCode])
        if (pm && pm.size() > 0) {
            def pg = pm[0].providerGroup
            return pg.findPrimaryInstitution()
        }
        return null
    }*/
}
