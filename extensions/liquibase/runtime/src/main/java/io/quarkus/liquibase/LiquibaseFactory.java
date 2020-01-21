package io.quarkus.liquibase;

import javax.sql.DataSource;

import io.agroal.api.AgroalDataSource;
import io.quarkus.liquibase.runtime.LiquibaseConfig;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

/**
 * The quarkus liquibase factory
 */
public class LiquibaseFactory {

    /**
     * The datasource
     */
    private DataSource dataSource;

    /**
     * The liquibase configuration
     */
    private LiquibaseConfig config;

    /**
     * The default constructor
     *
     * @param config the liquibase configuration
     * @param datasource the datasource for this liquibase bean
     */
    public LiquibaseFactory(LiquibaseConfig config, AgroalDataSource datasource) {
        this.dataSource = datasource;
        this.config = config;
    }

    /**
     * Creates the liquibase context.
     * 
     * @return the liquibase context.
     */
    public LiquibaseContext createContext() {
        try {
            ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(Thread.currentThread().getContextClassLoader());

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
            ;
            if (database != null) {
                database.setDatabaseChangeLogLockTableName(config.databaseChangeLogLockTableName);
                database.setDatabaseChangeLogTableName(config.databaseChangeLogTableName);
                config.liquibaseCatalogName.ifPresent(database::setLiquibaseCatalogName);
                config.liquibaseSchemaName.ifPresent(database::setLiquibaseSchemaName);
                config.liquibaseTablespaceName.ifPresent(database::setLiquibaseTablespaceName);

                if (config.defaultCatalogName.isPresent()) {
                    database.setDefaultCatalogName(config.defaultCatalogName.get());
                }
                if (config.defaultSchemaName.isPresent()) {
                    database.setDefaultSchemaName(config.defaultSchemaName.get());
                }
            }
            return new LiquibaseContext(config, resourceAccessor, database);

        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Gets the liquibase configuration
     *
     * @return the liquibase configuration
     */
    public LiquibaseConfig getConfiguration() {
        return config;
    }
}
