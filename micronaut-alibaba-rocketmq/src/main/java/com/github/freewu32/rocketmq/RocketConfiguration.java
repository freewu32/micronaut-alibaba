package com.github.freewu32.rocketmq;

import com.github.freewu32.rocketmq.condition.RequiresRocket;
import io.micronaut.context.annotation.ConfigurationProperties;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.common.MixAll;

import javax.inject.Inject;

@RequiresRocket
@ConfigurationProperties(RocketConfiguration.PREFIX)
public class RocketConfiguration {
    public static final String PREFIX = "rocketmq";

    private ProducerConfiguration producer;

    private ConsumerConfiguration consumer;

    public ProducerConfiguration getProducer() {
        return producer;
    }

    @Inject
    public void setProducer(ProducerConfiguration producer) {
        this.producer = producer;
    }

    public ConsumerConfiguration getConsumer() {
        return consumer;
    }

    @Inject
    public void setConsumer(ConsumerConfiguration consumer) {
        this.consumer = consumer;
    }

    @ConfigurationProperties(ProducerConfiguration.PREFIX)
    public static class ProducerConfiguration {
        public static final String PREFIX = "producer";

        private String namespace;

        private String producerGroup = MixAll.DEFAULT_PRODUCER_GROUP;

        private boolean enableMsgTrace = false;

        private String customizedTraceTopic;

        private String rpcHookClassName;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getProducerGroup() {
            return producerGroup;
        }

        public void setProducerGroup(String producerGroup) {
            this.producerGroup = producerGroup;
        }

        public boolean isEnableMsgTrace() {
            return enableMsgTrace;
        }

        public void setEnableMsgTrace(boolean enableMsgTrace) {
            this.enableMsgTrace = enableMsgTrace;
        }

        public String getCustomizedTraceTopic() {
            return customizedTraceTopic;
        }

        public void setCustomizedTraceTopic(String customizedTraceTopic) {
            this.customizedTraceTopic = customizedTraceTopic;
        }

        public String getRpcHookClassName() {
            return rpcHookClassName;
        }

        public void setRpcHookClassName(String rpcHookClassName) {
            this.rpcHookClassName = rpcHookClassName;
        }
    }

    @ConfigurationProperties(ConsumerConfiguration.PREFIX)
    public static class ConsumerConfiguration {
        public static final String PREFIX = "consumer";

        private String namespace;

        private String consumerGroup;

        private boolean enableMsgTrace;

        private String customizedTraceTopic;

        private String rpcHookClassName;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getConsumerGroup() {
            return consumerGroup;
        }

        public void setConsumerGroup(String consumerGroup) {
            this.consumerGroup = consumerGroup;
        }

        public boolean isEnableMsgTrace() {
            return enableMsgTrace;
        }

        public void setEnableMsgTrace(boolean enableMsgTrace) {
            this.enableMsgTrace = enableMsgTrace;
        }

        public String getCustomizedTraceTopic() {
            return customizedTraceTopic;
        }

        public void setCustomizedTraceTopic(String customizedTraceTopic) {
            this.customizedTraceTopic = customizedTraceTopic;
        }

        public String getRpcHookClassName() {
            return rpcHookClassName;
        }

        public void setRpcHookClassName(String rpcHookClassName) {
            this.rpcHookClassName = rpcHookClassName;
        }
    }
}
