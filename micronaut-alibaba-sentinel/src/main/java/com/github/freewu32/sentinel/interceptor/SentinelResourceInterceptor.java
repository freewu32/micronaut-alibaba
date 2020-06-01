package com.github.freewu32.sentinel.interceptor;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.util.MethodUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.github.freewu32.sentinel.annotation.SentinelResource;
import com.github.freewu32.sentinel.condition.RequiresSentinel;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@RequiresSentinel
@Singleton
public class SentinelResourceInterceptor implements MethodInterceptor<Object, Object> {
    
    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        Method originMethod = resolveMethod(context);

        SentinelResource annotation = originMethod.getAnnotation(SentinelResource.class);
        if (annotation == null) {
            // Should not go through here.
            throw new IllegalStateException("Wrong state for SentinelResource annotation");
        }
        String resourceName = getResourceName(annotation.value(), originMethod);
        EntryType entryType = annotation.entryType();
        int resourceType = annotation.resourceType();
        Entry entry = null;
        try {
            entry = SphU.entry(resourceName, resourceType, entryType, context.getParameterValues());
            Object result = context.proceed();
            return result;
        } catch (BlockException ex) {
            return handleBlockException(context, annotation, ex);
        } catch (Throwable ex) {
            Class<? extends Throwable>[] exceptionsToIgnore = annotation.exceptionsToIgnore();
            // The ignore list will be checked first.
            if (exceptionsToIgnore.length > 0 && exceptionBelongsTo(ex, exceptionsToIgnore)) {
                throw ex;
            }
            if (exceptionBelongsTo(ex, annotation.exceptionsToTrace())) {
                traceException(ex);
                return handleFallback(context, annotation, ex);
            }

            // No fallback function can handle the exception, so throw it out.
            throw ex;
        } finally {
            if (entry != null) {
                entry.exit(1, context.getParameterValues());
            }
        }
    }

    protected void traceException(Throwable ex) {
        Tracer.trace(ex);
    }

    protected void traceException(Throwable ex, SentinelResource annotation) {
        Class<? extends Throwable>[] exceptionsToIgnore = annotation.exceptionsToIgnore();
        // The ignore list will be checked first.
        if (exceptionsToIgnore.length > 0 && exceptionBelongsTo(ex, exceptionsToIgnore)) {
            return;
        }
        if (exceptionBelongsTo(ex, annotation.exceptionsToTrace())) {
            traceException(ex);
        }
    }

    /**
     * Check whether the exception is in provided list of exception classes.
     *
     * @param ex         provided throwable
     * @param exceptions list of exceptions
     * @return true if it is in the list, otherwise false
     */
    protected boolean exceptionBelongsTo(Throwable ex, Class<? extends Throwable>[] exceptions) {
        if (exceptions == null) {
            return false;
        }
        for (Class<? extends Throwable> exceptionClass : exceptions) {
            if (exceptionClass.isAssignableFrom(ex.getClass())) {
                return true;
            }
        }
        return false;
    }

    protected String getResourceName(String resourceName, /*@NonNull*/ Method method) {
        // If resource name is present in annotation, use this value.
        if (StringUtil.isNotBlank(resourceName)) {
            return resourceName;
        }
        // Parse name of target method.
        return MethodUtil.resolveMethodName(method);
    }

    protected Object handleFallback(MethodInvocationContext pjp, SentinelResource annotation, Throwable ex) {
        return handleFallback(pjp, annotation.fallback(), annotation.defaultFallback(), annotation.fallbackClass(), ex);
    }

    protected Object handleFallback(MethodInvocationContext pjp, String fallback, String defaultFallback,
                                    Class<?>[] fallbackClass, Throwable ex) {
        Object[] originArgs = pjp.getParameterValues();

        // Execute fallback function if configured.
        Method fallbackMethod = extractFallbackMethod(pjp, fallback, fallbackClass);
        if (fallbackMethod != null) {
            // Construct args.
            int paramCount = fallbackMethod.getParameterTypes().length;
            Object[] args;
            if (paramCount == originArgs.length) {
                args = originArgs;
            } else {
                args = Arrays.copyOf(originArgs, originArgs.length + 1);
                args[args.length - 1] = ex;
            }

            try {
                if (isStatic(fallbackMethod)) {
                    return fallbackMethod.invoke(null, args);
                }
                return fallbackMethod.invoke(pjp.getTarget(), args);
            } catch (Exception e) {
                // throw the actual exception
                throw new RuntimeException(e);
            }
        }
        // If fallback is absent, we'll try the defaultFallback if provided.
        return handleDefaultFallback(pjp, defaultFallback, fallbackClass, ex);
    }

    protected Object handleDefaultFallback(MethodInvocationContext pjp, String defaultFallback,
                                           Class<?>[] fallbackClass, Throwable ex) {
        // Execute the default fallback function if configured.
        Method fallbackMethod = extractDefaultFallbackMethod(pjp, defaultFallback, fallbackClass);
        if (fallbackMethod != null) {
            // Construct args.
            Object[] args = fallbackMethod.getParameterTypes().length == 0 ? new Object[0] : new Object[] {ex};
            try {
                if (isStatic(fallbackMethod)) {
                    return fallbackMethod.invoke(null, args);
                }
                return fallbackMethod.invoke(pjp.getTarget(), args);
            } catch (Exception e) {
                // throw the actual exception
                throw new RuntimeException(e);
            }
        }

        // If no any fallback is present, then directly throw the exception.
        throw new RuntimeException(ex);
    }

    protected Object handleBlockException(MethodInvocationContext pjp, SentinelResource annotation, BlockException ex) {

        // Execute block handler if configured.
        Method blockHandlerMethod = extractBlockHandlerMethod(pjp, annotation.blockHandler(),
                annotation.blockHandlerClass());
        if (blockHandlerMethod != null) {
            Object[] originArgs = pjp.getParameterValues();
            // Construct args.
            Object[] args = Arrays.copyOf(originArgs, originArgs.length + 1);
            args[args.length - 1] = ex;
            try {
                if (isStatic(blockHandlerMethod)) {
                    return blockHandlerMethod.invoke(null, args);
                }
                return blockHandlerMethod.invoke(pjp.getTarget(), args);
            } catch (Exception e) {
                // throw the actual exception
                throw new RuntimeException(e);
            }
        }

        // If no block handler is present, then go to fallback.
        return handleFallback(pjp, annotation, ex);
    }

    private Method extractFallbackMethod(MethodInvocationContext pjp, String fallbackName, Class<?>[] locationClass) {
        if (StringUtil.isBlank(fallbackName)) {
            return null;
        }
        boolean mustStatic = locationClass != null && locationClass.length >= 1;
        Class<?> clazz = mustStatic ? locationClass[0] : pjp.getTarget().getClass();
        MethodWrapper m = ResourceMetadataRegistry.lookupFallback(clazz, fallbackName);
        if (m == null) {
            // First time, resolve the fallback.
            Method method = resolveFallbackInternal(pjp, fallbackName, clazz, mustStatic);
            // Cache the method instance.
            ResourceMetadataRegistry.updateFallbackFor(clazz, fallbackName, method);
            return method;
        }
        if (!m.isPresent()) {
            return null;
        }
        return m.getMethod();
    }

    private Method extractDefaultFallbackMethod(MethodInvocationContext pjp, String defaultFallback,
                                                Class<?>[] locationClass) {
        if (StringUtil.isBlank(defaultFallback)) {
            SentinelResource annotationClass = pjp.getTarget().getClass().getAnnotation(SentinelResource.class);
            if (annotationClass != null && StringUtil.isNotBlank(annotationClass.defaultFallback())) {
                defaultFallback = annotationClass.defaultFallback();
                if (locationClass == null || locationClass.length < 1) {
                    locationClass = annotationClass.fallbackClass();
                }
            } else {
                return null;
            }
        }
        boolean mustStatic = locationClass != null && locationClass.length >= 1;
        Class<?> clazz = mustStatic ? locationClass[0] : pjp.getTarget().getClass();

        MethodWrapper m = ResourceMetadataRegistry.lookupDefaultFallback(clazz, defaultFallback);
        if (m == null) {
            // First time, resolve the default fallback.
            Class<?> originReturnType = resolveMethod(pjp).getReturnType();
            // Default fallback allows two kinds of parameter list.
            // One is empty parameter list.
            Class<?>[] defaultParamTypes = new Class<?>[0];
            // The other is a single parameter {@link Throwable} to get relevant exception info.
            Class<?>[] paramTypeWithException = new Class<?>[] {Throwable.class};
            // We first find the default fallback with empty parameter list.
            Method method = findMethod(mustStatic, clazz, defaultFallback, originReturnType, defaultParamTypes);
            // If default fallback with empty params is absent, we then try to find the other one.
            if (method == null) {
                method = findMethod(mustStatic, clazz, defaultFallback, originReturnType, paramTypeWithException);
            }
            // Cache the method instance.
            ResourceMetadataRegistry.updateDefaultFallbackFor(clazz, defaultFallback, method);
            return method;
        }
        if (!m.isPresent()) {
            return null;
        }
        return m.getMethod();
    }

    private Method resolveFallbackInternal(MethodInvocationContext pjp, /*@NonNull*/ String name, Class<?> clazz,
                                           boolean mustStatic) {
        Method originMethod = resolveMethod(pjp);
        // Fallback function allows two kinds of parameter list.
        Class<?>[] defaultParamTypes = originMethod.getParameterTypes();
        Class<?>[] paramTypesWithException = Arrays.copyOf(defaultParamTypes, defaultParamTypes.length + 1);
        paramTypesWithException[paramTypesWithException.length - 1] = Throwable.class;
        // We first find the fallback matching the signature of origin method.
        Method method = findMethod(mustStatic, clazz, name, originMethod.getReturnType(), defaultParamTypes);
        // If fallback matching the origin method is absent, we then try to find the other one.
        if (method == null) {
            method = findMethod(mustStatic, clazz, name, originMethod.getReturnType(), paramTypesWithException);
        }
        return method;
    }

    private Method extractBlockHandlerMethod(MethodInvocationContext pjp, String name, Class<?>[] locationClass) {
        if (StringUtil.isBlank(name)) {
            return null;
        }

        boolean mustStatic = locationClass != null && locationClass.length >= 1;
        Class<?> clazz;
        if (mustStatic) {
            clazz = locationClass[0];
        } else {
            // By default current class.
            clazz = pjp.getTarget().getClass();
        }
        MethodWrapper m = ResourceMetadataRegistry.lookupBlockHandler(clazz, name);
        if (m == null) {
            // First time, resolve the block handler.
            Method method = resolveBlockHandlerInternal(pjp, name, clazz, mustStatic);
            // Cache the method instance.
            ResourceMetadataRegistry.updateBlockHandlerFor(clazz, name, method);
            return method;
        }
        if (!m.isPresent()) {
            return null;
        }
        return m.getMethod();
    }

    private Method resolveBlockHandlerInternal(MethodInvocationContext pjp, /*@NonNull*/ String name, Class<?> clazz,
                                               boolean mustStatic) {
        Method originMethod = resolveMethod(pjp);
        Class<?>[] originList = originMethod.getParameterTypes();
        Class<?>[] parameterTypes = Arrays.copyOf(originList, originList.length + 1);
        parameterTypes[parameterTypes.length - 1] = BlockException.class;
        return findMethod(mustStatic, clazz, name, originMethod.getReturnType(), parameterTypes);
    }

    private boolean checkStatic(boolean mustStatic, Method method) {
        return !mustStatic || isStatic(method);
    }

    private Method findMethod(boolean mustStatic, Class<?> clazz, String name, Class<?> returnType,
                              Class<?>... parameterTypes) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (name.equals(method.getName()) && checkStatic(mustStatic, method)
                    && returnType.isAssignableFrom(method.getReturnType())
                    && Arrays.equals(parameterTypes, method.getParameterTypes())) {

                RecordLog.info("Resolved method [{}] in class [{}]", name, clazz.getCanonicalName());
                return method;
            }
        }
        // Current class not found, find in the super classes recursively.
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && !Object.class.equals(superClass)) {
            return findMethod(mustStatic, superClass, name, returnType, parameterTypes);
        } else {
            String methodType = mustStatic ? " static" : "";
            RecordLog.warn("Cannot find{} method [{}] in class [{}] with parameters {}",
                    methodType, name, clazz.getCanonicalName(), Arrays.toString(parameterTypes));
            return null;
        }
    }

    private boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    protected Method resolveMethod(MethodInvocationContext joinPoint) {
        String methodName = joinPoint.getMethodName();

        Class<?> targetClass = joinPoint.getTarget().getClass();

        Method method = getDeclaredMethodFor(targetClass, methodName,
                joinPoint.getArgumentTypes());
        if (method == null) {
            throw new IllegalStateException("Cannot resolve target method: " + methodName);
        }
        return method;
    }

    /**
     * Get declared method with provided name and parameterTypes in given class and its super classes.
     * All parameters should be valid.
     *
     * @param clazz          class where the method is located
     * @param name           method name
     * @param parameterTypes method parameter type list
     * @return resolved method, null if not found
     */
    private Method getDeclaredMethodFor(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethodFor(superClass, name, parameterTypes);
            }
        }
        return null;
    }
}
