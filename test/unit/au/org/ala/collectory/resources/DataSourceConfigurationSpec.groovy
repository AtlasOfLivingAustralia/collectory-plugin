package au.org.ala.collectory.resources

import au.org.ala.collectory.DataSourceConfiguration
import au.org.ala.collectory.resources.gbif.GbifDataSourceAdapter
import au.org.ala.util.TestUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import jline.internal.InputStreamReader
import spock.lang.Specification
/**
 * Test cases for {@link DataSourceConfiguration}.
 * <p>
 * More description.
 * </p>
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2017 CSIRO
 */
class DataSourceConfigurationSpec extends Specification implements TestUtil {
    ObjectMapper mapper

    def setup() {
        mapper = new ObjectMapper()
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
    }

    def cleanup() {
    }

    def readConfig(String config) {
        def reader = new InputStreamReader(this.class.getResourceAsStream(config))
        return mapper.readValue(reader, DataSourceConfiguration.class)
     }

    def "test from json 1"() {
        when:
        def config = readConfig("config1.json")
        then:
        config != null
        config instanceof DataSourceConfiguration
        config.adaptorClass == GbifDataSourceAdapter.class
        config.endpoint?.toExternalForm() == "http://api.gbif.org/v1/"
    }

    def "test from json 2"() {
        when:
        def config = readConfig("config2.json")
        then:
        config != null
        config instanceof DataSourceConfiguration
        config.guid == "1"
        config.name == "test-1"
        config.description == "description-1"
        config.adaptorClass == GbifDataSourceAdapter.class
        config.endpoint?.toExternalForm() == "http://api.gbif.org/v1/"
        config.country == "AU"
        config.recordType == "OCCURRENCE"
        config.defaultDatasetValues != null
        config.defaultDatasetValues.size() == 2
        config.defaultDatasetValues["focus"] == "nowhere"
        config.defaultDatasetValues["licenceType"] == "CC BY"
    }

    def "test to json 1"() {
        when:
        def config = new DataSourceConfiguration(adaptorClass: GbifDataSourceAdapter.class, endpoint: new URL("http://api.gbif.org/v1/"))
        def json = mapper.writeValueAsString(config)
        then:
        json == resourceAsString("config1.json")
    }

    def "test to json 2"() {
        when:
        def config = readConfig("config1.json")
        def json = mapper.writeValueAsString(config)
        then:
        json == resourceAsString("config1.json")
    }


    def "test create 1"() {
        when:
        def config = readConfig("config2.json")
        def adapter = config.create()
        then:
        adapter!= null
        adapter instanceof GbifDataSourceAdapter
    }

}
