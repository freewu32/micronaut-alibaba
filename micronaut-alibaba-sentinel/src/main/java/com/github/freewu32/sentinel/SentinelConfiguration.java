package com.github.freewu32.sentinel;

import com.github.freewu32.sentinel.condition.RequiresSentinel;
import io.micronaut.context.annotation.ConfigurationProperties;

@RequiresSentinel
@ConfigurationProperties(SentinelConfiguration.PREFIX)
public class SentinelConfiguration {
    public static final String PREFIX = "sentinel";
}
