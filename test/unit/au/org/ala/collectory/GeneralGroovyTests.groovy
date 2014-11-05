package au.org.ala.collectory

import grails.test.GrailsUnitTestCase
import grails.converters.JSON

/**
 * Created by markew
 * Date: May 20, 2010
 * Time: 11:31:48 AM
 */
class GeneralGroovyTests extends GrailsUnitTestCase {

    void testListFilter() {
        def list = []
        list << new Collection(guid: 1, name: "1")
        list << new Collection(guid: 2, name: "2")
        list << new Institution(guid: 3, name: "3")
        list << new Institution(guid: 4, name: "4")
        list << new Collection(guid: 5, name: "5")

        assertEquals 2, list.findAll{it instanceof Institution}.size()
    }

    void testLoadingAsParams() {
        String [] keys = ['one', 'two', 'three'];
        def values = ['v1', 'v2', 'v3']
        def params = [:]
        keys.eachWithIndex {it, i ->
            params[it] = values[i]
        }
        assertEquals 3, params.size()
        params.each {key, value ->
            println "${key} == ${value}"
        }
        assertEquals 'v2', params.two
    }

    void testSplit() {
        String test = 'Entomology (Insects/Spiders)'

        def words = test.tokenize("[ ()/]")
        words.each {println it}
        assertEquals 3, words.size()
        assertEquals 'Entomology', words[0]
        assertEquals 'Insects', words[1]
        assertEquals 'Spiders', words[2]
    }

    void testExtractKeywords() {
        def str = 'Ducks, Quail and Sparrows'
        List words = str.tokenize("[, ()/]")
        words = words.collect{it.toLowerCase()}
        def keywords = words.findAll {!(it in ['and','not','specified'])}
        assertEquals '["ducks","quail","sparrows"]', (keywords as JSON).toString()
    }

}
