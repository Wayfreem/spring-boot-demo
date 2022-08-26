package com.demo.conditionAutowire;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.demo")
public class ConditionAutowireApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext content = new SpringApplicationBuilder(ConditionAutowireApplication.class)
                .web(WebApplicationType.NONE).run(args);

        String hellWorld  =content.getBean("conditionHello", String.class);
        System.out.println("hello world" + hellWorld);
        content.close();
    }
}
