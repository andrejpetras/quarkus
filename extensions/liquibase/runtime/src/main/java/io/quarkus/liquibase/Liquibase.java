package io.quarkus.liquibase;

import java.io.PrintStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import io.agroal.api.AgroalDataSource;
import io.quarkus.liquibase.runtime.LiquibaseConfig;
import liquibase.CatalogAndSchema;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.change.CheckSum;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.ChangeSetStatus;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.changelog.RanChangeSet;
import liquibase.database.Database;
import liquibase.database.core.*;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.lockservice.DatabaseChangeLogLock;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

/**
 * The quarkus liquibase bean
 */
public class Liquibase {

    /**
     * The datasource
     */
    private DataSource dataSource;

    /**
     * The resource accessor
     */
    private ResourceAccessor resourceAccessor;

    /**
     * The liquibase executor instance
     */
    private liquibase.Liquibase executor;

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
    public Liquibase(LiquibaseConfig config, AgroalDataSource datasource) {
        this.dataSource = datasource;
        this.resourceAccessor = new ClassLoaderResourceAccessor(Thread.currentThread().getContextClassLoader());
        this.config = config;

        Database database = null;
        if (datasource != null) {
            try {
                Class<?> driverClass = datasource.getConfiguration()
                        .connectionPoolConfiguration()
                        .connectionFactoryConfiguration()
                        .connectionProviderClass();
                database = guessDatabase(driverClass.getName());
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }

            if (database != null) {
                database.setDatabaseChangeLogLockTableName(config.databaseChangeLogLockTableName);
                database.setDatabaseChangeLogTableName(config.databaseChangeLogTableName);
                config.liquibaseCatalogName.ifPresent(database::setLiquibaseCatalogName);
                config.liquibaseSchemaName.ifPresent(database::setLiquibaseSchemaName);
                config.liquibaseTablespaceName.ifPresent(database::setLiquibaseTablespaceName);

                try {
                    if (config.defaultCatalogName.isPresent()) {
                        database.setDefaultCatalogName(config.defaultCatalogName.get());
                    }
                    if (config.defaultSchemaName.isPresent()) {
                        database.setDefaultSchemaName(config.defaultSchemaName.get());
                    }
                } catch (DatabaseException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }
        executor = new liquibase.Liquibase(config.changeLog, resourceAccessor, database);
    }

    /**
     * Creates the liquibase database base on the resolved driver name.
     *
     * @param resolvedDriver the resolved driver name.
     * @return the liquibase database.
     */
    private Database guessDatabase(String resolvedDriver) {
        if (resolvedDriver.contains("postgresql")) {
            return new PostgresDatabase();
        }
        if (resolvedDriver.contains("org.h2.Driver")) {
            return new H2Database();
        }
        if (resolvedDriver.contains("org.mariadb.jdbc.Driver")) {
            return new MariaDBDatabase();
        }
        if (resolvedDriver.contains("com.mysql.cj.jdbc.Driver")) {
            return new MySQLDatabase();
        }
        if (resolvedDriver.contains("org.apache.derby.jdbc.ClientDriver")) {
            return new DerbyDatabase();
        }
        if (resolvedDriver.contains("microsoft")) {
            return new MSSQLDatabase();
        }
        return null;
    }

    /**
     * Gets the liquibase configuration
     * 
     * @return the liquibase configuration
     */
    public LiquibaseConfig getConfiguration() {
        return config;
    }

    /**
     * Gets the change log file path
     * 
     * @return the change log file path
     */
    public String getChangeLog() {
        return config.changeLog;
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#getDatabaseChangeLog()} method
     * 
     * @return the database change log
     * @throws LiquibaseException if the method fails.
     */
    public DatabaseChangeLog getDatabaseChangeLog() throws LiquibaseException {
        liquibase.Liquibase liquibase = new liquibase.Liquibase(config.changeLog, resourceAccessor, (Database) null);
        return liquibase.getDatabaseChangeLog();
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#dropAll(CatalogAndSchema...)} method
     *
     * @throws LiquibaseException if the method fails.
     */
    public void dropAll(CatalogAndSchema... schemas) throws LiquibaseException {
        executeConsumer(t -> t.dropAll(schemas));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#listUnrunChangeSets(Contexts, LabelExpression, boolean)} method
     * 
     * @return the list of change set
     * @throws LiquibaseException if the method fails
     */
    public List<ChangeSet> listUnrunChangeSets() throws LiquibaseException {
        return listUnrunChangeSets(createContexts(), createLabels(), true);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#listUnrunChangeSets(Contexts, LabelExpression, boolean)} method
     * 
     * @param contexts the contexts
     * @param labels the label expression
     * @param checkLiquibaseTables check liquibase tables
     * @return the list of change set
     * @throws LiquibaseException if the method fails
     */
    List<ChangeSet> listUnrunChangeSets(Contexts contexts, LabelExpression labels, boolean checkLiquibaseTables)
            throws LiquibaseException {
        return executeFunction(t -> t.listUnrunChangeSets(contexts, labels, checkLiquibaseTables));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#getChangeSetStatuses(Contexts, LabelExpression, boolean)} method
     * 
     * @return the list of change set
     * @throws LiquibaseException if the method fails
     */
    public List<ChangeSetStatus> getChangeSetStatuses() throws LiquibaseException {
        return getChangeSetStatuses(createContexts(), createLabels(), true);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#getChangeSetStatuses(Contexts, LabelExpression, boolean)} method
     * 
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @param checkLiquibaseTables check liquibase tables
     * @return the list of change set
     * @throws LiquibaseException if the method fails
     */
    public List<ChangeSetStatus> getChangeSetStatuses(Contexts contexts, LabelExpression labelExpression,
            boolean checkLiquibaseTables) throws LiquibaseException {
        return executeFunction(t -> t.getChangeSetStatuses(contexts, labelExpression, checkLiquibaseTables));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#update(Contexts, LabelExpression)} method
     * 
     * @throws LiquibaseException if the method fails
     */
    public void update() throws LiquibaseException {
        update(createContexts(), createLabels());
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#update(Contexts, LabelExpression)} method
     * 
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @throws LiquibaseException if the method fails
     */
    public void update(Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        update(contexts, labelExpression, true);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#update(Contexts, LabelExpression, boolean)} method
     *
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @param checkLiquibaseTables check liquibase tables
     * @throws LiquibaseException if the method fails
     */
    public void update(final Contexts contexts, final LabelExpression labelExpression, final boolean checkLiquibaseTables)
            throws LiquibaseException {
        executeConsumer(t -> t.update(contexts, labelExpression, checkLiquibaseTables));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#update(String, Contexts, LabelExpression)} method
     * 
     * @param tag the tag
     * @throws LiquibaseException if the method fails
     */
    public void update(String tag) throws LiquibaseException {
        update(tag, createContexts(), createLabels());
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#update(String, Contexts, LabelExpression)} method
     *
     * @param tag the tag
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @throws LiquibaseException if the method fails
     */
    public void update(String tag, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        executeConsumer(t -> t.update(tag, contexts, labelExpression));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#update(String, Contexts, LabelExpression, Writer)} method
     *
     * @param tag the tag
     * @param output the output
     * @throws LiquibaseException if the method fails
     */
    public void update(String tag, Writer output) throws LiquibaseException {
        update(tag, createContexts(), createLabels(), output);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#update(String, Contexts, LabelExpression, Writer)} method
     *
     * @param tag the tag
     * @param output the output
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @throws LiquibaseException if the method fails
     */
    public void update(String tag, Contexts contexts, LabelExpression labelExpression, Writer output)
            throws LiquibaseException {
        executeConsumer(t -> t.update(tag, contexts, labelExpression, output));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(int, String, Contexts, LabelExpression)} method.
     *
     * @param changesToApply changes to apply
     * @throws LiquibaseException if the method fails
     */
    public void update(int changesToApply) throws LiquibaseException {
        update(changesToApply, createContexts(), createLabels());
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(int, String, Contexts, LabelExpression)} method.
     *
     * @param changesToApply changes to apply
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @throws LiquibaseException if the method fails
     */
    public void update(int changesToApply, Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        executeConsumer(t -> t.update(changesToApply, contexts, labelExpression));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(int, String, Contexts, LabelExpression, Writer)} method.
     *
     * @param changesToApply changes to apply
     * @param output the output
     * @throws LiquibaseException if the method fails
     */
    public void update(int changesToApply, Writer output) throws LiquibaseException {
        this.update(changesToApply, createContexts(), createLabels(), output);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(int, String, Contexts, LabelExpression, Writer)} method.
     *
     * @param changesToApply changes to apply
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @param output the output
     * @throws LiquibaseException if the method fails
     */
    public void update(int changesToApply, Contexts contexts, LabelExpression labelExpression, Writer output)
            throws LiquibaseException {
        executeConsumer(t -> t.update(changesToApply, contexts, labelExpression, output));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(int, String, Contexts, LabelExpression, Writer)} method.
     *
     * @param changesToRollback changes to roll back
     * @param rollbackScript the rollback script
     * @param output the output
     * @throws LiquibaseException if the method fails
     */
    public void rollback(int changesToRollback, String rollbackScript, Writer output) throws LiquibaseException {
        rollback(changesToRollback, rollbackScript, createContexts(), createLabels(), output);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(int, String, Contexts, LabelExpression, Writer)} method.
     *
     * @param changesToRollback changes to roll back
     * @param rollbackScript the rollback script
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @param output the output
     * @throws LiquibaseException if the method fails
     */
    public void rollback(int changesToRollback, String rollbackScript, Contexts contexts, LabelExpression labelExpression,
            Writer output) throws LiquibaseException {
        executeConsumer(t -> t.rollback(changesToRollback, rollbackScript, contexts, labelExpression, output));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(int, String, Contexts, LabelExpression)} method.
     *
     * @param changesToRollback changes to roll back
     * @param rollbackScript the rollback script
     * @throws LiquibaseException if the method fails
     */
    public void rollback(int changesToRollback, String rollbackScript) throws LiquibaseException {
        rollback(changesToRollback, rollbackScript, createContexts(), createLabels());
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(int, String, Contexts, LabelExpression)} method.
     *
     * @param changesToRollback changes to roll back
     * @param rollbackScript the rollback script
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @throws LiquibaseException if the method fails
     */
    public void rollback(int changesToRollback, String rollbackScript, Contexts contexts, LabelExpression labelExpression)
            throws LiquibaseException {
        executeConsumer(t -> t.rollback(changesToRollback, rollbackScript, contexts, labelExpression));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(String, String, Contexts, LabelExpression, Writer)} method.
     *
     * @param tagToRollBackTo tag to roll back to
     * @param rollbackScript the rollback script
     * @param output the output
     * @throws LiquibaseException if the method fails
     */
    public void rollback(String tagToRollBackTo, String rollbackScript, Writer output) throws LiquibaseException {
        rollback(tagToRollBackTo, rollbackScript, createContexts(), createLabels(), output);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(String, String, Contexts, LabelExpression, Writer)} method.
     *
     * @param tagToRollBackTo tag to roll back to
     * @param rollbackScript the rollback script
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @param output the output
     * @throws LiquibaseException if the method fails
     */
    public void rollback(String tagToRollBackTo, String rollbackScript, Contexts contexts, LabelExpression labelExpression,
            Writer output) throws LiquibaseException {
        executeConsumer(t -> t.rollback(tagToRollBackTo, rollbackScript, contexts, labelExpression, output));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(String, String, Contexts, LabelExpression)} method.
     *
     * @param tagToRollBackTo tag to roll back to
     * @param rollbackScript the rollback script
     * @throws LiquibaseException if the method fails
     */
    public void rollback(String tagToRollBackTo, String rollbackScript) throws LiquibaseException {
        rollback(tagToRollBackTo, rollbackScript, createContexts(), createLabels());
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(String, String, Contexts, LabelExpression)} method.
     *
     * @param tagToRollBackTo tag to roll back to
     * @param rollbackScript the rollback script
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @throws LiquibaseException if the method fails
     */
    public void rollback(String tagToRollBackTo, String rollbackScript, Contexts contexts, LabelExpression labelExpression)
            throws LiquibaseException {
        executeConsumer(t -> t.rollback(tagToRollBackTo, rollbackScript, contexts, labelExpression));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(Date, String, Contexts, LabelExpression, Writer)} method.
     *
     * @param output the output
     * @param dateToRollBackTo date to roll back to
     * @param rollbackScript the rollback script
     * @throws LiquibaseException if the method fails
     */
    public void rollback(Date dateToRollBackTo, String rollbackScript, Writer output) throws LiquibaseException {
        rollback(dateToRollBackTo, rollbackScript, createContexts(), createLabels(), output);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(Date, String, Contexts, LabelExpression, Writer)} method.
     *
     * @param output the output
     * @param dateToRollBackTo date to roll back to
     * @param rollbackScript the rollback script
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @throws LiquibaseException if the method fails
     */
    public void rollback(Date dateToRollBackTo, String rollbackScript, Contexts contexts, LabelExpression labelExpression,
            Writer output) throws LiquibaseException {
        executeConsumer(t -> t.rollback(dateToRollBackTo, rollbackScript, createContexts(), createLabels()));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(Date, String, Contexts, LabelExpression)} method.
     *
     * @param dateToRollBackTo date to roll back to
     * @param rollbackScript the rollback script
     * @throws LiquibaseException if the method fails
     */
    public void rollback(Date dateToRollBackTo, String rollbackScript) throws LiquibaseException {
        rollback(dateToRollBackTo, rollbackScript, createContexts(), createLabels());
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#rollback(Date, String, Contexts, LabelExpression)} method.
     *
     * @param dateToRollBackTo date to roll back to
     * @param rollbackScript the rollback script
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @throws LiquibaseException if the method fails
     */
    public void rollback(Date dateToRollBackTo, String rollbackScript, Contexts contexts, LabelExpression labelExpression)
            throws LiquibaseException {
        executeConsumer(t -> t.rollback(dateToRollBackTo, rollbackScript, contexts, labelExpression));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#changeLogSync(Contexts, LabelExpression)} method.
     *
     * @throws LiquibaseException if the method fails
     */
    public void changeLogSync() throws LiquibaseException {
        changeLogSync(createContexts(), createLabels());
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#changeLogSync(Contexts, LabelExpression)} method.
     *
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @throws LiquibaseException if the method fails
     */
    public void changeLogSync(Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        executeConsumer(t -> t.changeLogSync(contexts, labelExpression));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#changeLogSync(Contexts, LabelExpression, Writer)} method.
     *
     * @param output the output
     * @throws LiquibaseException if the method fails
     */
    public void changeLogSync(Writer output) throws LiquibaseException {
        changeLogSync(createContexts(), createLabels(), output);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#changeLogSync(Contexts, LabelExpression, Writer)} method.
     *
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @param output the output
     * @throws LiquibaseException if the method fails
     */
    public void changeLogSync(Contexts contexts, LabelExpression labelExpression, Writer output) throws LiquibaseException {
        executeConsumer(t -> t.changeLogSync(contexts, labelExpression, output));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#markNextChangeSetRan(Contexts, LabelExpression, Writer)} method.
     *
     * @param output the output
     * @throws LiquibaseException if the method fails
     */
    public void markNextChangeSetRan(Writer output) throws LiquibaseException {
        markNextChangeSetRan(createContexts(), createLabels(), output);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#markNextChangeSetRan(Contexts, LabelExpression, Writer)} method.
     *
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @param output the output
     * @throws LiquibaseException if the method fails
     */
    public void markNextChangeSetRan(Contexts contexts, LabelExpression labelExpression, Writer output)
            throws LiquibaseException {
        executeConsumer(t -> t.markNextChangeSetRan(contexts, labelExpression, output));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#markNextChangeSetRan(Contexts, LabelExpression)} method.
     *
     * @throws LiquibaseException if the method fails
     */
    public void markNextChangeSetRan() throws LiquibaseException {
        markNextChangeSetRan(createContexts(), createLabels());
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#markNextChangeSetRan(Contexts, LabelExpression)} method.
     *
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @throws LiquibaseException if the method fails
     */
    public void markNextChangeSetRan(Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        executeConsumer(t -> t.markNextChangeSetRan(contexts, labelExpression));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#futureRollbackSQL(String, Contexts, LabelExpression, Writer)} method.
     *
     * @param tag the tag
     * @param output the output
     * @throws LiquibaseException if the method fails
     */
    public void futureRollbackSQL(String tag, Writer output) throws LiquibaseException {
        futureRollbackSQL(tag, createContexts(), createLabels(), output);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#futureRollbackSQL(String, Contexts, LabelExpression, Writer)} method.
     *
     * @param tag the tag
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @param output the output
     * @throws LiquibaseException if the method fails
     */
    public void futureRollbackSQL(String tag, Contexts contexts, LabelExpression labelExpression, Writer output)
            throws LiquibaseException {
        executeConsumer(t -> t.futureRollbackSQL(tag, contexts, labelExpression, output));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#futureRollbackSQL(Integer, Contexts, LabelExpression, Writer, boolean)}
     * method.
     *
     * @param count the count
     * @param output the output
     * @param checkLiquibaseTables check liquibase tables
     * @throws LiquibaseException if the method fails
     */
    public void futureRollbackSQL(Integer count, Writer output, boolean checkLiquibaseTables) throws LiquibaseException {
        futureRollbackSQL(count, createContexts(), createLabels(), output, checkLiquibaseTables);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#futureRollbackSQL(Integer, Contexts, LabelExpression, Writer, boolean)}
     * method.
     * 
     * @param count the count
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @param output the output
     * @param checkLiquibaseTables check liquibase tables
     * @throws LiquibaseException if the method fails
     */
    public void futureRollbackSQL(Integer count, Contexts contexts, LabelExpression labelExpression, Writer output,
            boolean checkLiquibaseTables) throws LiquibaseException {
        executeConsumer(t -> t.futureRollbackSQL(count, contexts, labelExpression, output, checkLiquibaseTables));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#tag(String)} method.
     *
     * @param tag the tag
     * @return returns {@code true} if tag exists
     * @throws LiquibaseException if the method fails.
     */
    public void tag(String tag) throws LiquibaseException {
        executeConsumer(t -> t.tag(tag));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#tagExists(String)} method.
     * 
     * @param tag the tag
     * @return returns {@code true} if tag exists
     * @throws LiquibaseException if the method fails.
     */
    public boolean tagExists(String tag) throws LiquibaseException {
        return executeFunction(t -> t.tagExists(tag));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#updateTestingRollback(String, Contexts, LabelExpression)} method.
     * 
     * @throws LiquibaseException if the method fails.
     */
    public void updateTestingRollback() throws LiquibaseException {
        updateTestingRollback(null, createContexts(), createLabels());
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#updateTestingRollback(String, Contexts, LabelExpression)} method.
     * 
     * @param tag the tag
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @throws LiquibaseException if the method fails.
     */
    public void updateTestingRollback(String tag, Contexts contexts, LabelExpression labelExpression)
            throws LiquibaseException {
        executeConsumer(t -> t.updateTestingRollback(tag, contexts, labelExpression));
    }

    /**
     * Implementation of the
     * {@link liquibase.Liquibase#checkLiquibaseTables(boolean, DatabaseChangeLog, Contexts, LabelExpression)}
     * method.
     * 
     * @param updateExistingNullChecksums update existing null checksums
     * @param databaseChangeLog database change log
     * @throws LiquibaseException if the method fails.
     */
    public void checkLiquibaseTables(boolean updateExistingNullChecksums, DatabaseChangeLog databaseChangeLog)
            throws LiquibaseException {
        checkLiquibaseTables(updateExistingNullChecksums, databaseChangeLog, createContexts(), createLabels());
    }

    /**
     * Implementation of the
     * {@link liquibase.Liquibase#checkLiquibaseTables(boolean, DatabaseChangeLog, Contexts, LabelExpression)}
     * method.
     * 
     * @param updateExistingNullChecksums update existing null checksums
     * @param databaseChangeLog database change log
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @throws LiquibaseException if the method fails.
     */
    public void checkLiquibaseTables(boolean updateExistingNullChecksums, DatabaseChangeLog databaseChangeLog,
            Contexts contexts, LabelExpression labelExpression) throws LiquibaseException {
        executeConsumer(t -> t.checkLiquibaseTables(updateExistingNullChecksums, databaseChangeLog, contexts, labelExpression));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#listLocks()} method.
     * 
     * @return list of database change log locks
     * @throws LiquibaseException if the method fails.
     */
    public DatabaseChangeLogLock[] listLocks() throws LiquibaseException {
        return executeFunction(liquibase.Liquibase::listLocks);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#reportLocks(PrintStream)} method.
     * 
     * @throws LiquibaseException if the method fails.
     */
    public void reportLocks(PrintStream out) throws LiquibaseException {
        executeConsumer(t -> t.reportLocks(out));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#forceReleaseLocks()} method.
     * 
     * @throws LiquibaseException if the method fails.
     */
    public void forceReleaseLocks() throws LiquibaseException {
        executeConsumer(liquibase.Liquibase::forceReleaseLocks);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#reportStatus(boolean, Contexts, Writer)} method.
     * 
     * @param verbose the verbose flag
     * @param output the output
     * @throws LiquibaseException if the method fails.
     */
    public void reportStatus(boolean verbose, Writer output) throws LiquibaseException {
        reportStatus(verbose, createContexts(), createLabels(), output);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#reportStatus(boolean, Contexts, Writer)} method.
     * 
     * @param verbose the verbose flag
     * @param contexts the contexts
     * @param labels the label expression
     * @param output the output
     * @throws LiquibaseException if the method fails.
     */
    public void reportStatus(boolean verbose, Contexts contexts, LabelExpression labels, Writer output)
            throws LiquibaseException {
        executeConsumer(t -> t.reportStatus(verbose, contexts, labels, output));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#listUnexpectedChangeSets(Contexts, LabelExpression)} method.
     * 
     * @return the collection of the ran change sets
     * @throws LiquibaseException if the method fails.
     */
    public Collection<RanChangeSet> listUnexpectedChangeSets() throws LiquibaseException {
        return listUnexpectedChangeSets(createContexts(), createLabels());
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#listUnexpectedChangeSets(Contexts, LabelExpression)} method.
     * 
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @return the collection of the ran change sets
     * @throws LiquibaseException if the method fails.
     */
    public Collection<RanChangeSet> listUnexpectedChangeSets(Contexts contexts, LabelExpression labelExpression)
            throws LiquibaseException {
        return executeFunction(t -> t.listUnexpectedChangeSets(contexts, labelExpression));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#reportStatus(boolean, Contexts, LabelExpression, Writer)} method
     * 
     * @param verbose the verbose flag
     * @param output the writer output
     * @throws LiquibaseException if the method fails.
     */
    public void reportUnexpectedChangeSets(boolean verbose, Writer output) throws LiquibaseException {
        reportUnexpectedChangeSets(verbose, createContexts(), createLabels(), output);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#reportStatus(boolean, Contexts, LabelExpression, Writer)} method
     * 
     * @param verbose the verbose flag
     * @param contexts the contexts
     * @param labelExpression the label expression
     * @param output the writer output
     * @throws LiquibaseException if the method fails.
     */
    public void reportUnexpectedChangeSets(boolean verbose, Contexts contexts, LabelExpression labelExpression, Writer output)
            throws LiquibaseException {
        executeConsumer(t -> t.reportUnexpectedChangeSets(verbose, contexts, labelExpression, output));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#clearCheckSums()} method
     * 
     * @throws LiquibaseException if the method fails.
     */
    public void clearCheckSums() throws LiquibaseException {
        executeConsumer(liquibase.Liquibase::clearCheckSums);
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#calculateCheckSum(String)} method
     *
     * @return check sum of the change
     * @throws LiquibaseException if the method fails.
     */
    public CheckSum calculateCheckSum(String changeSetIdentifier) throws LiquibaseException {
        return executeFunction(t -> t.calculateCheckSum(changeSetIdentifier));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#calculateCheckSum(String, String, String)} ()} method.
     * 
     * @param filename the file name
     * @param id the change if
     * @param author the author
     * @return the check sum of the change
     * @throws LiquibaseException if the method fails.
     */
    public CheckSum calculateCheckSum(String filename, String id, String author) throws LiquibaseException {
        return executeFunction(t -> t.calculateCheckSum(filename, id, author));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#diff(Database, Database, CompareControl)} ()} method.
     * 
     * @param referenceDatabase the reference database
     * @param targetDatabase the target database
     * @param compareControl the compare control
     * @return the diff result
     * @throws LiquibaseException if the method fails
     */
    public DiffResult diff(Database referenceDatabase, Database targetDatabase, CompareControl compareControl)
            throws LiquibaseException {
        return executeFunction(t -> t.diff(referenceDatabase, targetDatabase, compareControl));
    }

    /**
     * Implementation of the {@link liquibase.Liquibase#validate()} method.
     * 
     * @throws LiquibaseException if the method fails.
     */
    public void validate() throws LiquibaseException {
        executeConsumer(liquibase.Liquibase::validate);
    }

    /**
     * Creates the default labels base on the configuration
     * 
     * @return the label expression
     */
    private LabelExpression createLabels() {
        return new LabelExpression(config.labels);
    }

    /**
     * Creates the default contexts base on the configuration
     * 
     * @return the contexts
     */
    private Contexts createContexts() {
        return new Contexts(config.contexts);
    }

    /**
     * The liquibase function
     * 
     * @param <Liquibase> the liquibase instance
     */
    public interface LiquibaseFunction<Liquibase, R> {
        R apply(Liquibase t) throws LiquibaseException;
    }

    /**
     * Execute the function of the liquibase
     * 
     * @param function the function
     * @throws LiquibaseException if the method fails.
     */
    private <R> R executeFunction(LiquibaseFunction<liquibase.Liquibase, R> function) throws LiquibaseException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            executor.getDatabase().setConnection(new JdbcConnection(connection));
            return function.apply(executor);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            closeLiquibase(connection);
        }
    }

    /**
     * The liquibase consumer
     * 
     * @param <Liquibase> the liquibase instance
     */
    public interface LiquibaseConsumer<Liquibase> {
        void apply(Liquibase t) throws LiquibaseException;
    }

    /**
     * Execute the consumer method of the liquibase
     * 
     * @param consumer the consumer
     * @throws LiquibaseException if the method fails.
     */
    private void executeConsumer(LiquibaseConsumer<liquibase.Liquibase> consumer) throws LiquibaseException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            executor.getDatabase().setConnection(new JdbcConnection(connection));
            consumer.apply(executor);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            closeLiquibase(connection);
        }
    }

    /**
     * Close the database connection for the liquibase instance.
     *
     * @param con the database connection
     * @throws LiquibaseException if the method fails
     */
    private void closeLiquibase(Connection con) throws LiquibaseException {
        if (executor != null && executor.getDatabase() != null) {
            executor.getDatabase().close();
        } else if (con != null) {
            try {
                if (!con.getAutoCommit()) {
                    con.rollback();
                }
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
