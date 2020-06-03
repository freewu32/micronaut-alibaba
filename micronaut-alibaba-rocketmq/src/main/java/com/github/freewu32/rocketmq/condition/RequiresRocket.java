package com.github.freewu32.rocketmq.condition;

import com.github.freewu32.rocketmq.RocketConfiguration;
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
        @Requires(property = RocketConfiguration.PREFIX),
        @Requires(property = RocketConfiguration.PREFIX + ".enabled", value = StringUtils.TRUE, defaultValue = StringUtils.FALSE)
})
public @interface RequiresRocket {
}
