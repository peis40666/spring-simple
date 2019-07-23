package com.wp.spring.framework.webmvc;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author Peis
 */
@Data
public class HandlerMapping {
    private Object controller;

    private Method  method;

    private Pattern pattern;

    public HandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }


}
