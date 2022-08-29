package com.demo.customizeAutowire.condition;


import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

public class MyOnPropertyCondition implements Condition {

    /**
     * 匹配出对应的 bean
     * @param context 可以获取 spring 中一些基本的信息
     * @param metadata 可以获取到 注解上面对应的信息
     * @return boolean
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        // 获取到 ioc 容器中的 beanFactory
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        //获取类加载器
        ClassLoader classLoader = context.getClassLoader();
        //获取当前环境信息
        Environment environment = context.getEnvironment();
        //获取bean定义的注册类
        BeanDefinitionRegistry registry = context.getRegistry();

        // 通过 metadata 来获取值
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(MyConditionalOnProperty.class.getName());
        String propertyName = (String) annotationAttributes.get("name");
        String value = (String) annotationAttributes.get("value");

        if ("test".equals(propertyName) && "123".equals(value)) {
            return true;
        }

        return false;
    }
}
