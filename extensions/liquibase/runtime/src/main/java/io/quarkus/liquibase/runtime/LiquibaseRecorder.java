package io.quarkus.liquibase.runtime;

import java.lang.annotation.Annotation;
import java.util.Map.Entry;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.arc.runtime.BeanContainerListener;
import io.quarkus.liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseDataSource;
import io.quarkus.runtime.annotations.Recorder;
import liquibase.exception.LiquibaseException;

/**
 * The liquibase recorder
 */
@Recorder
public class LiquibaseRecorder {

    /**
     * Sets the liquibase build configuration
     * 
     * @param liquibaseBuildConfig the liquibase build time configuration
     * @return the bean container listener
     */
    public BeanContainerListener setLiquibaseBuildConfig(LiquibaseBuildTimeConfig liquibaseBuildConfig) {
        return beanContainer -> {
            LiquibaseProducer producer = beanContainer.instance(LiquibaseProducer.class);
            producer.setLiquibaseBuildConfig(liquibaseBuildConfig);
        };
    }

    /**
     * Configure the liquibase runtime properties
     * 
     * @param liquibaseRuntimeConfig the liquibase runtime configuration
     * @param container the bean container
     */
    public void configureLiquibaseProperties(LiquibaseRuntimeConfig liquibaseRuntimeConfig, BeanContainer container) {
        container.instance(LiquibaseProducer.class).setLiquibaseRuntimeConfig(liquibaseRuntimeConfig);
    }

    /**
     * Do start actions
     * 
     * @param config the runtime configuration
     * @param container the bean container
     */
    public void doStartActions(LiquibaseRuntimeConfig config, BeanContainer container) {
        try {
            if (config.defaultDataSource.cleanAtStart) {
                dropAll(container, Default.Literal.INSTANCE);
            }
            if (config.defaultDataSource.migrateAtStart) {
                migrate(container, Default.Literal.INSTANCE);
            }
            for (Entry<String, LiquibaseDataSourceRuntimeConfig> configPerDataSource : config.namedDataSources.entrySet()) {

                if (configPerDataSource.getValue().cleanAtStart) {
                    dropAll(container, LiquibaseDataSource.LiquibaseDataSourceLiteral.of(configPerDataSource.getKey()));
                }
                if (configPerDataSource.getValue().migrateAtStart) {
                    migrate(container, LiquibaseDataSource.LiquibaseDataSourceLiteral.of(configPerDataSource.getKey()));
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Drop all database objects
     * 
     * @param container the bean container
     * @param qualifier the bean qualifier
     * @throws LiquibaseException if the database actions fails
     */
    private void dropAll(BeanContainer container, AnnotationLiteral<? extends Annotation> qualifier) throws LiquibaseException {
        Liquibase liquibase = container.instance(Liquibase.class, qualifier);
        liquibase.dropAll();
    }

    /**
     * Migrate the database objects
     * 
     * @param container the bean container
     * @param qualifier the bean qualifier
     * @throws LiquibaseException if the database actions fails
     */
    private void migrate(BeanContainer container, AnnotationLiteral<? extends Annotation> qualifier) throws LiquibaseException {
        Liquibase liquibase = container.instance(Liquibase.class, qualifier);
        liquibase.update();
    }
}
