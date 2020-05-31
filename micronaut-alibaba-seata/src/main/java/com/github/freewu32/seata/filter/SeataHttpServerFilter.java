package com.github.freewu32.seata.filter;

import com.github.freewu32.seata.condition.RequiresSeata;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.seata.core.context.RootContext;
import org.reactivestreams.Publisher;

/**
 * 注入header中的xid到线程上下文中
 */
@RequiresSeata
@Filter("/**")
public class SeataHttpServerFilter implements HttpServerFilter {
    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request,
                                                      ServerFilterChain chain) {
        String xid = request.getHeaders().get(RootContext.KEY_XID);
        try {
            if (xid != null) {
                RootContext.bind(xid);
            }
            return chain.proceed(request);
        } finally {
            if (xid != null) {
                String unbindXid = RootContext.unbind();
                // 调用过程有新的事务上下文开启，则不能清除
                if (!xid.equalsIgnoreCase(unbindXid) && unbindXid != null) {
                    RootContext.bind(unbindXid);
                }
            }
        }
    }
}
