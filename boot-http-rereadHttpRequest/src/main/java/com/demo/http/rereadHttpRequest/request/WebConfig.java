package com.demo.http.rereadHttpRequest.request;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

/**
 * @author wuq
 * @Time 2023-1-13 9:40
 * @Description
 */
@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<RequestBodyReaderFilter> requestBodyReaderFilter() {
        FilterRegistrationBean<RequestBodyReaderFilter> registrationBean = new FilterRegistrationBean<>();
        RequestBodyReaderFilter filter = new RequestBodyReaderFilter();
        registrationBean.setFilter(filter);     // 注册 Filter

        ArrayList<String> urls = new ArrayList<>();
        urls.add("/*"); //配置过滤规则
        registrationBean.setUrlPatterns(urls);
        registrationBean.setOrder(3);
        return registrationBean;
        
    }
}
