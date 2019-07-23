package com.wp.spring.framework.webmvc;

import lombok.Data;

import java.util.Map;

/**
 * @author Peis
 */
@Data
public class ModelAndView {
    private String viewName;

    private Map<String,?> model;

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public ModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
