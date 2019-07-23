package com.wp.spring.framework.aop;

/**
 * @author Peis
 */
public interface AopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);

}
