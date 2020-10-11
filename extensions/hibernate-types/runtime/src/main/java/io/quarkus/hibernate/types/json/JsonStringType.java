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
 * <strong>{@code VARCHAR2}</strong> column type storing JSON. For more details, check out
 * <a href="https://vladmihalcea.com/oracle-json-jpa-hibernate/">this article</a> on
 * <a href="https://vladmihalcea.com/">vladmihalcea.com</a>.
 * <p>
 * If you are using <strong>SQL Server</strong>, you should use this {@link JsonStringType} to map an
 * <strong>{@code NVARCHAR}</strong> column type storing JSON. For more details, check out
 * <a href="https://vladmihalcea.com/sql-server-json-hibernate/">this article</a> on
 * <a href="https://vladmihalcea.com/">vladmihalcea.com</a>.
 * <p>
 * If you are using <strong>MySQL</strong>, you should use this {@link JsonStringType} to map the <strong>{@code json}</strong>
 * column type. For more details, check out
 * <a href="https://vladmihalcea.com/how-to-map-json-objects-using-generic-hibernate-types/">this article</a> on
 * <a href="https://vladmihalcea.com/">vladmihalcea.com</a>.
 * <p>
 * If you are using <strong>PostgreSQL</strong>, then you should <strong>NOT</strong> use this {@link JsonStringType}. You
 * should use {@link JsonBinaryType} instead. For more details, check out
 * <a href="https://vladmihalcea.com/how-to-map-json-objects-using-generic-hibernate-types/">this article</a> on
 * <a href="https://vladmihalcea.com/">vladmihalcea.com</a>.
 *
 * @author Vlad Mihalcea
 *
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
