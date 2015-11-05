package au.org.ala.collectory

/**
 * @.history 2-8-2010 removed inst codes as these are now related only to collections (can be added back with a different mechanism if required)
 */
class InstitutionSummary extends ProviderGroupSummary {
//    List derivedInstCodes
    String institutionId
    String institutionUid
    String institutionName
    List collections
    List relatedDataProviders = []    // list of map with name: and uid:
    List relatedDataResources = []    // list of map with name: and uid:
    List hubMembership = []
}
