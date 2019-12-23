package io.quarkus.liquibase.test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.inject.Inject;

import org.h2.jdbc.JdbcSQLException;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.agroal.api.AgroalDataSource;
import io.quarkus.liquibase.Liquibase;
import io.quarkus.test.QuarkusUnitTest;
import liquibase.changelog.ChangeSetStatus;
import liquibase.exception.LiquibaseException;

public class LiquibaseExtensionCleanAndMigrateAtStartTest {

    @Inject
    Liquibase liquibase;

    @Inject
    AgroalDataSource defaultDataSource;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("clean-and-migrate-at-start-config.properties", "application.properties"));

    @Test
    @DisplayName("Clean and migrate at start correctly")
    public void testLiquibaseConfigInjection() throws SQLException, LiquibaseException {

        try (Connection connection = defaultDataSource.getConnection(); Statement stat = connection.createStatement()) {
            try (ResultSet executeQuery = stat
                    .executeQuery("select * from fake_existing_tbl")) {
                fail("fake_existing_tbl should not exist");
            } catch (JdbcSQLException e) {
                // expected fake_existing_tbl does not exist
            }
        }
        List<ChangeSetStatus> status = liquibase.getChangeSetStatuses();
        assertNotNull(status, "Status is null");
        assertEquals(1, status.size(), "The set of changes is not null");
        assertFalse(status.get(0).getWillRun());
    }
}
