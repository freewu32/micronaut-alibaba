package com.github.freewu32.config;

import com.alibaba.nacos.api.config.ConfigService;
import com.github.freewu32.NacosConfiguration;
import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.*;
import io.micronaut.context.env.yaml.YamlPropertySourceLoader;
import io.micronaut.context.exceptions.ConfigurationException;
import io.micronaut.core.util.StringUtils;
import io.micronaut.discovery.config.ConfigurationClient;
import io.micronaut.discovery.consul.condition.RequiresConsul;
import io.micronaut.jackson.env.JsonPropertySourceLoader;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * nacos配置加载客户端
 */
@Singleton
@RequiresConsul
@Requires(beans = ConfigService.class)
@Requires(property = ConfigurationClient.ENABLED, value = StringUtils.TRUE, defaultValue = StringUtils.FALSE)
@BootstrapContextCompatible
public class NacosConfigurationClient implements ConfigurationClient {

    private final Map<String, PropertySourceLoader> loaderByFormatMap = new ConcurrentHashMap<>();

    private NacosConfiguration.NacosConfigDiscoveryConfiguration discoveryConfiguration;

    @Inject
    private ConfigService configService;

    public NacosConfigurationClient(NacosConfiguration configuration,
                                    Environment environment) {
        this.discoveryConfiguration = configuration.getConfiguration();

        //处理配置加载器
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
            //解析主配置
            String properties = configService.getConfig(discoveryConfiguration.getDataId(),
                    discoveryConfiguration.getGroup(), discoveryConfiguration
                            .getGetConfigTimeout());

            String type = discoveryConfiguration.getType();

            switch (type) {
                case "json":
                case "yaml":
                case "properties":
                    PropertySourceLoader propertySourceLoader = resolveLoader(type);
                    Map<String, Object> m = propertySourceLoader.read(discoveryConfiguration.getDataId(),
                            properties.getBytes());
                    return new MapPropertySource(discoveryConfiguration.getDataId(),
                            m);
                default:
                    throw new RuntimeException("not support type" + type);
            }
        });
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
                    // no-op
            }
        } catch (Exception e) {
            // ignore, fallback to exception
        }
        throw new ConfigurationException("Unsupported properties file format: " + format);
    }

    @Override
    public String getDescription() {
        return NacosConfiguration.ID;
    }
}
