package io.quarkus.liquibase.test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.liquibase.Liquibase;
import io.quarkus.liquibase.LiquibaseDataSource;
import io.quarkus.test.QuarkusUnitTest;

/**
 * Test a full configuration with default and two named datasources plus their liquibase settings.
 */
public class LiquibaseExtensionConfigMultiDataSourcesTest {

    @Inject
    LiquibaseExtensionConfigFixture fixture;

    @Inject
    Liquibase liquibase;

    @Inject
    @LiquibaseDataSource("users")
    Liquibase liquibaseUsers;

    @Inject
    @LiquibaseDataSource("inventory")
    Liquibase liquibaseInventory;

    @Inject
    @Named("liquibase_inventory")
    Liquibase liquibaseNamedInventory;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(LiquibaseExtensionConfigFixture.class)
                    .addAsResource("config-for-multiple-datasources.properties", "application.properties"));

    @Test
    @DisplayName("Reads default liquibase configuration for default datasource correctly")
    public void testLiquibaseDefaultConfigInjection() {
        fixture.assertAllConfigurationSettings(liquibase.getConfiguration(), "");
        assertFalse(fixture.migrateAtStart(""));
    }

    @Test
    @DisplayName("Reads liquibase configuration for datasource named 'users' correctly")
    public void testLiquibaseConfigNamedUsersInjection() {
        fixture.assertAllConfigurationSettings(liquibaseUsers.getConfiguration(), "users");
        assertFalse(fixture.migrateAtStart(""));
    }

    @Test
    @DisplayName("Reads liquibase configuration for datasource named 'inventory' correctly")
    public void testLiquibaseConfigNamedInventoryInjection() {
        fixture.assertAllConfigurationSettings(liquibaseInventory.getConfiguration(), "inventory");
        assertFalse(fixture.migrateAtStart(""));
    }

    @Test
    @DisplayName("Reads liquibase configuration directly named 'liquibase_inventory' correctly")
    public void testLiquibaseConfigDirectlyNamedInventoryInjection() {
        fixture.assertAllConfigurationSettings(liquibaseNamedInventory.getConfiguration(), "inventory");
        assertFalse(fixture.migrateAtStart(""));
    }
}
