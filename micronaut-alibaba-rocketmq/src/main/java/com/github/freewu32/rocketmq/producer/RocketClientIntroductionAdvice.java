package com.github.freewu32.rocketmq.producer;

import com.github.freewu32.rocketmq.annotation.RocketBody;
import com.github.freewu32.rocketmq.annotation.RocketProperties;
import com.github.freewu32.rocketmq.annotation.RocketSendTo;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.type.MutableArgumentValue;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.Map;

@Requires(beans = MQProducer.class)
@Singleton
public class RocketClientIntroductionAdvice implements MethodInterceptor<Object, Object> {

    private MQProducer producer;

    private ConversionService conversionService;

    public RocketClientIntroductionAdvice(MQProducer producer,
                                          ConversionService conversionService) {
        this.producer = producer;
        this.conversionService = conversionService;
    }

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        if (!context.hasAnnotation(RocketSendTo.class)) {
            throw new RuntimeException("请增加RocketSendTo到需要调用的方法上");
        }
        Message message = new Message();
        //方法注解处理
        message.setTopic(context.stringValue(RocketSendTo.class, "topic")
                .get());
        message.setTags(context.stringValue(RocketSendTo.class, "tags")
                .orElse(""));
        message.setKeys(context.stringValue(RocketSendTo.class, "keys")
                .orElse(""));
        message.setFlag(context.intValue(RocketSendTo.class, "flag")
                .orElse(0));
        message.setWaitStoreMsgOK(context.booleanValue(RocketSendTo.class, "waitStoreMsgOK")
                .orElse(Boolean.TRUE));
        //方法入参注解处理
        Map<String, MutableArgumentValue<?>> params = context.getParameters();
        for (Map.Entry<String, MutableArgumentValue<?>> entry : params.entrySet()) {
            MutableArgumentValue<?> v = entry.getValue();
            if (v.isAnnotationPresent(RocketBody.class)) {
                if (message.getBody() != null) {
                    throw new RuntimeException("RocketBody重复定义");
                }
                message.setBody((byte[]) conversionService.convert(v.getValue(),
                        byte[].class).orElse(v.getValue().toString().getBytes()));
            }
            if (v.isAnnotationPresent(RocketProperties.class)) {
                String propertiesKey = v.getAnnotation(RocketProperties.class)
                        .stringValue("value").get();
                message.getProperties().put(propertiesKey, (String) conversionService
                        .convert(v.getValue(), String.class)
                        .orElse(v.getValue().toString()));
            }
        }
        try {
            //处理返回值
            Class<?> returnType = context.getReturnType().getType();

            if (returnType.isAssignableFrom(SendResult.class)) {
                return this.producer.send(message);
            } else if (returnType.isAssignableFrom(Publisher.class)) {
                Publisher<?> publisher = s -> {
                    try {
                        this.producer.send(message, new SendCallback() {
                            @Override
                            public void onSuccess(SendResult sendResult) {
                                s.onNext(sendResult);
                                s.onComplete();
                            }

                            @Override
                            public void onException(Throwable e) {
                                s.onError(e);
                            }
                        });
                    } catch (Exception e) {
                        s.onError(e);
                    }
                };
                return conversionService.convert(publisher, returnType).get();
            } else {
                this.producer.send(message);
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
