package io.quarkus.hibernate.types.jsonb;

import javax.json.JsonObject;
import javax.json.JsonStructure;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;

import io.quarkus.hibernate.types.json.JsonTypes;
import io.quarkus.hibernate.types.json.impl.JsonBinarySqlTypeDescriptor;
import io.quarkus.hibernate.types.json.impl.JsonObjectTypeDescriptor;

/**
 * Maps a JSON {@link JsonStructure} object on a JSON column type that is managed via
 * {@link java.sql.PreparedStatement#setObject(int, Object)} at JDBC Driver level. For instance, if you are using PostgreSQL,
 * you should be using {@link JsonStructureBinaryType} to map both {@code jsonb} and {@code json} column types to a Jackson
 * {@link JsonObject} object.
 */
public class JsonStructureBinaryType extends AbstractSingleColumnStandardBasicType<JsonStructure> {

    public JsonStructureBinaryType() {
        super(JsonBinarySqlTypeDescriptor.INSTANCE,
                new JsonObjectTypeDescriptor<JsonStructure>(JsonStructure.class));
    }

    public String getName() {
        return JsonTypes.JSON_OBJECT_BIN;
    }
}
