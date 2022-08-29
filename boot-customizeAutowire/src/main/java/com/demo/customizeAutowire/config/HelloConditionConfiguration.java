package com.demo.customizeAutowire.config;

import com.demo.customizeAutowire.condition.MyConditionalOnProperty;

/**
 * @author wuq
 * @Time 2022-8-29 17:30
 * @Description
 */
@MyConditionalOnProperty(value = "123", name = "test")
public class HelloConditionConfiguration {
}
