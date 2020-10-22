package io.quarkus.hibernate.types.json.impl;

import javax.enterprise.inject.Default;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.hibernate.types.json.JsonMapper;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class JsonMapperRecorder {

    public void init(BeanContainer container) {
        JsonMapper service = container.instance(JsonMapper.class, Default.Literal.INSTANCE);
        if (service == null) {
            throw new IllegalStateException("Missing JsonMapper instance implementation [jsonb,jackson,...]");
        }
        JsonMapperInstance.setJsonMapper(service);
    }
}
