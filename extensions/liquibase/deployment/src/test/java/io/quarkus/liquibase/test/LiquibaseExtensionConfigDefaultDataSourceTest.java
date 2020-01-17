package io.quarkus.liquibase.test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.liquibase.Liquibase;
import io.quarkus.test.QuarkusUnitTest;

public class LiquibaseExtensionConfigDefaultDataSourceTest {

    @Inject
    Liquibase liquibase;

    @Inject
    LiquibaseExtensionConfigFixture fixture;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(LiquibaseExtensionConfigFixture.class)
                    .addAsResource("config-for-default-datasource.properties", "application.properties"));

    @Test
    @DisplayName("Reads liquibase configuration for default datasource correctly")
    public void testLiquibaseConfigInjection() {
        fixture.assertAllConfigurationSettings(liquibase.getConfiguration(), "");
        assertFalse(fixture.migrateAtStart(""));
    }
}
