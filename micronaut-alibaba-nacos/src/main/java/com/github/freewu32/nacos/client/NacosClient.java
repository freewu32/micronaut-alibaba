package com.github.freewu32.nacos.client;

import com.github.freewu32.nacos.NacosConfiguration;
import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.discovery.DiscoveryClient;
import io.micronaut.discovery.ServiceInstance;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@Client(id = NacosConfiguration.ID, path = "/nacos/", configuration = NacosConfiguration.class)
@Requires(beans = NacosConfiguration.class)
@BootstrapContextCompatible
public abstract class NacosClient implements NacosOperations, DiscoveryClient {
    public static final String SERVICE_ID = NacosConfiguration.ID;

    private NacosConfiguration nacosConfiguration;

    @Inject
    public void setConsulConfiguration(NacosConfiguration nacosConfiguration) {
        if (nacosConfiguration != null) {
            this.nacosConfiguration = nacosConfiguration;
        }
    }

    @Override
    public String getDescription() {
        return NacosConfiguration.ID;
    }

    @Override
    public Publisher<List<ServiceInstance>> getInstances(String serviceId) {
        if (!nacosConfiguration.getDiscovery().isEnabled()) {
            return Publishers.just(Collections.emptyList());
        }
        if (SERVICE_ID.equals(serviceId)) {
            return Publishers.just(
                    Collections.singletonList(ServiceInstance.of(SERVICE_ID,
                            nacosConfiguration.getHost(),
                            nacosConfiguration.getPort()))
            );
        }
        return Flowable.empty();
    }

    @Override
    public Publisher<List<String>> getServiceIds() {
        return Flowable.empty();
    }
}
