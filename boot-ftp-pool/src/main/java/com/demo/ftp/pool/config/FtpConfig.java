package com.demo.ftp.pool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ftp")
public class FtpConfig {


    /**
     * IP
     */
    private String host;

    /**
     * 端口
     */
    private int port;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String passWord;

    /**
     * 根目录
     */
    private String root;

    /**
     * 工作目录
     */
    private String workDir;

    /**
     * 字符集
     */
    private String encoding;

    /**
     * 最大连接数，默认值 DEFAULT_MAX_TOTAL = 8
     */
    private int maxTotal;

    /**
     * 最小空闲连接数， 默认值 DEFAULT_MIN_IDLE = 0
     */
    private int minIdel;

    /**
     * 最大空闲连接数， 默认值 DEFAULT_MAX_IDLE = 8
     */
    private int maxIdle;

    /**获取连接时的最大等待时间（单位 ：毫秒）；默认值 DEFAULT_MAX_WAIT_MILLIS = -1L， 永不超时。(2.11.1 版本才有该功能)*/
    //private int maxWaitMillis;
}

