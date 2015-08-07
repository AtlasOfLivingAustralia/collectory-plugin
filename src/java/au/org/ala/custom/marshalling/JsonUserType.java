package au.org.ala.custom.marshalling;

import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import org.hibernate.HibernateException;
import org.hibernate.type.TextType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Map a simple object back/forward to json for storage in a database column.
 * <p>
 * The object needs to be:
 * <ul>
 *     <li>{@link JsonOutput} serializable</li>
 *     <li>{@link Serializable}</li>
 *     <li>Immutable, meaning that if the object is modified, a new copy is made and then modified</li>
 *     <li>Simple, meaning that it's a structural collection of arrays, maps and simple values</li>
 * </ul>
 */
public class JsonUserType implements UserType {
    public JsonUserType() {
        System.err.println("User type created");
    }

    // Handle type mapping for java
    protected String toJson(Object value) {
        if (value == null)
            return null;
        if (value instanceof Boolean)
            return JsonOutput.toJson((Boolean) value);
        if (value instanceof Calendar)
            return JsonOutput.toJson((Calendar) value);
        if (value instanceof Character)
            return JsonOutput.toJson((Character) value);
        if (value instanceof Date)
            return JsonOutput.toJson((Date) value);
        if (value instanceof Map)
            return JsonOutput.toJson((Map) value);
        if (value instanceof Number)
            return JsonOutput.toJson((Number) value);
        if (value instanceof String)
            return JsonOutput.toJson((String) value);
        if (value instanceof URL)
            return JsonOutput.toJson((URL) value);
        if (value instanceof UUID)
            return JsonOutput.toJson((UUID) value);
        return JsonOutput.toJson(value);
    }

    @Override
    public int[] sqlTypes() {
        return new int[] { TextType.INSTANCE.sqlType() };
    }

    @Override
    public Class returnedClass() {
        return Object.class;
    }

    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        if (o == o1)
            return true;
        if (o == null || o1 == null)
            return false;
        return o.equals(o1);
    }

    @Override
    public int hashCode(Object value) throws HibernateException {
        return value == null ? 0 : value.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        String json = rs.getString(names[0]);
        JsonSlurper slurper = new JsonSlurper();
        return json == null || json.isEmpty() ? null : slurper.parseText(json);
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException, SQLException {
        if (value == null)
            preparedStatement.setNull(index, Types.LONGNVARCHAR);
        else {
            String json = this.toJson(value);
            preparedStatement.setString(index, json);
        }
     }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) this.deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return this.deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return this.deepCopy(original);
    }
}
