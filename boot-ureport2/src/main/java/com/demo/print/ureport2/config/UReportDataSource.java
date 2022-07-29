package com.demo.print.ureport2.config;

import com.bstek.ureport.definition.datasource.BuildinDatasource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Slf4j
public class UReportDataSource implements BuildinDatasource {
    private static final String NAME = "UReportDataSource";

    @Autowired
    private DataSource dataSource;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.error("UReport 数据源 获取连接失败！");
            e.printStackTrace();
        }
        return null;
    }
}
