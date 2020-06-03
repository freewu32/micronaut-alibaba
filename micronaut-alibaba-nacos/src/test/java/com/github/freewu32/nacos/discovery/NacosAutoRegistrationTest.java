package com.github.freewu32.nacos.discovery;

import com.github.freewu32.nacos.client.NacosClient;
import io.micronaut.context.ApplicationContext;
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
            NacosClient namingService = context.getBean(NacosClient.class);

            String serviceName = context.getEnvironment().get("micronaut.application.name",
                    String.class).get();

            Assertions.assertEquals(namingService.getInstances(serviceName,
                    null, null, null, null)
                    .getHosts().size(), 1);
        } finally {
            context.stop();
        }
    }
}
