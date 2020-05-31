package com.github.freewu32.nacos;

import com.github.freewu32.nacos.condition.RequiresNacos;
import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.util.Toggleable;
import io.micronaut.discovery.DiscoveryConfiguration;
import io.micronaut.discovery.client.DiscoveryClientConfiguration;
import io.micronaut.discovery.config.ConfigDiscoveryConfiguration;
import io.micronaut.discovery.registration.RegistrationConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

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

    private NacosConfigDiscoveryConfiguration configuration = new NacosConfigDiscoveryConfiguration();

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

    public NacosConfigDiscoveryConfiguration getConfiguration() {
        return configuration;
    }

    @Inject
    public void setConfiguration(NacosConfigDiscoveryConfiguration configuration) {
        if (configuration != null) {
            this.configuration = configuration;
        }
    }

    @ConfigurationProperties(DiscoveryConfiguration.PREFIX)
    @BootstrapContextCompatible
    public static class NacosDiscoveryConfiguration extends DiscoveryConfiguration {
        public static final String PREFIX = NacosConfiguration.PREFIX + "." + DiscoveryConfiguration.PREFIX;
    }

    @ConfigurationProperties(RegistrationConfiguration.PREFIX)
    @BootstrapContextCompatible
    public static class NacosRegistrationConfiguration extends RegistrationConfiguration {
        public static final String PREFIX = NacosConfiguration.PREFIX + "." + RegistrationConfiguration.PREFIX;
    }

    @ConfigurationProperties(ConfigDiscoveryConfiguration.PREFIX)
    @BootstrapContextCompatible
    public static class NacosConfigDiscoveryConfiguration implements Toggleable {
        public static final String PREFIX = NacosConfiguration.PREFIX + "." + ConfigDiscoveryConfiguration.PREFIX;

        /**
         * 是否启用
         */
        private boolean enabled;

        /**
         * 主配置服务器地址
         */
        private String serverAddr = "localhost";

        /**
         * 主配置 data-id
         */
        private String dataId;

        /**
         * 主配置 group-id
         */
        private String group = "DEFAULT_GROUP";

        /**
         * 获取配置的超时时间
         */
        private long getConfigTimeout = 5000;

        /**
         * 主配置 配置文件类型
         */
        private String type;

        /**
         * 是否开启自动刷新
         */
        private boolean autoRefresh = true;

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getServerAddr() {
            return serverAddr;
        }

        public void setServerAddr(String serverAddr) {
            this.serverAddr = serverAddr;
        }

        public String getDataId() {
            return dataId;
        }

        public void setDataId(String dataId) {
            this.dataId = dataId;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public long getGetConfigTimeout() {
            return getConfigTimeout;
        }

        public void setGetConfigTimeout(long getConfigTimeout) {
            this.getConfigTimeout = getConfigTimeout;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isAutoRefresh() {
            return autoRefresh;
        }

        public void setAutoRefresh(boolean autoRefresh) {
            this.autoRefresh = autoRefresh;
        }
    }
}
