package com.wp.spring.framework.aop.intercept;

import com.wp.spring.framework.aop.aspect.JoinPoint;


import java.util.List;
import java.lang.reflect.Method;
/**
 * @author Peis
 */
public class MethodInvocation   implements JoinPoint {

    private Object proxy;
    private Method method;
    private Object target;
    private Class<?> targetClass;
    private Object[] arguments;
    private List<Object> interceptorsAndDynamicMethodMatchers;

    private int currentInterceptorIndex  = -1;

    public MethodInvocation(Object proxy, Method method, Object target, Class<?> targetClass, Object[] arguments, List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.method = method;
        this.target = target;
        this.targetClass = targetClass;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public Object proceed() throws Throwable{
        if(this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size()-1){
            return this.method.invoke(this.target,this.arguments);
        }
        Object interceptorOrInterceptionAdvice =
            this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        if (interceptorOrInterceptionAdvice instanceof MethodInterceptor) {
            MethodInterceptor mi = (MethodInterceptor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        }else {
            return proceed();
        }
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object[] getArguments() {
        return new Object[0];
    }

    @Override
    public Object getThis() {
        return null;
    }
}
