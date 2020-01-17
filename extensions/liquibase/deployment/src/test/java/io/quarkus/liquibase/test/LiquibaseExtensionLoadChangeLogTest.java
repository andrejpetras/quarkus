package io.quarkus.liquibase.test;

import static org.junit.jupiter.api.Assertions.*;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.liquibase.Liquibase;
import io.quarkus.test.QuarkusUnitTest;
import liquibase.changelog.DatabaseChangeLog;

public class LiquibaseExtensionLoadChangeLogTest {
    // Quarkus built object
    @Inject
    Liquibase liquibase;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("load-change-log-config.properties", "application.properties"));

    @Test
    @DisplayName("Load the change log config correctly")
    public void testLiquibaseConfigInjection() throws Exception {
        DatabaseChangeLog changelog = liquibase.getDatabaseChangeLog();
        assertEquals("db/xml/changeLog.xml", changelog.getFilePath());
        assertNotNull(changelog.getChangeSets());
        assertEquals("db/xml/create-tables.xml", changelog.getChangeSets().get(0).getFilePath());
        assertEquals("db/xml/test/test.xml", changelog.getChangeSets().get(1).getFilePath());
    }

}
