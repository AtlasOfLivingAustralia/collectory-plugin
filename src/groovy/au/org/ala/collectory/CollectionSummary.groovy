package au.org.ala.collectory

/**
 * TO for the summary of a collection.
 */
class CollectionSummary extends ProviderGroupSummary {
    String institution
    String institutionId
    String institutionUid
    List derivedInstCodes
    List derivedCollCodes
    String institutionLogoUrl
    List relatedDataProviders = []    // list of map with name: and uid:
    List relatedDataResources = []    // list of map with name: and uid:
    List hubMembership = []
}
