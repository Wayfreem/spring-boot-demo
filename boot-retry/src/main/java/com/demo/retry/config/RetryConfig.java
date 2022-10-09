package com.demo.retry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

/**
 * 使用 RetryTemple 时，增加配置类，用于注册 RetryTemplate 为 Spring 的配置 Bean
 * @author wuq
 * @Time 2022-10-9 15:37
 * @Description
 */
@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {

        // 定义简易重试策略，最大重试次数为3次,重试间隔为3s
        RetryTemplate retryTemplate = RetryTemplate.builder()
                .maxAttempts(3)
                .fixedBackoff(3000)
                .retryOn(RuntimeException.class)
                .build();
        retryTemplate.registerListener(new SimpleRetryListener());  // 添加监听
        // 这里还可以增加 重试策略 以及 回退策略
        return retryTemplate;
    }

}
