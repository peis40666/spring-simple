package com.wp.spring.framework.aop;

import lombok.Data;

/**
 * @author Peis
 */
@Data
public class AopConfig {
    private String ponitCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
