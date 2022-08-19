package com.demo.actuator.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author wuq
 * @Time 2022-8-19 11:17
 * @Description
 */
@Component
public class ServerInfoContributor implements InfoContributor {


    @Override
    public void contribute(Info.Builder builder) {
        // 这里可以访问数据库读取数据库中的信息
        builder.withDetail("date", LocalDateTime.now());
    }
}
