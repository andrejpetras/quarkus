package io.quarkus.hibernate.types.jsonb;

import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;

import io.quarkus.hibernate.types.json.JsonTypes;
import io.quarkus.hibernate.types.json.impl.JsonObjectTypeDescriptor;
import io.quarkus.hibernate.types.json.impl.JsonStringSqlTypeDescriptor;

/**
 * Maps a object {@link JsonStructure} object on a JSON column type that is managed via
 * {@link java.sql.PreparedStatement#setString(int, String)} at JDBC Driver level. For instance, if you are using MySQL, you
 * should be using {@link JsonObjectStringType} to map the {@code json} column type to a Jackson {@link JsonObject} object.
 * <p>
 * For more details about how to use it, check out
 * <a href="https://vladmihalcea.com/how-to-store-schema-less-eav-entity-attribute-value-data-using-json-and-hibernate/">this
 * article</a> on <a href="https://vladmihalcea.com/">vladmihalcea.com</a>.
 *
 * @author Vlad Mihalcea
 *
 */
public class JsonObjectStringType extends AbstractSingleColumnStandardBasicType<JsonStructure> {

    public JsonObjectStringType() {
        super(JsonStringSqlTypeDescriptor.INSTANCE,
                new JsonObjectTypeDescriptor<JsonStructure>(JsonStructure.class));
    }

    @Override
    public String getName() {
        return JsonTypes.JSON_OBJECT_STRING;
    }
}
