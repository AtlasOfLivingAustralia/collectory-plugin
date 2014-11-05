package au.org.ala.collectory

/**
 * Created by markew
 * Date: Sep 2, 2010
 * Time: 3:13:37 PM
 */
class DataProviderSummary extends ProviderGroupSummary {
    List resources
    List relatedCollections = []     // list of map with name: and uid:
    List relatedInstitutions = []    // list of map with name: and uid:
}
