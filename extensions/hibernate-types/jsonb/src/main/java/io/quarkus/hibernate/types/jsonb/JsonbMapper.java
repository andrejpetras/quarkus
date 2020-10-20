package io.quarkus.hibernate.types.jsonb;

import java.io.StringReader;
import java.lang.reflect.Type;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbException;

import io.quarkus.hibernate.types.json.JsonMapper;

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
        return JsonValue.class;
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

    public JsonStructure readObject(String value) {
        try {
            JsonReader reader = Json.createReader(new StringReader(value));
            return reader.read();
        } catch (JsonbException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public JsonStructure toJsonType(String value) {
        return new JsonStructureImpl(readObject(value));
    }

    public <T> T clone(T value) {
        try {
            return jsonb.fromJson(jsonb.toJson(value), (Class<T>) value.getClass());
        } catch (JsonbException e) {
            throw new IllegalArgumentException("The given Json object value: " + value + " cannot be clone.", e);
        }
    }

    public static class JsonStructureImpl implements JsonStructure {

        private JsonStructure data;

        public JsonStructureImpl(JsonStructure data) {
            this.data = data;
        }

        @Override
        public ValueType getValueType() {
            return data.getValueType();
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }
}
