package com.wp.spring.framework.aop.intercept;

/**
 * @author Peis
 */
public interface MethodInterceptor {
    Object invoke(MethodInvocation mi) throws Throwable;
}
