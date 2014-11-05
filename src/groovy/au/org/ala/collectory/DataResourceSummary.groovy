package au.org.ala.collectory

class DataResourceSummary extends ProviderGroupSummary {
    String dataProvider
    String dataProviderId
    String dataProviderUid
    String institution          // maintained for backward compatibility
    String institutionUid       // maintained for backward compatibility
    int downloadLimit
    List relatedCollections = []     // list of map with name: and uid:
    List relatedInstitutions = []    // list of map with name: and uid:
    List hubMembership = []
}