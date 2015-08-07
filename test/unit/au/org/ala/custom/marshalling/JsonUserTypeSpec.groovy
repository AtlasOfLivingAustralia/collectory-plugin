package au.org.ala.custom.marshalling

import spock.lang.Specification

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class JsonUserTypeSpec extends Specification {
    JsonUserType type

    def setup() {
        type = new JsonUserType()
    }

    def cleanup() {
    }

    void testSqlTypes() {
        when:
        def types = type.sqlTypes()
        then:
        types.length == 1
        // This should be right but hibernate says -1
        //types[0] == Types.LONGNVARCHAR
    }

    void testReturnedClass() {
        expect:
        type.returnedClass() == Object.class
    }

    void testEquals1() {
        when:
        def o1 = [key: 'value']
        then:
        type.equals(o1, o1) == true
    }

    void testEquals2() {
        when:
        def o1 = [key: 'value']
        def o2 = [key: 'value']
        then:
        type.equals(o1, o2) == true
    }

    void testEquals3() {
        when:
        def o1 = [key: 'value']
        def o2 = [key: 'another value']
        then:
        type.equals(o1, o2) == false
    }

    void testEquals4() {
        when:
        def o1 = [key: 'value']
        then:
        type.equals(o1, null) == false
    }

    void testEquals5() {
        when:
        def o1 = [key: 'value']
        then:
        type.equals(null, o1) == false
    }

    void testHashCode1() {
        expect:
        type.hashCode(null) == 0
    }

    void testHashCode2() {
        when:
        def o1 = "Hello there"
        then:
        type.hashCode(o1) != 0
        type.hashCode(o1) == type.hashCode(o1)
    }

    void testHashCode3() {
        when:
        def o1 = "Hello there"
        def o2 = "Hello there"
        then:
        type.hashCode(o1) == type.hashCode(o2)
    }

    void testHashCode4() {
        when:
        def o1 = "Hello there"
        def o2 = "Not Hello there"
        then:
        type.hashCode(o1) != type.hashCode(o2)
    }

    void testDeepCopy1() {
        expect:
        type.deepCopy(null) == null
    }

    void testDeepCopy2() {
        when:
        def o1 = [ "Hello there" ]
        def o2 = type.deepCopy(o1)
        then:
        o1 == o2
    }

    void testDeepCopy3() {
        when:
        def o1 = [key: "Hello there"]
        def o2 = type.deepCopy(o1)
        then:
        o1 == o2
        o1.key == o2.key
    }

    void testNullSafeGet1() {
        given:
        ResultSet rs = Stub()
        rs.getString(_) >> '{ "key": "value" }'
        String[] cols = (String[]) [ "col" ].toArray()
        when:
        def val = type.nullSafeGet(rs, cols, null)
        then:
        val.key == "value"
    }

    void testNullSafeGet2() {
        given:
        ResultSet rs = Stub()
        rs.getString(_) >> null
        String[] cols = (String[]) [ "col" ].toArray()
        when:
        def val = type.nullSafeGet(rs, cols, null)
        then:
        val == null
    }

    void testNullSafeGet3() {
        given:
        ResultSet rs = Stub()
        rs.getString(_) >> '[ 1, 2, 3 ]'
        String[] cols = (String[]) [ "col" ].toArray()
        when:
        def val = type.nullSafeGet(rs, cols, null)
        then:
        val == [1, 2, 3]
    }

    void testNullSafeSet1() {
        given:
        PreparedStatement ps = Mock()
        when:
        type.nullSafeSet(ps, [key: 'value'], 1)
        then:
        1 * ps.setString(1, '{"key":"value"}')
    }

    void testNullSafeSet2() {
        given:
        PreparedStatement ps = Mock()
        when:
        type.nullSafeSet(ps, null, 1)
        then:
        1 * ps.setNull(1, -1)
    }

    void testNullSafeSet3() {
        given:
        PreparedStatement ps = Mock()
        when:
        type.nullSafeSet(ps, [1, 2, 3], 1)
        then:
        1 * ps.setString(1, '[1,2,3]')
    }

}