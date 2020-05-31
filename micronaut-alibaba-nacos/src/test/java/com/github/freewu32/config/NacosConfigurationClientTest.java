package com.github.freewu32.config;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.naming.NamingService;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.runtime.Micronaut;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NacosConfigurationClientTest {

    @Test
    void getPropertySources() {
        ApplicationContext context = Micronaut.build(new String[]{})
                .environments(Environment.TEST).start();

        try {
            context.containsBean(ConfigService.class);

            int dbPort = context.getEnvironment().get("db.port", Integer.class)
                    .get();

            Assertions.assertEquals(dbPort, 3307);
        } finally {
            context.stop();
        }
    }
}