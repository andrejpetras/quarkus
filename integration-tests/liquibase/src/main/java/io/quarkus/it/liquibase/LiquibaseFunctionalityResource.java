package io.quarkus.it.liquibase;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import io.quarkus.liquibase.LiquibaseContext;
import io.quarkus.liquibase.LiquibaseFactory;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.ChangeSetStatus;
import liquibase.exception.LiquibaseException;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LiquibaseFunctionalityResource {

    @Inject
    LiquibaseFactory liquibaseFactory;

    @GET
    @Path("update")
    public String doUpdateAuto() {
        try (LiquibaseContext liquibase = liquibaseFactory.createContext()) {
            liquibase.update();
            List<ChangeSetStatus> status = liquibase.getChangeSetStatuses();
            List<ChangeSetStatus> changeSets = Objects.requireNonNull(status,
                    "ChangeSetStatus is null! Database update was not applied");
            return changeSets.stream()
                    .filter(ChangeSetStatus::getPreviouslyRan)
                    .map(ChangeSetStatus::getChangeSet)
                    .map(ChangeSet::getId)
                    .collect(Collectors.joining(","));
        } catch (LiquibaseException ex) {
            throw new WebApplicationException(ex.getMessage(), ex);
        }
    }

}
