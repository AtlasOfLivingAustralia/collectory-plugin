package au.org.ala.collectory

import grails.converters.JSON
import grails.test.mixin.integration.Integration
import org.codehaus.groovy.grails.web.json.JSONArray
import spock.lang.Specification

/**
 * Created by markew
 * Date: Jun 2, 2010
 * Time: 5:10:33 PM
 */
@Integration
class GeneralGrailsTests extends Specification {

    def testJSONLists1() {
        when:
        String providerCodes = ' insects, spiders,beetles'
        String[] codes = providerCodes?.split(',')
        List cs = codes.collect {it.trim()}
        providerCodes = (cs as JSON).toString()
        then:
        providerCodes == '["insects","spiders","beetles"]'
    }

    def testJSONLists2() {
        when:
        List<String> list = ['code1', 'code2', 'code3']
        def json = list as JSON
        def back = JSON.parse(json.toString())
        println back.class
        then:
        back instanceof JSONArray
        ((JSONArray) back).contains('code1')
        ((JSONArray) back).contains('code2')
        ((JSONArray) back).contains('code3')
    }

}
