package com.github.freewu32.seata.condition;

import com.github.freewu32.seata.SeataConfiguration;
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
        @Requires(property = SeataConfiguration.PREFIX),
        @Requires(property = SeataConfiguration.PREFIX + ".enabled", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
})
public @interface RequiresSeata {
}
