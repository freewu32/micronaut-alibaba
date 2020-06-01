package com.github.freewu32.seata;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.seata.core.rpc.netty.RmMessageListener;
import io.seata.core.rpc.netty.RmRpcClient;
import io.seata.core.rpc.netty.TmRpcClient;
import io.seata.rm.DefaultRMHandler;
import io.seata.rm.DefaultResourceManager;

import javax.inject.Inject;

@Factory
public class SeataFactory {

    @Inject
    private SeataConfiguration configuration;

    @Bean(preDestroy = "destroy")
    public RmRpcClient rmRpcClient() {
        RmRpcClient rmRpcClient = RmRpcClient.getInstance(configuration
                .getApplicationId(), configuration.getTransactionServiceGroup());
        rmRpcClient.setResourceManager(DefaultResourceManager.get());
        rmRpcClient.setClientMessageListener(new RmMessageListener(DefaultRMHandler.get(), rmRpcClient));
        rmRpcClient.init();
        return rmRpcClient;
    }

    @Bean(preDestroy = "destroy")
    public TmRpcClient tmRpcClient() {
        TmRpcClient tmRpcClient = TmRpcClient.getInstance(configuration.getApplicationId()
                , configuration.getTransactionServiceGroup());
        tmRpcClient.init();
        return tmRpcClient;
    }
}
