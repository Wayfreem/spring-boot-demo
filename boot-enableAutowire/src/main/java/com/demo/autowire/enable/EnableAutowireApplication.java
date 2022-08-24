package com.demo.autowire.enable;

import com.demo.autowire.enable.config.EnableHelloWorld;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


@EnableHelloWorld
public class EnableAutowireApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext content = new SpringApplicationBuilder(EnableAutowireApplication.class)
                .web(WebApplicationType.NONE).run(args);
        String hellWorld  =content.getBean("helloWorld",String.class);
        System.out.println("hello world" + hellWorld);
        content.close();
    }
}
