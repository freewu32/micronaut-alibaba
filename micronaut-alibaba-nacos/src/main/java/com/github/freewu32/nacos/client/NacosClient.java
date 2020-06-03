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
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        } else {
            return Flowable.fromCallable(() -> {
                NacosConfiguration.NacosRegistrationConfiguration registration =
                        (NacosConfiguration.NacosRegistrationConfiguration) nacosConfiguration
                                .getRegistration();
                GetInstancesResponse response = getInstances(serviceId, registration.getGroupName(), registration.getNamespaceId(),
                        registration.getClusterName(), registration.getEphemeral());

                return response.getHosts().stream().map(v -> {
                    return ServiceInstance.builder(serviceId, URI.create("http://" + v.getIp() + ":" + v.getPort()))
                            .instanceId(v.getInstanceId())
                            .metadata(v.getMetadata())
                            .build();
                }).collect(Collectors.toList());
            });
        }
    }

    @Override
    public Publisher<List<String>> getServiceIds() {
        return Flowable.fromCallable(() -> {
            NacosConfiguration.NacosRegistrationConfiguration registration =
                    (NacosConfiguration.NacosRegistrationConfiguration) nacosConfiguration
                            .getRegistration();
            return getServices(1, Integer.MAX_VALUE, registration.getGroupName(),
                    registration.getNamespaceId()).getDoms();
        });
    }
}
