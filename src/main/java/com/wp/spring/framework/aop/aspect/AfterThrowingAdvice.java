package com.wp.spring.framework.aop.aspect;

import com.wp.spring.framework.aop.intercept.MethodInterceptor;
import com.wp.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author Peis
 */
public class AfterThrowingAdvice extends AbstractAspectJAdvice implements Advice, MethodInterceptor {


    private String throwingName;
    private MethodInvocation mi;

    public AfterThrowingAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public void setThrowingName(String name) {
        this.throwingName = name;
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try{
            return  mi.proceed();
        }catch (Throwable ex){
            invokeAdviceMethod(mi,null,ex.getCause());
            throw ex;
        }
    }
}
