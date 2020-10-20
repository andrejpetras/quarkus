package io.quarkus.hibernate.types;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.Unremovable;
import io.quarkus.hibernate.types.json.JsonMapper;

@Singleton
public class MyParamMapperProducer {

    @Produces
    @Singleton
    @DefaultBean
    @Unremovable
    public JsonMapper jsonb() {
        return new MyParamMapper();
    }

}
