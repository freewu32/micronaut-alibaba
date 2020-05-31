package com.github.freewu32.seata.Interceptor;

import com.github.freewu32.seata.annotation.GlobalTransactional;
import com.github.freewu32.seata.condition.RequiresSeata;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.seata.core.exception.TransactionException;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import io.seata.tm.api.transaction.TransactionInfo;

import javax.inject.Singleton;
import java.util.UUID;

/**
 * 全局事务拦截器
 */
@RequiresSeata
@Singleton
public class GlobalTransactionalInterceptor implements MethodInterceptor<Object, Object> {

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        GlobalTransaction transaction = GlobalTransactionContext.getCurrentOrCreate();
        try {
            int timeout = context.intValue(GlobalTransactional.class,
                    "timeoutMills").orElse(TransactionInfo.DEFAULT_TIME_OUT);
            String name = context.stringValue(GlobalTransactional.class,
                    "name").orElse(UUID.randomUUID().toString());
            transaction.begin(timeout, name);
            return context.proceed();
        } catch (Throwable e) {
            try {
                transaction.rollback();
                throw new RuntimeException(e);
            } catch (TransactionException ex) {
                throw new RuntimeException(e);
            }
        } finally {
            try {
                transaction.commit();
            } catch (TransactionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
