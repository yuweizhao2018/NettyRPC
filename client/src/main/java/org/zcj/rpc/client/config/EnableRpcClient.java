package org.zcj.rpc.client.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(value= InterfaceBeanDefinitionRegistrar.class)
public @interface EnableRpcClient {

    String[] basePackages() default {};

}