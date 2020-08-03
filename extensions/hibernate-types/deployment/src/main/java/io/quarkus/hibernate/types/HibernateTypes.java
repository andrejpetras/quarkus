package io.quarkus.hibernate.types;

import io.quarkus.deployment.Capability;
import io.quarkus.deployment.Feature;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CapabilityBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class HibernateTypes {

    @BuildStep
    FeatureBuildItem createFeatureItem() {
        return new FeatureBuildItem(Feature.HIBERNATE_TYPES);
    }

    @BuildStep
    CapabilityBuildItem capability() {
        return new CapabilityBuildItem(Capability.HIBERNATE_TYPES);
    }

}
