package com.github.freewu32.nacos.client;

import io.micronaut.http.annotation.*;

import javax.annotation.Nullable;

public interface NacosOperations {
    String INSTANCE_URL = "/v1/ns/instance";

    String SERVICE_URL = "/v1/ns/service";

    String METRICS_URL = "/v1/ns/operator/metrics";

    //实例接口

    /**
     * 注册实例
     */
    @Post(INSTANCE_URL)
    String registerInstance(@QueryValue("ip") String ip,
                            @QueryValue("port") int port,
                            @QueryValue("serviceName") String serviceName,
                            @QueryValue("namespaceId") @Nullable String namespaceId,
                            @QueryValue("weight") @Nullable Double weight,
                            @QueryValue("enabled") @Nullable Boolean enabled,
                            @QueryValue("healthy") @Nullable Boolean healthy,
                            @QueryValue("metadata") @Nullable String metadata,
                            @QueryValue("clusterName") @Nullable String clusterName,
                            @QueryValue("groupName") @Nullable String groupName,
                            @QueryValue("ephemeral") @Nullable Boolean ephemeral);

    /**
     * 删除实例
     */
    @Delete(INSTANCE_URL)
    String unRegisterInstance(@QueryValue("serviceName") String serviceName,
                              @QueryValue("ip") String ip,
                              @QueryValue("port") int port,
                              @QueryValue("namespaceId") @Nullable String namespaceId,
                              @QueryValue("groupName") @Nullable String groupName,
                              @QueryValue("clusterName") @Nullable String clusterName,
                              @QueryValue("ephemeral") @Nullable Boolean ephemeral);

    /**
     * 删除实例
     */
    @Put(INSTANCE_URL)
    String updateInstance(@QueryValue("serviceName") String serviceName,
                          @QueryValue("ip") String ip,
                          @QueryValue("port") int port,
                          @QueryValue("namespaceId") @Nullable String namespaceId,
                          @QueryValue("groupName") @Nullable String groupName,
                          @QueryValue("clusterName") @Nullable String clusterName,
                          @QueryValue("weight") @Nullable Double weight,
                          @QueryValue("metadata") @Nullable String metadata,
                          @QueryValue("enabled") @Nullable Boolean enabled,
                          @QueryValue("ephemeral") @Nullable Boolean ephemeral);

    /**
     * 获取实例列表
     */
    @Get(INSTANCE_URL + "/list")
    GetInstancesResponse getInstances(@QueryValue("serviceName") String serviceName,
                                      @QueryValue("groupName") @Nullable String groupName,
                                      @QueryValue("namespaceId") @Nullable String namespaceId,
                                      @QueryValue("clusters") @Nullable String clusters,
                                      @QueryValue("healthyOnly") @Nullable Boolean healthyOnly);

    /**
     * 发送实例心跳
     */
    @Put(INSTANCE_URL + "/beat")
    String sendInstanceBeat(@QueryValue("serviceName") String serviceName,
                            @QueryValue("beat") String beat,
                            @QueryValue("groupName") @Nullable String groupName,
                            @QueryValue("ephemeral") @Nullable Boolean ephemeral);


    // 服务接口

    /**
     * 创建服务
     */
    @Post(SERVICE_URL)
    String createService(@QueryValue("serviceName") String serviceName,
                         @QueryValue("groupName") @Nullable String groupName,
                         @QueryValue("namespaceId") @Nullable String namespaceId,
                         @QueryValue("protectThreshold") @Nullable Float protectThreshold,
                         @QueryValue("metadata") @Nullable String metadata,
                         @QueryValue("selector") @Nullable String selector);

    /**
     * 删除服务
     */
    @Delete(SERVICE_URL)
    String deleteService(@QueryValue("serviceName") String serviceName,
                         @QueryValue("groupName") @Nullable String groupName,
                         @QueryValue("namespaceId") @Nullable String namespaceId);

    /**
     * 更新服务
     */
    @Put(SERVICE_URL)
    String updateService(@QueryValue("serviceName") String serviceName,
                         @QueryValue("groupName") @Nullable String groupName,
                         @QueryValue("namespaceId") @Nullable String namespaceId,
                         @QueryValue("protectThreshold") @Nullable Float protectThreshold,
                         @QueryValue("metadata") @Nullable String metadata,
                         @QueryValue("selector") @Nullable String selector);

    /**
     * 查询服务信息
     */
    @Get(SERVICE_URL)
    GetServiceResponse getService(@QueryValue("serviceName") String serviceName,
                                  @QueryValue("groupName") @Nullable String groupName,
                                  @QueryValue("namespaceId") @Nullable String namespaceId);

    /**
     * 查询服务列表
     */
    @Get(SERVICE_URL + "list")
    GetServicesResponse getServices(@QueryValue("pageNo") Integer pageNo,
                                    @QueryValue("pageSize") Integer pageSize,
                                    @QueryValue("groupName") @Nullable String groupName,
                                    @QueryValue("namespaceId") @Nullable String namespaceId);

    //配置接口



    //健康检查

    /**
     * 查看系统当前数据指标
     */
    @Get(METRICS_URL)
    GetMetricsResponse getMetrics();
}
