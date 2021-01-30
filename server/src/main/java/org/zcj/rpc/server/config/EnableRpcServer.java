package org.zcj.rpc.server.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(value= ServiceBeanDefinitionRegistrar.class)
public @interface EnableRpcServer {

    String[] basePackages() default {};

}