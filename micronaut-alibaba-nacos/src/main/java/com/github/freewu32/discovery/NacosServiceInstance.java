package com.github.freewu32.discovery;

import com.alibaba.nacos.api.naming.pojo.Instance;
import io.micronaut.core.convert.value.ConvertibleValues;
import io.micronaut.core.convert.value.ConvertibleValuesMap;
import io.micronaut.discovery.ServiceInstance;

import java.net.URI;
import java.util.Optional;

public class NacosServiceInstance implements ServiceInstance {

    private String schema;

    private Instance nacosInstance;

    public NacosServiceInstance(String schema, Instance nacosInstance) {
        this.schema = schema;
        this.nacosInstance = nacosInstance;
    }

    @Override
    public String getId() {
        return nacosInstance.getServiceName();
    }

    @Override
    public Optional<String> getInstanceId() {
        return Optional.of(nacosInstance.getInstanceId());
    }

    @Override
    public String getHost() {
        return nacosInstance.getIp();
    }

    @Override
    public int getPort() {
        return nacosInstance.getPort();
    }

    @Override
    public ConvertibleValues<String> getMetadata() {
        return new ConvertibleValuesMap<>(nacosInstance.getMetadata());
    }

    @Override
    public URI getURI() {
        return URI.create(schema + "://" + getHost() + ":" + getPort());
    }
}
