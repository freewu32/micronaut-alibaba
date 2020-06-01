package com.github.freewu32.sentinel.condition;

import com.github.freewu32.sentinel.SentinelConfiguration;
import io.micronaut.context.annotation.Requirements;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;

import java.lang.annotation.*;

/**
 * nacos是否启用条件判断
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Requirements({
        @Requires(property = SentinelConfiguration.PREFIX),
        @Requires(property = SentinelConfiguration.PREFIX + ".enabled", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
})
public @interface RequiresSentinel {
}
