package com.demo.conditionAutowire.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(MyOnPropertyCondition.class)   // 导入具体的判断类
public @interface MyConditionalOnProperty{
    // 这里定义几个注解的信息
    String value();

    String name();
}