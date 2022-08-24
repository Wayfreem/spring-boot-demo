package com.demo.autowire.enable.config;

import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(HelloWorldConfiguration.class)  // 导入 HelloWorldConfiguration
public @interface EnableHelloWorld {
}
