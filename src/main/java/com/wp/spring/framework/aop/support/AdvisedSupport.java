package com.wp.spring.framework.aop.support;

import com.wp.spring.framework.aop.aspect.AfterReturningAdvice;
import com.wp.spring.framework.aop.aspect.AfterThrowingAdvice;
import com.wp.spring.framework.aop.aspect.MethodBeforeAdvice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peis
 */
public class AdvisedSupport {
    private Class  targetClass;

    private Object target;

    private Pattern pointCutClassPattern;

    private transient Map<Method, List<Object>> methodCache;

    private AopConfig config;

    public AdvisedSupport(AopConfig config){
        this.config = config;
    }

    public void setTargetClass(Class targetClass){
        this.targetClass = targetClass;
    }

    public Class getTargetClass(){
        return this.targetClass;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?>
            targetClass) throws Exception{

        List<Object> cached = methodCache.get(method);
        // 缓存未命中，则进行下一步处理
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
            cached = methodCache.get(m);
            // 存入缓存
            this.methodCache.put(m, cached);
        }
        return cached;
    }

    public boolean pointCutMatch(){
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    public void parse(){
        //pointCut 表达式
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        String pointCutForClass = pointCut.substring(0,pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " +
                pointCutForClass.substring(pointCutForClass.lastIndexOf(" ")+1));

        methodCache = new HashMap<Method, List<Object>>();
        Pattern pattern = Pattern.compile(pointCut);

        Class aspectClass = null;
        try {
            aspectClass = Class.forName(config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String,Method>();
            for (Method m : aspectClass.getMethods()){
                aspectMethods.put(m.getName(),m);
            }
            //在这里得到的方法都是原生的方法
            for(Method m: targetClass.getMethods()){
                String methodString = m.toString();
                if(methodString.contains("throws")){
                    methodString = methodString.substring(0,methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                if(matcher.matches()){
                    //能满足切面规 则的类，添加的 AOP 配置中
                    List<Object> advices = new LinkedList<Object>();
                    //前置通知
                    if(!(null == config.getAspectBefore() ||
                            "".equals(config.getAspectBefore().trim()))){
                        advices.add(new
                                MethodBeforeAdvice(aspectMethods.get(config.getAspectBefore()), aspectClass.newInstance()));
                    }
                    //后置通知
                    if(!(null == config.getAspectAfter() ||
                            "".equals(config.getAspectAfter().trim()))) {
                        advices.add(new
                                AfterReturningAdvice(aspectMethods.get(config.getAspectAfter()), aspectClass.newInstance()));
                    }

                    //异常通知
                    if(!(null == config.getAspectAfterThrow() ||
                            "".equals(config.getAspectAfterThrow().trim()))) {
                        AfterThrowingAdvice afterThrowingAdvice = new
                                AfterThrowingAdvice(aspectMethods.get(config.getAspectAfterThrow()),
                                aspectClass.newInstance());
                        afterThrowingAdvice.setThrowingName(config.getAspectAfterThrowingName());
                        advices.add(afterThrowingAdvice);
                }
                 methodCache.put(m,advices);
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
