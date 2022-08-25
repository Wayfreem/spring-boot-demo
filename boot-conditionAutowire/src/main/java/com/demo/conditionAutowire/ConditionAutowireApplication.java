package com.demo.conditionAutowire;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class ConditionAutowireApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext content = new SpringApplicationBuilder(ConditionAutowireApplication.class)
                .web(WebApplicationType.NONE).run(args);
        String hellWorld  =content.getBean("helloWorld",String.class);
        System.out.println("hello world" + hellWorld);
        content.close();
    }
}
