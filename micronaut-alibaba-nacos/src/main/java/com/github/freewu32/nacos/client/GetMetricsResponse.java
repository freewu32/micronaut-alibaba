package com.github.freewu32.nacos.client;

import io.micronaut.core.annotation.Introspected;

import java.io.Serializable;

@Introspected
public class GetMetricsResponse implements Serializable {
    private int serviceCount;

    private double load;

    private double mem;

    private int responsibleServiceCount;

    private int instanceCount;

    private double cpu;

    private String status;

    private int responsibleInstanceCount;

    public int getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(int serviceCount) {
        this.serviceCount = serviceCount;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public double getMem() {
        return mem;
    }

    public void setMem(double mem) {
        this.mem = mem;
    }

    public int getResponsibleServiceCount() {
        return responsibleServiceCount;
    }

    public void setResponsibleServiceCount(int responsibleServiceCount) {
        this.responsibleServiceCount = responsibleServiceCount;
    }

    public int getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getResponsibleInstanceCount() {
        return responsibleInstanceCount;
    }

    public void setResponsibleInstanceCount(int responsibleInstanceCount) {
        this.responsibleInstanceCount = responsibleInstanceCount;
    }
}
