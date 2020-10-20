package io.quarkus.hibernate.types.json.impl;

import java.lang.reflect.Type;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;
import io.quarkus.hibernate.types.json.JsonMapper;

/**
 * Wraps a JSON mapper {@link JsonMapper} so that you can supply your own {@link JsonMapper} reference.
 */
public class JsonMapperInstance {

    static JsonMapper jsonMapper = get();

    /*
     * Gets the {@code JsonMapper} from the ARC container.
     */
    public static JsonMapper get() {
        JsonMapper tmp = null;
        ArcContainer container = Arc.container();
        if (container != null) {
            tmp = container.instance(JsonMapper.class).get();
        }
        if (tmp == null) {
            throw new IllegalStateException("Missing JsonMapper instance implementation [jsonb,jackson,...]");
        }
        return tmp;
    }

    public static Class<?> getBinaryTypeClass() {
        return jsonMapper.getBinaryTypeClass();
    }

    public static <T> T fromJson(String string, Type type) {
        try {
            return jsonMapper.fromJson(string, type);
        } catch (Exception e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object",
                    e);
        }
    }

    public static String toJson(Object value) {
        try {
            return jsonMapper.toJson(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("The given Json object value: " + value + " cannot be transformed to a String",
                    e);
        }
    }

    public static Object readObject(String value) {
        try {
            return jsonMapper.readObject(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T clone(T value) {
        try {
            return jsonMapper.clone(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("The given Json object value: " + value + " cannot be clone.", e);
        }
    }

    public static boolean areJsonEqual(Object one, Object another) {
        return readObject(toJson(one)).equals(readObject(toJson(another)));
    }

    public static Object toJsonType(Object value) {
        try {
            return jsonMapper.toJsonType(jsonMapper.toJson(value));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
