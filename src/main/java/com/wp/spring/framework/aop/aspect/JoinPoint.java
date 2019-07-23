package com.wp.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author Peis
 */
public interface JoinPoint {
    Method getMethod();

    Object[] getArguments();

    Object getThis();
}
