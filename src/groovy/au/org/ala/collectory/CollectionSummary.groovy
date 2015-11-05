package au.org.ala.collectory

/**
 * TO for the summary of a collection.
 */
class CollectionSummary extends ProviderGroupSummary {

    String institutionName
    String institutionId
    String institutionUid

    String collectionId
    String collectionUid
    String collectionName

    List derivedInstCodes
    List derivedCollCodes
    String institutionLogoUrl
    List relatedDataProviders = []    // list of map with name: and uid:
    List relatedDataResources = []    // list of map with name: and uid:
    List hubMembership = []
}
