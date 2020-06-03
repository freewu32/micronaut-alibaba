package com.github.freewu32.nacos.client;

import io.micronaut.core.annotation.Introspected;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Introspected
public class GetServiceResponse implements Serializable {
    private Map<String, String> metadata;

    private String groupName;

    private String namespaceId;

    private String name;

    private Map<String, String> selector;

    private float protectThreshold;

    private List<GetServiceCluster> clusters;

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getSelector() {
        return selector;
    }

    public void setSelector(Map<String, String> selector) {
        this.selector = selector;
    }

    public float getProtectThreshold() {
        return protectThreshold;
    }

    public void setProtectThreshold(float protectThreshold) {
        this.protectThreshold = protectThreshold;
    }

    public List<GetServiceCluster> getClusters() {
        return clusters;
    }

    public void setClusters(List<GetServiceCluster> clusters) {
        this.clusters = clusters;
    }
}
