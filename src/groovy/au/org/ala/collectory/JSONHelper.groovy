package au.org.ala.collectory

import grails.converters.deep.JSON

/**
 *
 * Created by markew
 * Date: Nov 29, 2010
 * Time: 12:11:06 PM
 */
class JSONHelper {

    static def taxonomyHints(String json) {
        if (!json) { return [] }
        def obj = JSON.parse(json)
        def coverage = obj.coverage
        if (coverage) {
            return coverage
        } else {
            return null
        }
    }

    static String taxonomyBreakdownStart(String json) {
        if (!json) { return null }
        def obj = JSON.parse(json)
        def start = obj.breakdownStart
        return start.rank + (start.name ? ":${start.name}" : "")
    }

}
