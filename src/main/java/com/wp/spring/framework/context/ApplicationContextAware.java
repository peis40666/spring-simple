package com.wp.spring.framework.context;

/**
 * 通过解耦的方式获取IOC容器的顶层设计
 * 后面讲通过一个监听器iqu扫描所有的类，只要实现了此接口
 * 将自动调用setApplicationContext()方法，没从而将IOC容器注入目标类中
 * @author Peis
 */
public interface ApplicationContextAware {

    void setApplicationContext(ApplicationContext applicationContext);
}
