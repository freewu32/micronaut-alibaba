package com.github.freewu32.nacos.config;

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
            int dbPort = context.getEnvironment().get("db.port", Integer.class)
                    .get();

            Assertions.assertEquals(dbPort, 3307);
        } finally {
            context.stop();
        }
    }
}