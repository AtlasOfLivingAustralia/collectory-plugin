package au.org.ala.collectory

import au.org.ala.util.TestUtil
import grails.test.mixin.integration.Integration
import spock.lang.Specification

@Integration
class DataLoaderServiceTests extends Specification implements TestUtil {

    def dataLoaderService

    def column = ["lsid","record_id","created","modified","name","code","kind","taxon_scope","geo_scope","size","size_approx_int","founded_year","notes","contact_person","contact_position","contact_phone","contact_fax","contact_email","web_site","web_service_uri","web_service_type","location_department","location_street","location_post_box","location_city","location_state","location_postcode","location_country_name","location_country_iso","location_long","location_lat","location_alt","location_notes","institution_name","institution_type","institution_uri","description_tech","description_pub","url"]

    def testJSONLoad1() {
        when:
        def result = dataLoaderService.loadContact("Lemmy", "Caution", true)
        then:
        result.firstName == 'Lemmy'
    }

    def testJSONLoad2() {
        setup:
        def tmp = copyToTempFile("load.json")
        when:
        def contact = dataLoaderService.loadFromFile(tmp.absolutePath)
        then:
        contact.firstName == 'Sandy'
    }
}
