package com.wp.spring.framework.context;
import com.wp.spring.framework.annotation.Autowired;
import com.wp.spring.framework.annotation.Controller;
import com.wp.spring.framework.annotation.Service;
import com.wp.spring.framework.aop.support.AdvisedSupport;
import com.wp.spring.framework.beans.BeanFactory;
import com.wp.spring.framework.beans.BeanWrapper;
import com.wp.spring.framework.beans.config.BeanDefinition;
import com.wp.spring.framework.beans.config.BeanPostProcessor;
import com.wp.spring.framework.beans.support.BeanDefinitionReader;
import com.wp.spring.framework.beans.support.DefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *@author Peis
 */
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    private String[] configLocations;

    private BeanDefinitionReader reader;
    //单例的IOC容器缓存
    private Map<String,Object> singletonBeanCacheMap = new ConcurrentHashMap<String,Object>();

    //通用的IOC容器
    private Map<String,BeanWrapper> beanWrapperMap =new ConcurrentHashMap<String,BeanWrapper>();

    public  ApplicationContext(String[] configLocations){
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        //1、定位，定位配置文件
        reader = new BeanDefinitionReader(this.configLocations);
        //2、加载配置文件，扫描相关的类，把他们封装成BeanDefinition,
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //3、注册  把配置信息放到一个容器里面 （伪IOC容器，真正的容器是BeanWrapper）
        doRegisterBeanDefinition(beanDefinitions);
        //4、把不是延时加载的类，提前初始化
        doAutowrited();
    }

    //只处理非延时加载的情况
    private void doAutowrited() {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLayInit()){ //非延时加载
                getBean(beanName);
            }
        }
        ;
    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) {
        for(BeanDefinition beanDefinition:beanDefinitions){
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
    }

    @Override
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        try{
            //生成通知事件
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();
            Object instance = instantiateBean(beanDefinition);
            if(null==instance){
                return  null;
            }
            //在实例初始化之前调用一次
            beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            BeanWrapper beanWrapper = new BeanWrapper(instance);
            this.beanWrapperMap.put(beanName,beanWrapper);
            //在实例初始化之后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            populateBean(beanName,instance);
            return  this.beanWrapperMap.get(beanName).getWrappedInstance();
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    private Object instantiateBean(BeanDefinition beanDefinition) {
        //1、拿到要实例化的对象的类名
        String className = beanDefinition.getBeanClassName();
        //2、反射实例化
        Object instance = null;
        try {
           if(this.singletonBeanCacheMap.containsKey(className)){
               instance = this.singletonBeanCacheMap.get(className);
           }else{
               Class<?> clazz = Class.forName(className);
               instance = clazz.newInstance();


               this.singletonBeanCacheMap.put(beanDefinition.getFactoryBeanName(),instance);
           }

        }catch (Exception e){
            e.printStackTrace();
        }



        return instance;
    }

    private void populateBean(String beanName, Object instance) {
        Class clazz = instance.getClass();
        if(!(clazz.isAnnotationPresent(Controller.class)||clazz.isAnnotationPresent(Service.class))){
            return ;
        }
        Field[] fields = clazz.getDeclaredFields();
        for(Field field:fields){
            if(!field.isAnnotationPresent(Autowired.class)){
                continue;
            }
            Autowired autowired = field.getAnnotation(Autowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                boolean flag = this.beanWrapperMap.get(autowiredBeanName)==null;
                if(this.beanWrapperMap.containsKey(autowiredBeanName)) {
                    field.set(instance, this.beanWrapperMap.get(autowiredBeanName).getWrappedInstance());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeandDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }

}
