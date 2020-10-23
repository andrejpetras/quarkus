package io.quarkus.it.hibernate.types.jackson.postgresql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@QuarkusTest
public class HibernateTypesTest {

    @Test
    public void findTest() {
        Response response = RestAssured.when()
                .get("/tests/1")
                .andReturn();

        Assertions.assertEquals(200, response.statusCode());
        MyEntity entity = response.as(MyEntity.class);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals("1", entity.getId());
        Assertions.assertNotNull(entity.getJsonb());
        Assertions.assertEquals("1", entity.getJsonb().getId());
        Assertions.assertNotNull(entity.getVarchar());
        Assertions.assertEquals("1", entity.getVarchar().getId());
    }
}
