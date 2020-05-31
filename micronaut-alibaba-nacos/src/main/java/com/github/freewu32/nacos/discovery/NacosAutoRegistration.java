package com.github.freewu32.nacos.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.github.freewu32.nacos.NacosConfiguration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.discovery.ServiceInstance;
import io.micronaut.discovery.client.registration.DiscoveryServiceAutoRegistration;
import io.micronaut.health.HealthStatus;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * nacos自动服务注册
 */
@Singleton
@Requires(beans = {NamingService.class, NamingMaintainService.class})
public class NacosAutoRegistration extends DiscoveryServiceAutoRegistration {

    @Inject
    private NamingService namingService;

    @Inject
    private NamingMaintainService namingMaintainService;

    protected NacosAutoRegistration(NacosConfiguration configuration) {
        super(configuration.getRegistration());
    }

    protected void pulsate(ServiceInstance instance, HealthStatus status) {
        try {
            namingService.getAllInstances(instance.getId())
                    .stream()
                    .filter(v -> v.getInstanceId().equals(instance.getInstanceId().get()))
                    .findFirst()
                    .ifPresent(i -> {
                        try {
                            i.setHealthy(status == HealthStatus.UP);
                            namingMaintainService.updateInstance(i.getServiceName(), i);
                        } catch (NacosException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    protected void deregister(ServiceInstance instance) {
        try {
            namingService.deregisterInstance(instance.getId(), instanceToNacosInstance(instance));
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    protected void register(ServiceInstance instance) {
        try {
            namingService.registerInstance(instance.getId(), instanceToNacosInstance(instance));
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    private Instance instanceToNacosInstance(ServiceInstance instance) {
        Instance nacosInstance = new Instance();
        nacosInstance.setIp(instance.getHost());
        nacosInstance.setPort(instance.getPort());
        nacosInstance.setMetadata(instance.getMetadata().asMap());
        instance.getInstanceId().ifPresent(nacosInstance::setInstanceId);
        return nacosInstance;
    }
}
