package com.github.freewu32.nacos.client;

import io.micronaut.core.annotation.Introspected;

import java.io.Serializable;
import java.util.Map;

@Introspected
public class GetServiceCluster implements Serializable {

    private Map<String, String> healthChecker;

    private Map<String, String> metadata;

    private String name;

    public Map<String, String> getHealthChecker() {
        return healthChecker;
    }

    public void setHealthChecker(Map<String, String> healthChecker) {
        this.healthChecker = healthChecker;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
