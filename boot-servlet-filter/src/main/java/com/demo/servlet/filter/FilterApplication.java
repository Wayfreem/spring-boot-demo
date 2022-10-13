package com.demo.servlet.filter;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author wuq
 * @Time 2022-10-12 17:07
 * @Description
 */
@ServletComponentScan       // 开启对 webServlet 支持
@SpringBootApplication
public class FilterApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilterApplication.class, args);
    }
}
