package com.github.freewu32.nacos;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.github.freewu32.nacos.condition.RequiresNacos;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.annotation.Factory;

import javax.inject.Inject;

/**
 * nacos客户端配置
 */
@RequiresNacos
@Factory
@BootstrapContextCompatible
public class NacosFactory {

    @Inject
    private NacosConfiguration configuration;

    @Bean
    public ConfigService nacosConfigService() throws NacosException {
        return com.alibaba.nacos.api.NacosFactory
                .createConfigService(configuration.getConfiguration().getServerAddr());
    }
}
