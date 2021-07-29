package au.org.ala.collectory.resources.gbif

import au.org.ala.collectory.DataResource
import au.org.ala.collectory.DataSourceConfiguration
import au.org.ala.collectory.ExternalResourceBean
import au.org.ala.collectory.GbifService
import au.org.ala.collectory.exception.ExternalResourceException
import net.sf.json.JSONObject
import org.slf4j.LoggerFactory

import java.text.MessageFormat

/**
 * Data source adapters for the GBIF API for downloading Repatriation data subsets.
 */
class GbifRepatDataSourceAdapter extends GbifDataSourceAdapter {

    static final LOGGER = LoggerFactory.getLogger(GbifRepatDataSourceAdapter.class)
    static final SOURCE = "GBIF_REPATRIATION"
    GbifService gbifService

    static final String OCCURRENCE_REPAT_SEARCH = "occurrence/search?repatriated=true&country={0}&type={1}&offset=0&limit=0&facet=datasetKey&facetLimit=10000"

    GbifRepatDataSourceAdapter(DataSourceConfiguration configuration) {
        super(configuration)
     }

    @Override
    List<Map> datasets() throws ExternalResourceException {
        def keys = []
        def datasets = []

        LOGGER.info("Requesting dataset lists configuration.country: ${configuration.country}")
        String url = MessageFormat.format(OCCURRENCE_REPAT_SEARCH, configuration.country, configuration.recordType)
        JSONObject json = getJSONWS(url)
        if (json?.facets) {
            json.facets[0].counts.each {
                keys << it.name

                if (it.count >= configuration.minRecordCount && it.count <= configuration.maxRecordCount && datasets.size() < configuration.maxNoOfDatasets) {
                    LOGGER.info("Getting metadata for ${it.name}  = ${it.count}")
                    def dataset = getDataset(it.name, it.count)
                    if (dataset.name) {
                        datasets << dataset
                    }
                } else {
                    LOGGER.info("Skipping dataset ${it.name}  = ${it.count}")
                }
            }
        }

        LOGGER.info("Total datasets retrieved: " + datasets.size())
        datasets
    }

    @Override
    ExternalResourceBean createExternalResource(Map external) {
        ExternalResourceBean ext = super.createExternalResource(external)
        ext.setRecordCount(external.recordCount)
        ext
     }

    Map getDataset(String id, Integer recordCount) throws ExternalResourceException {
        Map dataset = getDataset(id)
        dataset.put("recordCount", recordCount)
        dataset.put("repatriationCountry", configuration.country)
        dataset
    }
}
