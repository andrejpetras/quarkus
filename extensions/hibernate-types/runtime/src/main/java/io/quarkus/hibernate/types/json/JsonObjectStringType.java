package io.quarkus.hibernate.types.json;

import javax.json.JsonObject;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;

import io.quarkus.hibernate.types.json.impl.JsonObjectTypeDescriptor;
import io.quarkus.hibernate.types.json.impl.JsonStringSqlTypeDescriptor;
import io.quarkus.hibernate.types.json.impl.JsonWrapper;

/**
 * Maps a object {@link JsonObject} object on a JSON column type that is managed via
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
public class JsonObjectStringType extends AbstractSingleColumnStandardBasicType<JsonObject> {

    public JsonObjectStringType() {
        super(JsonStringSqlTypeDescriptor.INSTANCE, new JsonObjectTypeDescriptor(JsonWrapper.INSTANCE));
    }

    @Override
    public String getName() {
        return JsonTypes.JSON_OBJECT_STRING;
    }
}
