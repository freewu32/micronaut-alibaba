package com.github.freewu32;

import com.github.freewu32.condition.RequiresNacos;
import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.discovery.DiscoveryConfiguration;
import io.micronaut.discovery.client.DiscoveryClientConfiguration;
import io.micronaut.discovery.consul.ConsulConfiguration;
import io.micronaut.discovery.registration.RegistrationConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * nacos配置属性
 */
@RequiresNacos
@ConfigurationProperties(NacosConfiguration.PREFIX)
@BootstrapContextCompatible
public class NacosConfiguration extends DiscoveryClientConfiguration {
    public static final String ID = "nacos";

    public static final String PREFIX = "nacos.client";

    private NacosRegistrationConfiguration registration = new NacosRegistrationConfiguration();

    private ConnectionPoolConfiguration pool = new ConnectionPoolConfiguration();

    private NacosDiscoveryConfiguration discovery = new NacosDiscoveryConfiguration();

    @Nonnull
    @Override
    public DiscoveryConfiguration getDiscovery() {
        return discovery;
    }

    @Nullable
    @Override
    public RegistrationConfiguration getRegistration() {
        return registration;
    }

    @Override
    protected String getServiceID() {
        return ID;
    }

    @Override
    public ConnectionPoolConfiguration getConnectionPoolConfiguration() {
        return pool;
    }

    @ConfigurationProperties(DiscoveryConfiguration.PREFIX)
    @BootstrapContextCompatible
    public class NacosDiscoveryConfiguration extends DiscoveryConfiguration {
        public static final String PREFIX = ConsulConfiguration.PREFIX + "." + DiscoveryConfiguration.PREFIX;
    }

    @ConfigurationProperties(RegistrationConfiguration.PREFIX)
    @BootstrapContextCompatible
    public class NacosRegistrationConfiguration extends RegistrationConfiguration {
        public static final String PREFIX = ConsulConfiguration.PREFIX + "." + RegistrationConfiguration.PREFIX;
    }
}
