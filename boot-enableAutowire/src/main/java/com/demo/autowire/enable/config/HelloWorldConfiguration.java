package com.demo.autowire.enable.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloWorldConfiguration {

    @Bean
    public String helloWorld(){
        return "hell world 2022";
    }
}
