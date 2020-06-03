package com.github.freewu32.nacos.client;

import io.micronaut.core.annotation.Introspected;

import java.io.Serializable;
import java.util.List;

@Introspected
public class GetInstancesResponse implements Serializable {
    private String dom;

    private long cacheMillis;

    private boolean useSpecifiedURL;

    private List<GetInstancesHost> hosts;

    private String checksum;

    private long lastRefTime;

    private String env;

    private String clusters;

    public String getDom() {
        return dom;
    }

    public void setDom(String dom) {
        this.dom = dom;
    }

    public long getCacheMillis() {
        return cacheMillis;
    }

    public void setCacheMillis(long cacheMillis) {
        this.cacheMillis = cacheMillis;
    }

    public boolean isUseSpecifiedURL() {
        return useSpecifiedURL;
    }

    public void setUseSpecifiedURL(boolean useSpecifiedURL) {
        this.useSpecifiedURL = useSpecifiedURL;
    }

    public List<GetInstancesHost> getHosts() {
        return hosts;
    }

    public void setHosts(List<GetInstancesHost> hosts) {
        this.hosts = hosts;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public long getLastRefTime() {
        return lastRefTime;
    }

    public void setLastRefTime(long lastRefTime) {
        this.lastRefTime = lastRefTime;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getClusters() {
        return clusters;
    }

    public void setClusters(String clusters) {
        this.clusters = clusters;
    }
}
