package com.wp.spring.framework.aop.aspect;

import com.wp.spring.framework.aop.intercept.MethodInterceptor;
import com.wp.spring.framework.aop.intercept.MethodInvocation;
import java.lang.reflect.Method;
/**
 * @author Peis
 */
public class MethodBeforeAdvice extends AbstractAspectJAdvice implements Advice, MethodInterceptor {
    private JoinPoint joinPoint;

    public MethodBeforeAdvice(Method aspectMethod, Object target){
        super(aspectMethod,target);
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(this.joinPoint,null,null);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.joinPoint = mi;
        this.before(mi.getMethod(),mi.getArguments(),mi.getThis());
        return mi.proceed();
    }


}
