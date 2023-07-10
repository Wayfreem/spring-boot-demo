package com.demo.ftp.pool.config;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FtpClientFactory implements PooledObjectFactory<FTPClient> {

    private FtpConfig config;

    public FtpClientFactory(FtpConfig config) {
        this.config = config;
    }

    /**
     * 创建连接到池中
     */
    @Override
    public PooledObject<FTPClient> makeObject() {
        FTPClient ftpClient = new FTPClient();//创建客户端实例
        return new DefaultPooledObject<>(ftpClient);
    }

    /**
     * 销毁连接，当连接池空闲数量达到上限时，调用此方法销毁连接
     */
    @Override
    public void destroyObject(PooledObject<FTPClient> pooledObject) {
        FTPClient ftpClient = pooledObject.getObject();
        try {
            ftpClient.logout();
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not disconnect from server.", e);
        }
    }

    /**
     * 链接状态检查
     */
    @Override
    public boolean validateObject(PooledObject<FTPClient> pooledObject) {
        FTPClient ftpClient = pooledObject.getObject();
        try {
            return ftpClient.sendNoOp();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 初始化连接
     */
    @Override
    public void activateObject(PooledObject<FTPClient> pooledObject) throws Exception {
        FTPClient ftpClient = pooledObject.getObject();
        ftpClient.connect(config.getHost(), config.getPort());
        ftpClient.login(config.getUserName(), config.getPassWord());
        ftpClient.setControlEncoding(config.getEncoding());
        String pathname = new String(config.getWorkDir().getBytes(), FTP.DEFAULT_CONTROL_ENCODING);
        changeDir(ftpClient, pathname);
        ftpClient.enterLocalPassiveMode(); //设为被动模式
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);//设置上传文件类型为二进制，否则将无法打开文件
        ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
    }

    private void changeDir(FTPClient ftpClient, String pathname) throws IOException {
        if (!ftpClient.changeWorkingDirectory(pathname)) {
            if (ftpClient.makeDirectory(pathname)) {
                ftpClient.changeWorkingDirectory(pathname);
            } else {
                throw new RuntimeException("创建工作目录失败！");
            }
        }
    }

    /**
     * 钝化连接，使链接变为可用状态
     */
    @Override
    public void passivateObject(PooledObject<FTPClient> pooledObject) {
        FTPClient ftpClient = pooledObject.getObject();
        try {
            String pathname = new String(config.getRoot().getBytes(), FTP.DEFAULT_CONTROL_ENCODING);
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.logout();
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not disconnect from server.", e);
        }
    }

    /**
     * 用于连接池中获取pool属性
     */
    public FtpConfig getConfig() {
        return config;
    }

}


