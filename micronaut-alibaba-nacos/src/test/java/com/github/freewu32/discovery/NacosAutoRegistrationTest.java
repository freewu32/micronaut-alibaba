package com.github.freewu32.discovery;

import com.alibaba.nacos.api.naming.NamingService;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.DefaultApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.runtime.Micronaut;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NacosAutoRegistrationTest {

    @Test
    public void testRegister() throws Exception {
        ApplicationContext context = Micronaut.build(new String[]{})
                .environments(Environment.TEST).start();

        try {
            NamingService namingService = context.getBean(NamingService.class);

            String serviceName = context.getEnvironment().get("micronaut.application.name",
                    String.class).get();

            Assertions.assertEquals(namingService.getAllInstances(serviceName).size(), 1);
        } finally {
            context.stop();
        }
    }
}
