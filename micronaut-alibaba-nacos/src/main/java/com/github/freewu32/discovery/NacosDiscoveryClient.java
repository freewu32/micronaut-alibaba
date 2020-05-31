package com.github.freewu32.discovery;

import com.alibaba.nacos.api.naming.NamingService;
import com.github.freewu32.NacosConfiguration;
import com.github.freewu32.condition.RequiresNacos;
import io.micronaut.discovery.DiscoveryClient;
import io.micronaut.discovery.ServiceInstance;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiresNacos
@Singleton
public class NacosDiscoveryClient implements DiscoveryClient {

    @Inject
    private NamingService namingService;

    @Override
    public Publisher<List<ServiceInstance>> getInstances(String serviceId) {
        return Flowable.fromCallable(() -> namingService.getAllInstances(serviceId).stream()
                .map(v -> new NacosServiceInstance("http", v))
                .collect(Collectors.toList()));
    }

    @Override
    public Publisher<List<String>> getServiceIds() {
        return Flowable.fromCallable(() -> namingService
                .getServicesOfServer(1, Integer.MAX_VALUE).getData());
    }

    @Override
    public String getDescription() {
        return NacosConfiguration.ID;
    }

    @Override
    public void close() throws IOException {

    }
}
