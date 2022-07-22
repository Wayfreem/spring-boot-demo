package com.demo.orm.mybatisPlus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wuq
 * @Time 2022-7-20 14:51
 * @Description
 */
@SpringBootApplication
@MapperScan("com.demo.orm.mybatisPlus.mapper")
public class MybatisPlusApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusApplication.class, args);
    }
}
