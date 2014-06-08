package com.varicom.shadow.aop.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:aopSpringContext.xml"})
public class AopSpringContext {

}
