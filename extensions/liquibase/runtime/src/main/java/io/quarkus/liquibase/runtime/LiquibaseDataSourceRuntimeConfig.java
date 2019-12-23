package io.quarkus.liquibase.runtime;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

/**
 * The liquibase data source runtime time configuration
 */
@ConfigGroup
public final class LiquibaseDataSourceRuntimeConfig {

    /**
     * The default liquibase lock table
     */
    static final String DEFAULT_LOCK_TABLE = "DATABASECHANGELOGLOCK";

    /**
     * The default liquibase log table
     */
    static final String DEFAULT_LOG_TABLE = "DATABASECHANGELOG";

    /**
     * Creates a {@link LiquibaseDataSourceRuntimeConfig} with default settings.
     *
     * @return {@link LiquibaseDataSourceRuntimeConfig}
     */
    public static final LiquibaseDataSourceRuntimeConfig defaultConfig() {
        LiquibaseDataSourceRuntimeConfig config = new LiquibaseDataSourceRuntimeConfig();
        config.databaseChangeLogLockTableName = Optional.of(DEFAULT_LOCK_TABLE);
        config.databaseChangeLogTableName = Optional.of(DEFAULT_LOG_TABLE);
        return config;
    }

    /**
     * true to execute Liquibase automatically when the application starts, false otherwise.
     *
     */
    @ConfigItem
    public boolean migrateAtStart;

    /**
     * true to execute Liquibase clean command automatically when the application starts, false otherwise.
     *
     */
    @ConfigItem
    public boolean cleanAtStart;

    /**
     * The liquibase contexts
     */
    @ConfigItem
    public Optional<List<String>> contexts = Optional.empty();

    /**
     * The liquibase labels
     */
    @ConfigItem
    public Optional<List<String>> labels = Optional.empty();

    /**
     * The liquibase change log lock table name. Name of table to use for tracking concurrent Liquibase usage
     */
    @ConfigItem(defaultValue = DEFAULT_LOCK_TABLE)
    public Optional<String> databaseChangeLogLockTableName = Optional.empty();

    /**
     * The liquibase change log table name. Name of table to use for tracking change history
     */
    @ConfigItem(defaultValue = DEFAULT_LOG_TABLE)
    public Optional<String> databaseChangeLogTableName = Optional.empty();

    /**
     * Database-specific function for generating the current date/time.
     */
    @ConfigItem
    public Optional<String> currentDateTimeFunction = Optional.empty();

    /**
     * Default catalog name
     */
    @ConfigItem
    public Optional<String> defaultCatalogName = Optional.empty();

    /**
     * Default schema name
     */
    @ConfigItem
    public Optional<String> defaultSchemaName = Optional.empty();

    /**
     * Liquibase catalog name
     */
    @ConfigItem
    public Optional<String> liquibaseCatalogName = Optional.empty();

    /**
     * Liquibase schema name
     */
    @ConfigItem
    public Optional<String> liquibaseSchemaName = Optional.empty();

    /**
     * Liquibase table space name
     */
    @ConfigItem
    public Optional<String> liquibaseTablespaceName = Optional.empty();

}
