package com.github.freewu32.rocketmq.annotation;

import com.github.freewu32.rocketmq.producer.RocketClientIntroductionAdvice;
import io.micronaut.aop.Introduction;
import io.micronaut.context.annotation.Type;
import io.micronaut.retry.annotation.Recoverable;

import javax.inject.Scope;
import javax.inject.Singleton;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Scope
@Introduction
@Type(RocketClientIntroductionAdvice.class)
@Recoverable
@Singleton
public @interface RocketClient {
}
