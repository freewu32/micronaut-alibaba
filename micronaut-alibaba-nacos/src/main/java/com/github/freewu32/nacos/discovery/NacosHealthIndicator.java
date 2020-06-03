package com.github.freewu32.nacos.discovery;

import com.github.freewu32.nacos.NacosConfiguration;
import com.github.freewu32.nacos.client.AbstractNacosClient;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Requires(classes = HealthIndicator.class)
@Requires(beans = AbstractNacosClient.class)
@Requires(property = NacosConfiguration.PREFIX + ".health-check", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
public class NacosHealthIndicator implements HealthIndicator {

    @Inject
    private AbstractNacosClient nacosClient;

    @Override
    public Publisher<HealthResult> getResult() {
        return Flowable.just(nacosClient.getMetrics().getStatus()).map(v -> {
            HealthStatus status = v.equals("UP") ? HealthStatus.UP : HealthStatus.DOWN;
            return HealthResult.builder(NacosConfiguration.ID).details(v)
                    .status(status).build();
        });
    }
}
