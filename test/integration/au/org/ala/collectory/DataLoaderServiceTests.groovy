package au.org.ala.collectory

import grails.test.*
import au.com.bytecode.opencsv.CSVReader

class DataLoaderServiceTests extends GrailsUnitTestCase {

    def dataLoaderService

    def column = ["lsid","record_id","created","modified","name","code","kind","taxon_scope","geo_scope","size","size_approx_int","founded_year","notes","contact_person","contact_position","contact_phone","contact_fax","contact_email","web_site","web_service_uri","web_service_type","location_department","location_street","location_post_box","location_city","location_state","location_postcode","location_country_name","location_country_iso","location_long","location_lat","location_alt","location_notes","institution_name","institution_type","institution_uri","description_tech","description_pub","url"]

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testJSONLoad() {
        def result = dataLoaderService.loadContact("Lemmy", "Caution", true)
        assertEquals 'Lemmy', result.firstName

        def contact = dataLoaderService.loadFromFile("/Users/markew/load.json")
        assertEquals 'Sandy', contact.firstName
    }

    void testLoadSupplimentaryData() {
        //dataLoaderService.loadSupplementaryData("/data/collectory/bootstrap/sup.json", false)
    }
    
    /* order of fields in BCI csv
"lsid","record_id","created","modified","name","code","kind","taxon_scope","geo_scope","size","size_approx_int","founded_year","notes","contact_person","contact_position","contact_phone","contact_fax","contact_email","web_site","web_service_uri","web_service_type","location_department","location_street","location_post_box","location_city","location_state","location_postcode","location_country_name","location_country_iso","location_long","location_lat","location_alt","location_notes","institution_name","institution_type","institution_uri","description_tech","description_pub","url"
     */

    void testOpencsv() {
        CSVReader reader = new CSVReader(new FileReader("/data/collectory/bootstrap/lookup_lsid.csv"))
        String [] nextLine;
        int i = 0
		while ((nextLine = reader.readNext()) != null /*&& i++ < 20*/) {
            if (nextLine[28] == 'AU' || nextLine[27] == 'Australia') {
//                for (int j=0; j < column.size(); j++) {
//                    println(column[j] + ": " + nextLine[j])
//                }
                //println ""
                i++
            }
		}
        println "Total = " + i
        
    }

    void testLoadingAsParams() {
        CSVReader reader = new CSVReader(new FileReader("/data/collectory/bootstrap/lookup_lsid.csv"))
        String [] nextLine;
		while ((nextLine = reader.readNext()) != null) {
            if (nextLine[28] == 'AU' || nextLine[27] == 'Australia') {
                def params = [:]
                column.eachWithIndex {it, i ->
                    if (nextLine[i]) {
                        params[it] = nextLine[i]
                    }
                }
                assertEquals nextLine[4], params.name
                // try loading a domain object
                def pg = new Collection()
                pg.properties = params
                assertEquals nextLine[4], pg.name
            }
		}
    }

}
