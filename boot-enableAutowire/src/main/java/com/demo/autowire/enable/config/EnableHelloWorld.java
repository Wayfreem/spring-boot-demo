package com.demo.autowire.enable.config;

import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
//@Import(HelloWorldConfiguration.class)  // 导入 HelloWorldConfiguration
@Import(HelloWorldImportSelector.class)  // 导入 HelloWorldImportSelector 选择器，通过选择器注入
public @interface EnableHelloWorld {
}
