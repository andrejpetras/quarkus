package io.quarkus.hibernate.types.jsonb;

import java.io.StringReader;
import java.lang.reflect.Type;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbException;

import io.quarkus.hibernate.types.json.impl.JsonMapper;

/**
 * Wraps a JSON mapper {@link Jsonb} so that you can supply your own {@link Jsonb} reference.
 */
public class JsonbMapper implements JsonMapper {

    private final Jsonb jsonb;

    public JsonbMapper(Jsonb jsonb) {
        this.jsonb = jsonb;
    }

    @Override
    public Class<?> getBinaryTypeClass() {
        return JsonStructure.class;
    }

    public <T> T fromJson(String string, Class<T> clazz) {
        try {
            return jsonb.fromJson(string, clazz);
        } catch (JsonbException e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object",
                    e);
        }
    }

    public <T> T fromJson(String string, Type type) {
        try {
            return jsonb.fromJson(string, type);
        } catch (JsonbException e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object",
                    e);
        }
    }

    public String toJson(Object value) {
        try {
            return jsonb.toJson(value);
        } catch (JsonbException e) {
            throw new IllegalArgumentException("The given Json object value: " + value + " cannot be transformed to a String",
                    e);
        }
    }

    public JsonObject readObject(String value) {
        try {
            JsonReader reader = Json.createReader(new StringReader(value));
            return reader.readObject();
        } catch (JsonbException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public <T> T clone(T value) {
        try {
            return jsonb.fromJson(jsonb.toJson(value), (Class<T>) value.getClass());
        } catch (JsonbException e) {
            throw new IllegalArgumentException("The given Json object value: " + value + " cannot be clone.", e);
        }
    }
}
