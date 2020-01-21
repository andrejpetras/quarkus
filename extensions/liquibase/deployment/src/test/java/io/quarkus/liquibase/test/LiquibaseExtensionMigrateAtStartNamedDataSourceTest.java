package io.quarkus.liquibase.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.liquibase.LiquibaseContext;
import io.quarkus.liquibase.LiquibaseDataSource;
import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.QuarkusUnitTest;
import liquibase.changelog.ChangeSetStatus;

/**
 * Same as {@link LiquibaseExtensionMigrateAtStartTest} for named datasources.
 */
public class LiquibaseExtensionMigrateAtStartNamedDataSourceTest {

    @Inject
    @LiquibaseDataSource("users")
    LiquibaseFactory liquibaseFactory;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("db/changeLog.xml", "db/changeLog.xml")
                    .addAsResource("migrate-at-start-config-named-datasource.properties", "application.properties"));

    @Test
    @DisplayName("Migrates at start for datasource named 'users' correctly")
    public void testLiquibaseConfigInjection() throws Exception {
        try (LiquibaseContext liquibase = liquibaseFactory.createContext()) {
            List<ChangeSetStatus> status = liquibase.getChangeSetStatuses();
            assertNotNull(status);
            assertEquals(1, status.size());
            assertEquals("id-1", status.get(0).getChangeSet().getId());
            assertFalse(status.get(0).getWillRun());
        }
    }
}
