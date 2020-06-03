package com.github.freewu32.nacos.client;

import com.github.freewu32.nacos.NacosConfiguration;
import io.micronaut.discovery.DiscoveryClient;

public interface NacosClient extends NacosOperations, DiscoveryClient {
    String SERVICE_ID = NacosConfiguration.ID;
}
