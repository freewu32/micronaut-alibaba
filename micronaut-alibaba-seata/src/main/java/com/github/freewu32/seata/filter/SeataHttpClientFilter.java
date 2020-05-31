package com.github.freewu32.seata.filter;

import com.github.freewu32.seata.condition.RequiresSeata;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;
import io.seata.core.context.RootContext;
import org.reactivestreams.Publisher;

/**
 * 请求拦截器，http header注入xid
 */
@RequiresSeata
@Filter("/**")
public class SeataHttpClientFilter implements HttpClientFilter {
    @Override
    public Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request,
                                                         ClientFilterChain chain) {
        String xid = RootContext.getXID();
        if (xid != null) {
            request.header(RootContext.KEY_XID, xid);
        }
        return chain.proceed(request);
    }
}
