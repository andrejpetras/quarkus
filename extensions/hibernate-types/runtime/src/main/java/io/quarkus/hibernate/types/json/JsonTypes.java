package io.quarkus.hibernate.types.json;

import java.sql.Blob;

/**
 * JSON hibernate types.
 */
public final class JsonTypes {

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
     * If you are using <strong>MySQL</strong>, you should use this {@link JsonStringType} to map the
     * <strong>{@code json}</strong> column type. For more details, check out
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
     * @see JsonStringType
     */
    public static final String JSON_STRING = "json";

    /**
     * Maps any given Java object on a JSON column type that is managed via
     * {@link java.sql.PreparedStatement#setObject(int, Object)} at JDBC Driver level.
     * <p>
     * If you are using <strong>PostgreSQL</strong>, you should use this {@link JsonBinaryType} to map both
     * <strong>{@code jsonb}</strong> and <strong>{@code json}</strong> column types.
     * <p>
     * For more details about how to use it, check out
     * <a href="https://vladmihalcea.com/how-to-map-json-objects-using-generic-hibernate-types/">this article</a> on
     * <a href="https://vladmihalcea.com/">vladmihalcea.com</a>.
     *
     * @author Vlad Mihalcea
     *
     * @see JsonBinaryType
     */
    public static final String JSON_BIN = "jsonb";

    /**
     * Maps any given Java object on a JSON column type that is managed via
     * {@link java.sql.PreparedStatement#setBlob(int, Blob)} at JDBC Driver level.
     * <p>
     * If you are using Oracle, you should use this {@link JsonBlobType} to map a {@code BLOB} column type storing JSON.
     * <p>
     * For more details about how to use it, check out <a href="https://vladmihalcea.com/oracle-json-jpa-hibernate/">this
     * article</a> on <a href="https://vladmihalcea.com/">vladmihalcea.com</a>.
     *
     * @author Vlad Mihalcea
     *
     * @see JsonBlobType
     */
    public static final String JSON_BLOB = "jsonb-lob";

    /**
     * Maps a JSON object on a JSON column type that is managed via {@link java.sql.PreparedStatement#setString(int, String)}
     * at JDBC Driver level. For instance, if you are using MySQL, you should be using {@link JsonObjectStringType} to map the
     * {@code json} column type to a Jackson JsonNode object.
     * <p>
     * For more details about how to use it, check out <a href=
     * "https://vladmihalcea.com/how-to-store-schema-less-eav-entity-attribute-value-data-using-json-and-hibernate/">this
     * article</a> on <a href="https://vladmihalcea.com/">vladmihalcea.com</a>.
     *
     * @author Vlad Mihalcea
     *
     * @see JsonObjectStringType
     */
    public static final String JSON_OBJECT_STRING = "jsonb-node";

    /**
     * Maps a Jackson JsonNode object on a JSON column type that is managed via
     * {@link java.sql.PreparedStatement#setObject(int, Object)} at JDBC Driver level. For instance, if you are using
     * PostgreSQL, you should be using {@link JsonObjectBinaryType} to map both {@code jsonb} and {@code json} column types to a
     * Jackson JsonNode object.
     *
     * <p>
     * For more details about how to use it, check out <a href=
     * "https://vladmihalcea.com/how-to-store-schema-less-eav-entity-attribute-value-data-using-json-and-hibernate/">this
     * article</a> on <a href="https://vladmihalcea.com/">vladmihalcea.com</a>.
     *
     * @author Vlad Mihalcea
     *
     * @see JsonObjectBinaryType
     */
    public static final String JSON_OBJECT_BIN = "jsonb-node";

    /**
     * Default constructor.
     */
    private JsonTypes() {
    }
}
