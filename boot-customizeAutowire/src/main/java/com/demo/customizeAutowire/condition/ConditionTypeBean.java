package com.demo.customizeAutowire.condition;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 条件注入的具体 bean
 * @author wuq
 * @Time 2022-8-26 10:48
 * @Description
 */
@Component
public class ConditionTypeBean {

    @Bean
    @MyConditionalOnProperty(value = "123", name = "test")
    public String conditionHello(){
        return "condition 装配成功。";
    }
}
