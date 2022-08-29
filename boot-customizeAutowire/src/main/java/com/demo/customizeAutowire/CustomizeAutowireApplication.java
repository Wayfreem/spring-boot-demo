package com.demo.customizeAutowire;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@EnableAutoConfiguration
@ComponentScan("com.demo")
public class CustomizeAutowireApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(CustomizeAutowireApplication.class)
                .web(WebApplicationType.NONE).run(args);


        // 获取 Bean
        String hellworld = context.getBean("helloWorld", String.class);
        System.out.println("自动装配：" + hellworld);
        System.out.println("-------------------------");

        String condition = context.getBean("conditionHello", String.class);
        System.out.println("自动条件装配：" + condition);

        context.close();
    }
}
