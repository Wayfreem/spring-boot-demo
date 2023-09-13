package com.demo.jwtSecurity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wuq
 * @Time 2023-9-8 11:02
 * @Description
 */
@SpringBootApplication
@MapperScan("com.demo.jwtSecurity.mapper")
public class SecurityJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityJwtApplication.class, args);
    }

}
