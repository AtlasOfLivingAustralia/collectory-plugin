package au.org.ala.collectory.resources

import au.org.ala.collectory.DataSourceConfiguration
import au.org.ala.collectory.ExternalResourceBean
import au.org.ala.collectory.exception.ExternalResourceException
/**
 * Interface for accessing dataset and occurrence information.
 * <p>
 * Subclasses are responsible for connecting to a metadata/data source and accessing it.
 * </p>
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2017 CSIRO
 */
abstract class DataSourceAdapter {
    DataSourceConfiguration configuration

    /**
     * Construct for a configuration
     *
     * @param configuration The configuration
     */
    DataSourceAdapter(DataSourceConfiguration configuration) {
        this.configuration = configuration
    }

    /**
     * Get a list of datasets to update in the form of external resource beans
     *
     * @return The dataset list
     *
     * @throws ExternalResourceException if unable to retrieve data from the source
     */
    abstract List<ExternalResourceBean> datasets() throws ExternalResourceException

    /**
     * Create an external resource bean with pre-populated information for this external resource.
     *
     * @param external The external resource data, a map that looks like a data resource
     *
     * @return The initialised external resource bean
     */
    abstract ExternalResourceBean createExternalResource(Map external)

        /**
     * Get a dataset from the external data source
     *
     * @param id The identifier that can be used to find the source
     *
     * @return Either a matching dataset or null for not found
     *
     * @throws ExternalResourceException if unable to retrieve data from the source
     */
    abstract Map getDataset(String id) throws ExternalResourceException

    /**
     * Include default values into the resource.
     * <p>
     * If a property is null and there is a default value, then that property is set to the default
     * Groovy truthiness is not supported; null or it's not set.
     *
     * @param resource The resource
     *
     */
    def addDefaultDatasetValues(Map resource) {
        configuration.defaultDatasetValues.each { field, value ->
            if (resource.containsKey(field) == null)
                resource.put(field, value)

        }
    }

    /**
     * Get the name of the source that this adaptor is for
     *
     * @return The source name
     */
    abstract String getSource()

    /**
     * Get a map of code -> country name for lookup purposes
     *
     * @return A map from external service code to name
     */
    abstract Map getCountryMap()

    /**
     * Get a map of code -> dataset type for lookup purposes
     *
     * @return A map from code to name
     */
    abstract Map getDatasetTypeMap()

    /**
     * Does this data source have data that needs to be generated
     * before the data resource is connected.
     * <p>
     * Some data sources may have pre-generated data to collect or link to
     *
     * @return True if the data must be downloaded and processed
      */
    boolean isGeneratable() {
        return true
    }

    /**
     * Does this data source have data that neeeds to be downloaded and processed
     * before the data resource is connected.
     * <p>
     * Some data sources may simply have a URI as a connection and not need any gathering
     * or processing.
     *
     * @return True if the data must be downloaded and processed
     */
    boolean isDownloadable() {
        return true
    }

    /**
     * Check to see if there is data available for the supplied resource ID.
     *
     * @param guid The resource GUID
     *
     * @return True if there is data available
     *
     * @throws ExternalResourceException if unable test for data
     */
    abstract boolean isDataAvailableForResource(String guid) throws ExternalResourceException

    /**
     * Generate data for download.
     *
     * @param guid The identifier for the resource
     *
     * @return An identifier that can be used to monitor when the generation phase has completed (null for an error)
     *
     * @throws ExternalResourceException if unable to generate data
     */
    abstract String generateData(String guid, String country) throws ExternalResourceException

    /**
     * See if a task has completed.
     * <p>
     * Returns {@link TaskPhase#COMPLETED} for a completed generation task, {@link TaskPhase#GENERATING} for
     * something still generating and error/cancelled termnal status for errors etc.
     *
     * @param id The identifier for generation task
     *
     * @return A load phase corresponding to what is going on.
     *
     * @throws ExternalResourceException if unable to check the generation status
     */
    abstract TaskPhase generateStatus(String id)  throws ExternalResourceException

    /**
     * Download generated data
     *
     * @param id The identifier for the generation task
     * @param target The local file to download to
     *
     * @throws ExternalResourceException if unable to download data
     */
    abstract void downloadData(String id, File target) throws ExternalResourceException

    /**
     * Process downloaded data into a suitable form
     *
     * @param downloaded The downloaded data file
     * @param workDir The directory to generate the file in
     * @param resource The resource being processed
     *
     * @return The processed file, or null for an error
     *
     * @throws ExternalResourceException if unable to process the data
     */
    abstract File processData(File downloaded, File workDir, ExternalResourceBean resource) throws ExternalResourceException

    /**
     * Generate an update to the data resource providing connection and other update information.
     * <p>
     * As well as the connection parameters, this method can also update things like data currency, etc.
     *
     * @param upload The uploaded file
     * @param connection The existing connection parameters
     * @param resource The resource description
     *
     * @return An update to the data resource
     *
     * @throws ExternalResourceException if unable to build the connection
     */
    abstract Object buildConnection(File upload, Object connection, ExternalResourceBean resource) throws ExternalResourceException
}
