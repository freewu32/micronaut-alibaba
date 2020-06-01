package com.github.freewu32.seata;

import com.github.freewu32.seata.condition.RequiresSeata;
import io.micronaut.context.annotation.ConfigurationProperties;

@RequiresSeata
@ConfigurationProperties(SeataConfiguration.PREFIX)
public class SeataConfiguration {
    public static final String PREFIX = "seata";

    private String resourceGroupId = "DEFAULT";

    public String getResourceGroupId() {
        return resourceGroupId;
    }

    public void setResourceGroupId(String resourceGroupId) {
        this.resourceGroupId = resourceGroupId;
    }
}
