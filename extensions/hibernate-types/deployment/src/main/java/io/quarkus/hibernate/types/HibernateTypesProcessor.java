package io.quarkus.hibernate.types;

import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.Feature;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

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
}
