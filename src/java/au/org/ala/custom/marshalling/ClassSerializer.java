package au.org.ala.custom.marshalling;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Gson Serializer/Deserializer for Classes.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 * @copyright Copyright (c) 2017 CSIRO
 */
public class ClassSerializer implements JsonSerializer<Class>, JsonDeserializer<Class> {
    @Override
    public JsonElement serialize(Class aClass, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(aClass.getName());
    }

    @Override
    public Class deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            return Class.forName(jsonElement.getAsString());
        } catch (ClassNotFoundException ex) {
            throw new JsonParseException("Unable to decode class name from " + jsonElement, ex);
        }
    }
}
