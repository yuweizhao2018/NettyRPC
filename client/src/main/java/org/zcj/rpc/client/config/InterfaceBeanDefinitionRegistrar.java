package org.zcj.rpc.client.config;

import io.netty.channel.Channel;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.zcj.rpc.annotation.annotation.RpcProxy;
import org.zcj.rpc.client.netty.ChannelPool;
import org.zcj.rpc.client.netty.NettyClient;
import org.zcj.rpc.common.config.ZookeeperProperties;
import org.zcj.rpc.common.utils.ZKUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/13 18 25
 * Description:
 */
@Configuration
public class InterfaceBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        ClassPathScanningCandidateComponentProvider scan = getScanner();

        // 只扫描带RpcProxy注解的接口
        scan.addIncludeFilter(new AnnotationTypeFilter(RpcProxy.class));

        Set<BeanDefinition> candidateComponents = new HashSet<>();
        for (String basePackage : getBasePackages(annotationMetadata)) {
            candidateComponents.addAll(scan.findCandidateComponents(basePackage));
        }

        for (BeanDefinition candidateComponent : candidateComponents) {
            if (!beanDefinitionRegistry.containsBeanDefinition(candidateComponent.getBeanClassName())) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata metadata = annotatedBeanDefinition.getMetadata();
                    Assert.isTrue(metadata.isInterface(), "can only be specified on an interface");
                    cacheService(annotatedBeanDefinition);
                    Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(RpcProxy.class.getCanonicalName());

                    registerNettyRpcClient(beanDefinitionRegistry, metadata, annotationAttributes);
                }
            }
        }

    }

    private void cacheService(AnnotatedBeanDefinition annotatedBeanDefinition) {
        String interfaceName = annotatedBeanDefinition.getBeanClassName();
        AnnotationMetadata metadata = annotatedBeanDefinition.getMetadata();
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(RpcProxy.class.getCanonicalName());
        String version = (String) annotationAttributes.get("version");
        ChannelPool.addService(interfaceName + version);
    }

    private void registerNettyRpcClient(BeanDefinitionRegistry beanDefinitionRegistry, AnnotationMetadata metadata, Map<String, Object> annotationAttributes) {
        String className = metadata.getClassName();

        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(NettyClientFactoryBean.class);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        definition.addPropertyValue("type", className);
        String name = annotationAttributes.get("name") == null ? "" : (String) (annotationAttributes.get("name"));
        // 别名
        String alias = name + "NettyRpcClient";
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setPrimary(true);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                new String[] { alias });
        // 注册BeanDefinition
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, beanDefinitionRegistry);

    }

    private Set<String> getBasePackages(AnnotationMetadata annotationMetadata) {
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableRpcClient.class.getCanonicalName());

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

    public ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                // 判断候选人的条件:必须是独立的，然后是接口
                if (beanDefinition.getMetadata().isIndependent() && beanDefinition.getMetadata().isInterface()) {
                    return true;
                }
                return false;
            }
        };
    }
}
