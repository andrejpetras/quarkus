package io.quarkus.hibernate.types.json.impl;

import java.io.StringReader;
import java.lang.reflect.Type;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;

/**
 * Wraps a JSON mapper {@link Jsonb} so that you can supply your own {@link Jsonb} reference.
 */
public class JsonWrapper {

    public static final JsonWrapper INSTANCE = new JsonWrapper();

    private final Jsonb jsonMapper;

    /**
     * Gets the {@code Jsonb} from the ARC container.
     * 
     * @return Jsonb instance.
     */
    public static Jsonb get() {
        Jsonb jsonMapper = null;
        ArcContainer container = Arc.container();
        if (container != null) {
            jsonMapper = container.instance(Jsonb.class).get();
        }
        return jsonMapper != null ? jsonMapper : JsonbBuilder.create();
    }

    public JsonWrapper() {
        this.jsonMapper = get();
    }

    public <T> T fromJson(String string, Class<T> clazz) {
        try {
            return jsonMapper.fromJson(string, clazz);
        } catch (JsonbException e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object",
                    e);
        }
    }

    public <T> T fromJson(String string, Type type) {
        try {
            return jsonMapper.fromJson(string, type);
        } catch (JsonbException e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object",
                    e);
        }
    }

    public String toJson(Object value) {
        try {
            return jsonMapper.toJson(value);
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
            return jsonMapper.fromJson(jsonMapper.toJson(value), (Class<T>) value.getClass());
        } catch (JsonbException e) {
            throw new IllegalArgumentException("The given Json object value: " + value + " cannot be clone.", e);
        }
    }
}
