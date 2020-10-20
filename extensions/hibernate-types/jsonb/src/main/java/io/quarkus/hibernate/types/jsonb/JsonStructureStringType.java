package io.quarkus.hibernate.types.jsonb;

import javax.json.JsonObject;
import javax.json.JsonStructure;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;

import io.quarkus.hibernate.types.json.JsonTypes;
import io.quarkus.hibernate.types.json.impl.JsonObjectTypeDescriptor;
import io.quarkus.hibernate.types.json.impl.JsonStringSqlTypeDescriptor;

/**
 * Maps a object {@link JsonStructure} object on a JSON column type that is managed via
 * {@link java.sql.PreparedStatement#setString(int, String)} at JDBC Driver level. For instance, if you are using MySQL, you
 * should be using {@link JsonStructureStringType} to map the {@code json} column type to a Jackson {@link JsonObject} object.
 */
public class JsonStructureStringType extends AbstractSingleColumnStandardBasicType<JsonStructure> {

    public JsonStructureStringType() {
        super(JsonStringSqlTypeDescriptor.INSTANCE,
                new JsonObjectTypeDescriptor<JsonStructure>(JsonStructure.class));
    }

    @Override
    public String getName() {
        return JsonTypes.JSON_OBJECT_STRING;
    }
}
