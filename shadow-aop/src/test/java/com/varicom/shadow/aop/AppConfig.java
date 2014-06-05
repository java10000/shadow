package com.varicom.shadow.aop;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-12-27
 * <p>Version: 1.0
 */
@Configuration
@ComponentScan(basePackages = "com.varicom.shadow", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Controller.class})
})
public class AppConfig {
}