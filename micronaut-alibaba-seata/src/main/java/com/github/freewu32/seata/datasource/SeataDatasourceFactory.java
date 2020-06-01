package com.github.freewu32.seata.datasource;

import com.github.freewu32.seata.SeataConfiguration;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.seata.rm.datasource.DataSourceProxy;

import javax.inject.Inject;
import javax.sql.DataSource;

@Factory
public class SeataDatasourceFactory {

    @Inject
    private SeataConfiguration configuration;

    @Bean
    @Primary
    public DataSource proxyDatasource(DataSource dataSource) {
        return new DataSourceProxy(dataSource, configuration.getResourceGroupId());
    }
}
