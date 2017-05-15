package au.org.ala.collectory

import au.org.ala.collectory.resources.DataSourceAdapter
import com.fasterxml.jackson.annotation.JsonInclude
/**
 * Configuration details for a data source.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2017 CSIRO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class DataSourceConfiguration {
    static mapWith = 'none'
    static hasMany = [resources: ExternalResourceBean]

    static constraints = {
    }

    String guid
    String name
    String description
    Class<DataSourceAdapter> adaptorClass
    URL endpoint
    String username
    String password
    String country
    String recordType
    String dataProviderUid
    Map<String, String> defaultDatasetValues
    List<String> keyTerms
    List<ExternalResourceBean> resources

    /**
     * Create an adaptor source from this configutation
     *
     * @return An initalised adaptor
     */
    DataSourceAdapter create() {
        return adaptorClass.newInstance(this)
    }

    def getAdaptorString() {
        return adaptorClass?.name
    }

    def setAdaptorString(String ac) {
        adaptorClass = Class.forName(ac)
    }
}
