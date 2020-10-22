package io.quarkus.hibernate.types;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.deployment.Feature;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.hibernate.types.json.impl.JsonMapperRecorder;

public class HibernateTypesProcessor {

    @BuildStep
    FeatureBuildItem createFeatureItem() {
        return new FeatureBuildItem(Feature.HIBERNATE_TYPES);
    }

    @BuildStep
    UnremovableBeanBuildItem ensureJsonParserAvailable() {
        return UnremovableBeanBuildItem.beanClassNames(
                "io.quarkus.jackson.ObjectMapperProducer",
                "com.fasterxml.jackson.databind.ObjectMapper",
                "io.quarkus.jsonb.JsonbProducer",
                "javax.json.bind.Jsonb",
                "io.quarkus.hibernate.types.jsonb.JsonbMapperProducer",
                "io.quarkus.hibernate.types.jackson.JacksonMapperProducer");
    }

    @Record(RUNTIME_INIT)
    @BuildStep
    public void hibernateTypesInitBuildStep(JsonMapperRecorder recorder, BeanContainerBuildItem beanContainer) {
        BeanContainer container = beanContainer.getValue();
        recorder.init(container);
    }
}
