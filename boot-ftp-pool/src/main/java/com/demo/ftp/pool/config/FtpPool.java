package com.demo.ftp.pool.config;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * FTP连接池
 * <p>
 * 1.可以获取池中空闲链接
 * 2.可以将链接归还到池中
 * 3.当池中空闲链接不足时，可以创建链接
 */
@Component
public class FtpPool implements DisposableBean {
    private final FtpClientFactory factory;
    private final GenericObjectPool<FTPClient> internalPool;

    /**
     * 初始化连接池
     */
    public FtpPool(@Autowired FtpClientFactory factory) {
        this.factory = factory;
        FtpConfig config = factory.getConfig();
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(config.getMaxTotal());
        poolConfig.setMinIdle(config.getMinIdel());
        poolConfig.setMaxIdle(config.getMaxIdle());
        //poolConfig.setMaxWait(Duration.ofMillis(config.getMaxWaitMillis())); // 2.11.1 版本才有该功能
        this.internalPool = new GenericObjectPool<FTPClient>(factory, poolConfig);
    }

    /**
     * 从连接池中取连接
     */
    public FTPClient getFTPClient() {
        try {
            return internalPool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将连接归还到连接池
     */
    public void returnFTPClient(FTPClient ftpClient) {
        try {
            internalPool.returnObject(ftpClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁池子
     */
    public void destroy() {
        try {
            internalPool.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
