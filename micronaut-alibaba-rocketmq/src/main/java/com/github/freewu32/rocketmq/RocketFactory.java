package com.github.freewu32.rocketmq;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Requires;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.remoting.RPCHook;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@Requires(beans = RocketConfiguration.class)
public class RocketFactory {
    private RocketConfiguration configuration;

    private ApplicationContext context;

    public RocketFactory(RocketConfiguration configuration,
                         ApplicationContext context) {
        this.configuration = configuration;
        this.context = context;
    }

    @Bean(preDestroy = "shutdown")
    public MQProducer producer() throws Exception {
        RocketConfiguration.ProducerConfiguration producerConfiguration = configuration
                .getProducer();
        Optional<RPCHook> rpcHook = (Optional<RPCHook>) this.context.findBean(Class.forName(producerConfiguration
                .getRpcHookClassName()));
        return new DefaultMQProducer(producerConfiguration.getNamespace(),
                producerConfiguration.getProducerGroup(), rpcHook.orElse(null),
                producerConfiguration.isEnableMsgTrace(), producerConfiguration.getCustomizedTraceTopic());
    }

    @Bean(preDestroy = "shutdown")
    public MQConsumer consumer(@Nullable AllocateMessageQueueStrategy allocateMessageQueueStrategy)
            throws Exception {
        RocketConfiguration.ConsumerConfiguration consumerConfiguration = configuration
                .getConsumer();
        Optional<RPCHook> rpcHook = (Optional<RPCHook>) this.context.findBean(Class.forName(consumerConfiguration
                .getRpcHookClassName()));
        return new DefaultMQPushConsumer(consumerConfiguration.getNamespace(),
                consumerConfiguration.getConsumerGroup(), rpcHook.orElse(null),
                allocateMessageQueueStrategy, consumerConfiguration.isEnableMsgTrace(),
                consumerConfiguration.getCustomizedTraceTopic());
    }
}
