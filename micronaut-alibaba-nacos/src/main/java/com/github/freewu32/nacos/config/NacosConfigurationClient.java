package com.github.freewu32.nacos.config;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.github.freewu32.nacos.NacosConfiguration;
import com.github.freewu32.nacos.condition.RequiresNacos;
import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.*;
import io.micronaut.context.env.yaml.YamlPropertySourceLoader;
import io.micronaut.context.exceptions.ConfigurationException;
import io.micronaut.core.util.StringUtils;
import io.micronaut.discovery.config.ConfigurationClient;
import io.micronaut.jackson.env.JsonPropertySourceLoader;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import static io.micronaut.scheduling.TaskExecutors.SCHEDULED;

/**
 * nacos配置加载客户端
 */
@Singleton
@RequiresNacos
@Requires(beans = ConfigService.class)
@Requires(property = ConfigurationClient.ENABLED, value = StringUtils.TRUE, defaultValue = StringUtils.FALSE)
@BootstrapContextCompatible
public class NacosConfigurationClient implements ConfigurationClient {

    private final Map<String, PropertySourceLoader> loaderByFormatMap = new ConcurrentHashMap<>();

    private NacosConfiguration.NacosConfigDiscoveryConfiguration discoveryConfiguration;

    private ConfigService configService;

    private Executor executor;

    public NacosConfigurationClient(NacosConfiguration configuration,
                                    Environment environment,
                                    ConfigService configService,
                                    @Named(SCHEDULED) Executor executor) {
        this.discoveryConfiguration = configuration.getConfiguration();
        this.configService = configService;
        this.executor = executor;

        initLoaders(environment);
    }

    /**
     * 处理配置加载器
     */
    private void initLoaders(Environment environment) {
        Collection<PropertySourceLoader> loaders = environment.getPropertySourceLoaders();
        for (PropertySourceLoader loader : loaders) {
            Set<String> extensions = loader.getExtensions();
            for (String extension : extensions) {
                loaderByFormatMap.put(extension, loader);
            }
        }
    }

    @Override
    public Publisher<PropertySource> getPropertySources(Environment environment) {
        return Flowable.fromCallable(() -> {
            //获取文件类型
            String type = discoveryConfiguration.getType();
            //解析主配置
            String properties = configService.getConfig(discoveryConfiguration.getDataId(),
                    discoveryConfiguration.getGroup(), discoveryConfiguration.getGetConfigTimeout());
            PropertySource propertySource = genSourceForConfigValue(type, properties);
            if (discoveryConfiguration.isAutoRefresh()) {
                configService.addListener(discoveryConfiguration.getDataId(),
                        discoveryConfiguration.getGroup(), getListener((DefaultEnvironment) environment, type, propertySource));
            }
            return propertySource;
        });
    }

    /**
     * 生成自动刷新监听器
     */
    private Listener getListener(DefaultEnvironment environment,
                                 String type,
                                 PropertySource propertySource) {
        return new Listener() {
            @Override
            public Executor getExecutor() {
                return executor;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                environment.removePropertySource(propertySource);
                environment.addPropertySource(genSourceForConfigValue(type, configInfo));
            }
        };
    }

    /**
     * 解析数据
     */
    private PropertySource genSourceForConfigValue(String type, String configValue) {
        PropertySourceLoader propertySourceLoader = resolveLoader(type);
        Map<String, Object> m = propertySourceLoader.read(discoveryConfiguration.getDataId(),
                configValue.getBytes());
        return new MapPropertySource(NacosConfiguration.ID + "-" + discoveryConfiguration.getDataId(),
                m);
    }

    private PropertySourceLoader resolveLoader(String formatName) {
        return loaderByFormatMap.computeIfAbsent(formatName, f -> defaultLoader(formatName));
    }

    private PropertySourceLoader defaultLoader(String format) {
        try {
            switch (format) {
                case "json":
                    return new JsonPropertySourceLoader();
                case "properties":
                    return new PropertiesPropertySourceLoader();
                case "yml":
                case "yaml":
                    return new YamlPropertySourceLoader();
                default:
                    throw new ConfigurationException("Unsupported properties file format: " + format);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ConfigurationException("Unsupported properties file format: " + format);
    }

    @Override
    public String getDescription() {
        return NacosConfiguration.ID;
    }
}
