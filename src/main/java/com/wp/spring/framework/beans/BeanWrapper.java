package com.wp.spring.framework.beans;

/**
 * @author Peis
 */
public class BeanWrapper {
    private  Object wrappedInstance;

    private Class<?> wrappedClass;

    public BeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
        this.wrappedClass = this.wrappedInstance.getClass();
    }

    //如果是单例
    public Object getWrappedInstance(){
        return  this.wrappedInstance;
    }

    //非单例
   public Class<?> getWrappedClass(){
        return  this.wrappedClass;
    }
}
