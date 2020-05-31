package com.github.freewu32;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.github.freewu32.condition.RequiresNacos;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

import javax.inject.Inject;

/**
 * nacos客户端配置
 */
@RequiresNacos
@Factory
public class NacosFactory {

    @Inject
    private NacosConfiguration configuration;

    @Bean
    public NamingService namingService() throws NacosException {
        return com.alibaba.nacos.api.NacosFactory
                .createNamingService(configuration.getHost());
    }

    @Bean
    public NamingMaintainService namingMaintainService() throws NacosException {
        return com.alibaba.nacos.api.NacosFactory
                .createMaintainService(configuration.getHost());
    }
}
