package com.demo.servlet.listener.servletListener.registry;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用于将自定义的 Listener 注入到 Spring 容器中
 */
@Configuration
public class ListenerRegistry {

    @Bean
    public ServletListenerRegistrationBean<?> userContextListener(){
        ServletListenerRegistrationBean<TestContextListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new TestContextListener());
        return bean;
    }

    @Bean
    public ServletListenerRegistrationBean<TestRequestListener> userRequestListener(){
        ServletListenerRegistrationBean<TestRequestListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new TestRequestListener());
        return bean;
    }

    @Bean
    public ServletListenerRegistrationBean <TestSessionListener> userSessionListener(){
        ServletListenerRegistrationBean<TestSessionListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new TestSessionListener());
        return bean;
    }
}
