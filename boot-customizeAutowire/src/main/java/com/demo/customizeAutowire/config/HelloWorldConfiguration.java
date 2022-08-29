package com.demo.customizeAutowire.config;

import org.springframework.context.annotation.Bean;

/**
 * @author wuq
 * @Time 2022-8-29 16:47
 * @Description
 */
public class HelloWorldConfiguration {

    @Bean
    public String helloWorld(){
        return "hell world 2022";
    }
}
