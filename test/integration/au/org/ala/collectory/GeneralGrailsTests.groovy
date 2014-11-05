package au.org.ala.collectory

import grails.test.GrailsUnitTestCase
import grails.converters.JSON

/**
 * Created by markew
 * Date: Jun 2, 2010
 * Time: 5:10:33 PM
 */
class GeneralGrailsTests extends GrailsUnitTestCase {

    void testJSONLists() {
        List<String> list = ['code1', 'code2', 'code3']
        def json = list as JSON
        println json.toString()

        def back = JSON.parse(json.toString())
        back.each {println it}

        String providerCodes = ' insects, spiders,beetles'
        String[] codes = providerCodes?.split(',')
        List cs = codes.collect {it.trim()}
        cs.each {println it}
        //def js = cs as JSON
        providerCodes = (cs as JSON).toString()
        println providerCodes
        assertEquals '["insects","spiders","beetles"]', providerCodes
    }

}
