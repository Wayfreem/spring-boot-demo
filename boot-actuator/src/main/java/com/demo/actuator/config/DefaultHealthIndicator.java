package com.demo.actuator.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DefaultHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean isHealth = true;
        // 此处应判断各个组件是否连接正常,其中一个连接异常则设置isHealth = false
        isHealth = false; // 假如redis连接异常
        String errorMsg = "redis连接异常";
        if(!isHealth) {
            return Health.down().withDetail("message",errorMsg).build();
        }
        return Health.up().build();
    }
}
