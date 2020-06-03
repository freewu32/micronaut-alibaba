package com.github.freewu32.nacos.client;

import io.micronaut.core.annotation.Introspected;

import java.io.Serializable;
import java.util.List;

@Introspected
public class GetServicesResponse implements Serializable {
    private int count;

    private List<String> doms;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getDoms() {
        return doms;
    }

    public void setDoms(List<String> doms) {
        this.doms = doms;
    }
}
