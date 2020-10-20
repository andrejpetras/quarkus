package io.quarkus.hibernate.types.json;

import java.util.Properties;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.usertype.DynamicParameterizedType;

import io.quarkus.hibernate.types.json.impl.JsonStringSqlTypeDescriptor;
import io.quarkus.hibernate.types.json.impl.JsonTypeDescriptor;

/**
 * Maps any given Java object on a JSON column type that is managed via
 * {@link java.sql.PreparedStatement#setString(int, String)} at JDBC Driver level.
 * <p>
 * If you are using <strong>Oracle</strong>, you should use this {@link JsonStringType} to map a
 * <strong>{@code VARCHAR2}</strong> column type storing JSON.
 * <p>
 * If you are using <strong>SQL Server</strong>, you should use this {@link JsonStringType} to map an
 * <strong>{@code NVARCHAR}</strong> column type storing JSON.
 * <p>
 * If you are using <strong>MySQL</strong>, you should use this {@link JsonStringType} to map the <strong>{@code json}</strong>
 * column type.
 * <p>
 * If you are using <strong>PostgreSQL</strong>, then you should <strong>NOT</strong> use this {@link JsonStringType}. You
 * should use {@link JsonBinaryType} instead.
 */
public class JsonStringType extends AbstractSingleColumnStandardBasicType<Object> implements DynamicParameterizedType {

    public JsonStringType() {
        super(JsonStringSqlTypeDescriptor.INSTANCE, new JsonTypeDescriptor());
    }

    @Override
    public String getName() {
        return JsonTypes.JSON_STRING;
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        ((JsonTypeDescriptor) getJavaTypeDescriptor()).setParameterValues(parameters);
    }
}
