package com.wp.spring.framework.beans.config;

/**
 * @author Peis
 */
public class BeanPostProcessor {

    //为在 Bean 的初始化前提供回调入口
    public Object postProcessBeforeInitialization(Object bean,String beanName)throws Exception{
        return  bean;
    }

    //为在 Bean 的初始化之后提供回调入口
    public Object postProcessAfterInitialization(Object bean,String beanName) throws  Exception{
        return  bean;
    }
}
