package com.xiudongxu.xRpc.rpc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author dongxu.xiu
 * @since 2019-03-10 下午1:14
 */
@Component
public class RpcScannerConfigurer implements BeanDefinitionRegistryPostProcessor {

    String basePackage = "com.xiudongxu.xRpc.service";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ClassPathRpcScanner scanner = new ClassPathRpcScanner(registry);
        scanner.setAnnotationClass(null);
        scanner.registerFilters();

        scanner.doScan(StringUtils.tokenizeToStringArray(this.basePackage,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
