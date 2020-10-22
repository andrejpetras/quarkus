package io.quarkus.hibernate.types.jsonb;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.json.bind.Jsonb;

import io.quarkus.hibernate.types.json.JsonMapper;

@Singleton
public class JsonbMapperProducer {

    @Produces
    @Singleton
    public JsonMapper jsonb(Jsonb jsonb) {
        return new JsonbMapper(jsonb);
    }

}
