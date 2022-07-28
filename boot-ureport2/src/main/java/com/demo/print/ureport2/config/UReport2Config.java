package com.demo.print.ureport2.config;

import com.bstek.ureport.console.UReportServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:ureport-console-context.xml")
public class UReport2Config {

    /**
     * 这里是采用 ServletRegistrationBean 向 spring 容器创建一个 servlet 服务
     * @return ServletRegistrationBean
     */
    @Bean
    public ServletRegistrationBean buildUReportServlet() {
        return new ServletRegistrationBean(new UReportServlet(), "/ureport/*");
    }
}