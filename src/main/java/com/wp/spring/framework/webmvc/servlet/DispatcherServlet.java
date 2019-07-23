package com.wp.spring.framework.webmvc.servlet;

import com.wp.spring.framework.annotation.Controller;
import com.wp.spring.framework.annotation.RequestMapping;
import com.wp.spring.framework.context.ApplicationContext;
import com.wp.spring.framework.webmvc.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import ayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peis
 */
@Slf4j
public class DispatcherServlet extends HttpServlet {

    private final String LOCATION ="contextConfigLocation";

    private List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();

    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<HandlerMapping, HandlerAdapter>();

    private List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();

    private ApplicationContext context;



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatcher(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HandlerMapping handler = getHandler(req);
        if(handler == null){
            processDispatchResult(req,resp,new ModelAndView("404"));
            return;
        }

        //2、准备调用前的参数
        HandlerAdapter ha = getHandlerAdapter(handler);

        //3、真正的调用方法,返回ModelAndView存储了要穿页面上值，和页面模板的名称
        ModelAndView mv = ha.handle(req,resp,handler);

        processDispatchResult(req, resp, mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ModelAndView mv) throws Exception {
        //把给我的ModleAndView变成一个HTML、OuputStream、json、freemark、veolcity
        //ContextType
        if(null == mv){return;}

        //如果ModelAndView不为null，怎么办？
        if(this.viewResolvers.isEmpty()){return;}

        for (ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveViewName(mv.getViewName(),null);
            view.render(mv.getModel(),req,resp);
            return;
        }
    }


    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){return null;}
        HandlerAdapter ha = this.handlerAdapters.get(handler);
        if(ha.supports(ha)){
            return ha;
        }
        return null;
    }

    private HandlerMapping getHandler(HttpServletRequest req) throws Exception{
        if(this.handlerMappings.isEmpty()){ return null; }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (HandlerMapping handler : this.handlerMappings) {
            try{
                Matcher matcher = handler.getPattern().matcher(url);
                //如果没有匹配上继续下一个匹配
                if(!matcher.matches()){ continue; }

                return handler;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化IOC容器
        context  = new ApplicationContext(new String[]{config.getInitParameter(LOCATION)});
        initStrategies(context);
    }

    protected void initStrategies(ApplicationContext context) {
        //spring mvc九大组件
        initMultipartResolver(context);//文件上传解析，如果请求类型是 multipart 将通过MultipartResolver 进行文件上传解析
        initLocaleResolver(context);//本地化解析

        /**自己会实现 */
        //HandlerMapping 用来保存 Controller 中配置的 RequestMapping 和 Method 的一个对应关系
        initHandlerMappings(context);//通过 HandlerMapping，将请求映射到处理器

        /** 自己会实现 */
        //HandlerAdapters 用来动态匹配 Method 参数，包括类转换，动态赋值
        initHandlerAdapters(context);//通过 HandlerAdapter 进行多类型的参数动态匹配

        initHandlerExceptionResolvers(context);//如果执行过程中遇到异常，将交给HandlerExceptionResolver 来解析

        initRequestToViewNameTranslator(context);//直接解析请求到视图名
        /** 自己会实现 */
        //通过 ViewResolvers 实现动态模板的解析
        //自己解析一套模板语言
        initViewResolvers(context);//通过 viewResolver 解析逻辑视图到具体视图实现

        initFlashMapManager(context);//flash 映射管理器
    }

    private void initFlashMapManager(ApplicationContext context) {
    }
    private void initRequestToViewNameTranslator(ApplicationContext context) {
    }
    private void initHandlerExceptionResolvers(ApplicationContext context) {
    }
    private void initLocaleResolver(ApplicationContext context) {}
    private void initMultipartResolver(ApplicationContext context) {
    }

    /*
        自己实现的部分
     */
    private void initViewResolvers(ApplicationContext context) {
        //解决页面名字和模板文件关联的问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath =
                this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File template : templateRootDir.listFiles()) {
            this.viewResolvers.add(new ViewResolver(templateRoot));
        }

    }

    private void initHandlerAdapters(ApplicationContext context) {
        //在初始化阶段，我们能做的就是，将这些参数的名字或者类型按一定的顺序保存下来
        //因为后面用反射调用的时候，传的形参是一个数组
        //可以通过记录这些参数的位置 index,挨个从数组中填值，这样的话，就和参数的顺序无关了
        for (HandlerMapping handlerMapping : this.handlerMappings) {
            //每一个方法有一个参数列表，那么这里保存的是形参列表
            this.handlerAdapters.put(handlerMapping,new HandlerAdapter());
        }

    }

    private void initHandlerMappings(ApplicationContext context) {
        //从容器中取到所有的实例
        String[] beanNames = context.getBeanDefinitionNames();
        try{
            for(String beanName :beanNames){
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if(!clazz.isAnnotationPresent(Controller.class)){
                    continue;
                }
                String baseUrl = "";

                if(clazz.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                //扫描所有的public方法
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if(!method.isAnnotationPresent(RequestMapping.class)){
                        continue;
                    }
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    String regex = ("/" + baseUrl + requestMapping.value().replaceAll("\\*",
                            ".*")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new HandlerMapping(controller,method,pattern));
                    log.info("Mapping: " + regex + " , " + method);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
