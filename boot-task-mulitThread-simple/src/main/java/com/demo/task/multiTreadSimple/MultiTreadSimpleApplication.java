package com.demo.task.multiTreadSimple;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class MultiTreadSimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiTreadSimpleApplication.class, args);
    }
}
