package com.demo.conditionAutowire.condition;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MyOnPropertyCondition implements Condition {

    /**
     * 匹配出对应的 bean
     * @param context 可以获取 spring 中一些基本的信息
     * @param metadata 可以获取到 注解上面对应的信息
     * @return boolean
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {



        return false;
    }
}
