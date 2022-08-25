package com.demo.autowire.enable.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 实现一个接口 来返回bean数组 有弹性 可以实现多种自定义返回值方式
 */
public class HelloWorldImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{HelloWorldConfiguration.class.getName()};       // 这个地方返回我们具体的 config类
    }

}
