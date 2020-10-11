package io.quarkus.hibernate.types.jsonb;

import javax.json.JsonObject;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;

import io.quarkus.hibernate.types.json.JsonTypes;
import io.quarkus.hibernate.types.json.impl.JsonBinarySqlTypeDescriptor;
import io.quarkus.hibernate.types.json.impl.JsonObjectTypeDescriptor;

/**
 * Maps a JSON {@link JsonObject} object on a JSON column type that is managed via
 * {@link java.sql.PreparedStatement#setObject(int, Object)} at JDBC Driver level. For instance, if you are using PostgreSQL,
 * you should be using {@link JsonObjectBinaryType} to map both {@code jsonb} and {@code json} column types to a Jackson
 * {@link JsonObject} object.
 *
 * <p>
 * For more details about how to use it, check out
 * <a href="https://vladmihalcea.com/how-to-store-schema-less-eav-entity-attribute-value-data-using-json-and-hibernate/">this
 * article</a> on <a href="https://vladmihalcea.com/">vladmihalcea.com</a>.
 *
 * @author Vlad Mihalcea
 *
 */
public class JsonObjectBinaryType extends AbstractSingleColumnStandardBasicType<JsonObject> {

    public JsonObjectBinaryType() {
        super(JsonBinarySqlTypeDescriptor.INSTANCE,
                new JsonObjectTypeDescriptor<JsonObject>(JsonObject.class));
    }

    public String getName() {
        return JsonTypes.JSON_OBJECT_BIN;
    }
}
