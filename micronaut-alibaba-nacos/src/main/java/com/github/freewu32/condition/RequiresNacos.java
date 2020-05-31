package com.github.freewu32.condition;

import com.github.freewu32.NacosConfiguration;
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
        @Requires(property = NacosConfiguration.PREFIX),
        @Requires(property = NacosConfiguration.PREFIX + ".enabled", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
})
public @interface RequiresNacos {
}
