package io.quarkus.hibernate.types.json.impl;

import java.io.Serializable;

import javax.json.JsonObject;
import javax.json.JsonStructure;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.MutableMutabilityPlan;

/**
 * @author Vlad Mihalcea
 */
public class JsonObjectTypeDescriptor extends AbstractTypeDescriptor<JsonObject> {

    private JsonWrapper jsonWrapper;

    public JsonObjectTypeDescriptor(final JsonWrapper jsonWrapper) {
        super(JsonObject.class, new MutableMutabilityPlan<JsonObject>() {
            @Override
            public Serializable disassemble(JsonObject value) {
                return jsonWrapper.toJson(value);
            }

            @Override
            public JsonObject assemble(Serializable cached) {
                return jsonWrapper.readObject((String) cached);
            }

            @Override
            protected JsonObject deepCopyNotNull(JsonObject value) {
                return jsonWrapper.clone(value);
            }
        });
        this.jsonWrapper = jsonWrapper;
    }

    @Override
    public boolean areEqual(JsonObject one, JsonObject another) {
        if (one == another) {
            return true;
        }
        if (one == null || another == null) {
            return false;
        }
        return jsonWrapper.readObject(jsonWrapper.toJson(one)).equals(
                jsonWrapper.readObject(jsonWrapper.toJson(another)));
    }

    @Override
    public String toString(JsonObject value) {
        return jsonWrapper.toJson(value);
    }

    @Override
    public JsonObject fromString(String string) {
        return jsonWrapper.readObject(string);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public <X> X unwrap(JsonObject value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X) toString(value);
        }
        if (JsonStructure.class.isAssignableFrom(type)) {
            return (X) jsonWrapper.readObject(toString(value));
        }
        throw unknownUnwrap(type);
    }

    @Override
    public <X> JsonObject wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        return fromString(value.toString());
    }

}
