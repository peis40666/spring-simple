package com.wp.spring.framework.beans.config;

import lombok.Data;

/**
 * @author Peis
 */

@Data
public class BeanDefinition {
    private String beanClassName;

    private boolean layInit = false;

    private String factoryBeanName;

    private boolean isSingleton = true;
}
