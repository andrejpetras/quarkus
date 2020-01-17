package io.quarkus.liquibase.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.liquibase.Liquibase;
import io.quarkus.test.QuarkusUnitTest;
import liquibase.changelog.ChangeSetStatus;

public class LiquibaseExtensionMigrateAtStartTest {
    // Quarkus built object
    @Inject
    Liquibase liquibase;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("migrate-at-start-config.properties", "application.properties"));

    @Test
    @DisplayName("Migrates at start correctly")
    public void testLiquibaseConfigInjection() throws Exception {
        List<ChangeSetStatus> status = liquibase.getChangeSetStatuses();
        assertNotNull(status);
        assertEquals(1, status.size());
        assertEquals("id-1", status.get(0).getChangeSet().getId());
        assertFalse(status.get(0).getWillRun());
    }
}
