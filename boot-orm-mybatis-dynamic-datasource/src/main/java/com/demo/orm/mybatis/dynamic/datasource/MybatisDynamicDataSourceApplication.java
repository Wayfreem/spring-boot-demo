package com.demo.orm.mybatis.dynamic.datasource;

import com.demo.orm.mybatis.dynamic.datasource.confing.DynamicDataSourceConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = "com.demo.orm.mybatis.dynamic.datasource")
@Import({DynamicDataSourceConfig.class})
public class MybatisDynamicDataSourceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MybatisDynamicDataSourceApplication.class, args);
    }
}
