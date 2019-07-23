package com.wp.spring.framework.beans.support;

import com.wp.spring.framework.beans.config.BeanDefinition;
import com.wp.spring.framework.context.support.AbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Peis
 */
public class DefaultListableBeanFactory extends AbstractApplicationContext {

    //伪IOC容器
    protected final Map<String , BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String , BeanDefinition>();
}
