package com.demo.sse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author wuq
 * @Time 2022-12-16 16:53
 * @Description
 */
@SpringBootApplication
@ServletComponentScan
public class SSEApplication {

    public static void main(String[] args) {
        SpringApplication.run(SSEApplication.class, args);
    }
}
