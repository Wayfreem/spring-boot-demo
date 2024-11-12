package com.demo.task.mulitThread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class MulitTreadApplication {

    public static void main(String[] args) {
        SpringApplication.run(MulitTreadApplication.class, args);
    }
}
