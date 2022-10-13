package com.demo.servlet.filter.servletFilter.registry;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 将自定义的 Filter 注入到 Spring容器 bean 中
 * @author wuq
 * @Time 2022-10-13 10:02
 * @Description
 */
@Configuration
public class ServletRegistry {

    @Bean
    public FilterRegistrationBean<TestAllFilter> userFilterRegistry() {
        FilterRegistrationBean<TestAllFilter> bean = new FilterRegistrationBean<>();

        bean.setFilter(new TestAllFilter());       //注册自定义过滤器
        bean.setName("TestAllFilter");     //过滤器名称
        bean.addUrlPatterns("/*");  //过滤所有路径
        bean.setOrder(1);           //优先级，最顶级
        return bean;
    }

    @Bean
    public FilterRegistrationBean<TestSingleFilter> userTestFilterRegistry() {
        FilterRegistrationBean<TestSingleFilter> bean = new FilterRegistrationBean<>();

        bean.setFilter(new TestSingleFilter());       //注册自定义过滤器
        bean.setName("TestSingleFilter");     //过滤器名称
        bean.addUrlPatterns("/getUser/*");      //过滤所有路径
        bean.setOrder(6);               //优先级，越低越优先
        return bean;
    }

}
