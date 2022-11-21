package com.demo.orm.mybatis.dynamic.datasource.confing;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源配置类
 */
@Configuration
public class DynamicDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.second")
    public DataSource secondDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean
    @Primary
    public DynamicDataSource dataSource(DataSource primaryDataSource, DataSource secondDataSource) {
        // 这里新建一个 Map 是将对应的数据源放入其中，然后给后面使用的时候来获取数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("primary-source",primaryDataSource);
        targetDataSources.put("second-source", secondDataSource);
        return new DynamicDataSource(primaryDataSource, targetDataSources);
    }
}
