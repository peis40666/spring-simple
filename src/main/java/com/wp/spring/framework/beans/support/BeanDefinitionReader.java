package com.wp.spring.framework.beans.support;

import com.wp.spring.framework.beans.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import ayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author Peis
 */
public class BeanDefinitionReader {

    private Properties config = new Properties();

    private final String scanPackage = "scanPackage";

    private List<String> registyBeanClasses = new ArrayList<String>();

    public BeanDefinitionReader(String ...locations) {
        //通过URL定位找到其所对应的文件
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        doScanner(config.getProperty(scanPackage));
    }

    private void doScanner(String scanPackage) {
        //转换为文件路径，实际上就是把.替换为/
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if(file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else{
                if(!file.getName().endsWith(".class")){ continue;}
                String className = (scanPackage + "." + file.getName().replace(".class",""));
                registyBeanClasses.add(className);
            }
        }
    }

    public List<BeanDefinition> loadBeanDefinitions(){
        List<BeanDefinition> result = new ArrayList<BeanDefinition>();
        try {
            for(String className : registyBeanClasses){
                Class<?> beanClass = Class.forName(className);
                if(beanClass.isInterface()){continue;}
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()),beanClass.getName()));
                Class<?>[] interfaces =beanClass.getInterfaces();
                for(Class<?> i :interfaces){
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
    }

    private BeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName){
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(beanClassName);
        return  beanDefinition;
    }

    public Properties getConfig() {
        return config;
    }

    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
