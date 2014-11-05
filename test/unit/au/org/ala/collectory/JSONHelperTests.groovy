package au.org.ala.collectory

import grails.test.GrailsUnitTestCase

/**
 * Created by markew
 * Date: Nov 29, 2010
 * Time: 12:33:38 PM
 */
class JSONHelperTests  extends GrailsUnitTestCase {

    String json = "{coverage: [{'kingdom':'plantae'}, {'phylum':'zygomycota'}], breakdownStart: {rank:'phylum'}}"

    protected void setUp() {
        super.setUp()
        //loadCodec grails.converters.JSON
    }

    void testTaxonomyHints() {

        def taxHints = JSONHelper.taxonomyHints(json)
        assertEquals 2, taxHints.size()
        taxHints.each {
            assertTrue it.kingdom == 'plantae' || it.phylum == 'zygomycota'
        }
    }

    void testBreakdownStart() {

        def breakdownHint = JSONHelper.taxonomyBreakdownStart(json)
        assertNotNull breakdownHint
        assertEquals "phylum", breakdownHint

        assertEquals "phylum:zygomycota", JSONHelper.taxonomyBreakdownStart("{breakdownStart: {rank:'phylum',name:'zygomycota'}}")
    }
}
