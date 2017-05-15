package au.org.ala.collectory.resources.gbif

import au.org.ala.collectory.resources.DataSourceAdapter
import au.org.ala.collectory.resources.DataSourceConfiguration
import spock.lang.Specification

/**
 * Test cases for {@link GbifDataSourceAdapterSpec}.
 * <p>
 * More description.
 * </p>
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2017 CSIRO
 */
class GbifDataSourceAdapterSpec extends Specification {
    DataSourceConfiguration config
    DataSourceAdapter adaptor

    def setup() {
        config = new DataSourceConfiguration(
                adaptorClass: GbifDataSourceAdapter.class,
                endpoint: new URL("http://api.gbif.org/v1/"),
                country: "AU",
                recordType: "OCCURRENCE"
        )
        adaptor = config.create()
    }

    def cleanup() {
    }

    def "test datasets 1"() {
        when:
        def results = adaptor.datasets()
        then:
        results != null
    }

}
