package au.org.ala.collectory

import grails.test.*
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.plugins.codecs.HTMLCodec
import spock.lang.Specification

@TestFor(CollectoryTagLib)
class CollectoryTagLibTests extends Specification {

    protected void setup() {
        //loadCodec org.codehaus.groovy.grails.plugins.codecs.HTMLCodec
    }

    void testRoleIfPresent() {
        expect:
        applyTemplate('<cl:roleIfPresent role="Manager"/>') == ' - Manager'
        applyTemplate('<cl:roleIfPresent role=""/>') == ''
    }

    void testAdminIfPresent() {
        expect:
        applyTemplate('<cl:adminIfPresent admin="${true}"/>') == '(Authorised to edit this collection)'
        applyTemplate('<cl:adminIfPresent admin="${false}"/>') == ''
    }

    void testNumberIfKnown() {
        expect:
        applyTemplate('<cl:numberIfKnown number="${20}">&deg;</cl:numberIfKnown>') == '20&deg;'
        applyTemplate('<cl:numberIfKnown number="${-1}">&deg;</cl:numberIfKnown>') == ''
    }

    /*void testFormattedText_1() {
        tagLib.formattedText(body:"line1line2") { result ->
            out << result
        }
    }*/
}
