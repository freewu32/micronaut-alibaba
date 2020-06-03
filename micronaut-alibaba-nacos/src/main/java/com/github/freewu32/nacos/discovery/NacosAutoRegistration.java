package com.github.freewu32.nacos.discovery;

import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.freewu32.nacos.NacosConfiguration;
import com.github.freewu32.nacos.client.NacosOperations;
import io.micronaut.context.annotation.Requires;
import io.micronaut.discovery.ServiceInstance;
import io.micronaut.discovery.client.registration.DiscoveryServiceAutoRegistration;
import io.micronaut.health.HealthStatus;
import org.apache.commons.lang3.Validate;

import javax.inject.Singleton;

/**
 * nacos自动服务注册
 */
@Singleton
@Requires(beans = {NamingService.class, NamingMaintainService.class})
public class NacosAutoRegistration extends DiscoveryServiceAutoRegistration {

    private NacosOperations nacosClient;

    private NacosConfiguration.NacosRegistrationConfiguration registrationConfiguration;

    private ObjectMapper objectMapper;

    protected NacosAutoRegistration(NacosConfiguration configuration,
                                    NacosOperations nacosClient,
                                    ObjectMapper objectMapper) {
        super(configuration.getRegistration());
        this.nacosClient = nacosClient;
        this.objectMapper = objectMapper;
        this.registrationConfiguration = (NacosConfiguration.NacosRegistrationConfiguration)
                configuration.getRegistration();
    }

    protected void pulsate(ServiceInstance instance, HealthStatus status) {
        try {
            String result = nacosClient.sendInstanceBeat(instance.getId(),
                    objectMapper.writeValueAsString(status), registrationConfiguration
                            .getGroupName(), registrationConfiguration.getEphemeral());
            Validate.isTrue(result.equals("ok"), result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected void deregister(ServiceInstance instance) {
        String result = nacosClient.unRegisterInstance(instance.getId(), instance.getHost(), instance.getPort(),
                registrationConfiguration.getNamespaceId(), registrationConfiguration
                        .getGroupName(), registrationConfiguration.getClusterName(),
                registrationConfiguration.getEphemeral());
        Validate.isTrue(result.equals("ok"), result);
    }

    protected void register(ServiceInstance instance) {
        try {
            String result = nacosClient.registerInstance(instance.getHost(), instance.getPort(),
                    instance.getId(), registrationConfiguration.getNamespaceId(),
                    registrationConfiguration.getWeight(), registrationConfiguration.isEnabled(),
                    instance.getHealthStatus().equals(HealthStatus.UP), objectMapper
                            .writeValueAsString(instance.getMetadata().asMap()),
                    registrationConfiguration.getClusterName(), registrationConfiguration
                            .getGroupName(), registrationConfiguration.getEphemeral());
            Validate.isTrue(result.equals("ok"), result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
