package org.zcj.rpc.server.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.zcj.rpc.annotation.annotation.RpcService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/19 18 18
 * Description:
 */
public class ServiceBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar{

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        //是否使用默认的filter，使用默认的filter意味着只扫描那些类上拥有Component、Service、Repository或Controller注解的类。
        boolean useDefaultFilters = false;
        ClassPathScanningCandidateComponentProvider beanScanner = new ClassPathScanningCandidateComponentProvider(useDefaultFilters);
        TypeFilter includeFilter = new AnnotationTypeFilter(RpcService.class);
        beanScanner.addIncludeFilter(includeFilter);

        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        for (String basePackage : getBasePackages(annotationMetadata)) {
            beanDefinitions.addAll(beanScanner.findCandidateComponents(basePackage));
        }

        for (BeanDefinition beanDefinition : beanDefinitions) {
            // beanName通常由对应的BeanNameGenerator来生成，比如Spring自带的AnnotationBeanNameGenerator、DefaultBeanNameGenerator等，也可以自己实现。
            String beanName = beanDefinition.getBeanClassName();
            beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    private Set<String> getBasePackages(AnnotationMetadata annotationMetadata) {
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableRpcServer.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        // 指定包名
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        // 如果没有指定包名，则扫描注解所在类的包名
        if (basePackages.size() == 0) {
            basePackages.add(ClassUtils.getPackageName(annotationMetadata.getClassName()));
        }

        return basePackages;
    }
}
