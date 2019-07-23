package com.wp.spring.framework.aop.aspect;

import com.wp.spring.framework.aop.intercept.MethodInterceptor;
import com.wp.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author Peis
 */
public class AfterReturningAdvice extends AbstractAspectJAdvice implements Advice, MethodInterceptor {
    private JoinPoint joinPoint;

    public AfterReturningAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public void afterReturning(Object returnValue, Method method, Object[] args,Object target) throws
            Throwable{
        invokeAdviceMethod(joinPoint,returnValue,null);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }
}
