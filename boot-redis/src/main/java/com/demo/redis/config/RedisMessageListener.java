package com.demo.redis.config;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisMessageListener {

    String topic(); // 事件主题
}
