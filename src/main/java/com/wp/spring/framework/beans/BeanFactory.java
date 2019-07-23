package com.wp.spring.framework.beans;

/**
 *单例工程的顶层设计
 * @author Peis
 */
public interface BeanFactory {
    /*
     *根据beanName从IOC容器中获得一个实例Bean
     */
    Object getBean(String beanName);
}
