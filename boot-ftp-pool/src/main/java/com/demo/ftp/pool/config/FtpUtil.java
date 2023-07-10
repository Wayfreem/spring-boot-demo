package com.demo.ftp.pool.config;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * FTP工具类
 * 提供文件上传和下载
 */
@Component
public class FtpUtil {
    private final FtpPool pool;

    public FtpUtil(FtpPool pool) {
        this.pool = pool;
    }

    public boolean upload(InputStream inputStream, String originName, String remoteDir) {
        FTPClient ftpClient = pool.getFTPClient();
        try {
            Boolean isSuccess = ftpClient.storeFile(originName, inputStream);//保存文件
            if (!isSuccess) {
                throw new IOException("文件上传失败！");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            /** 归还资源 **/
            pool.returnFTPClient(ftpClient);
        }
    }

    public static ResponseEntity<Object> download(String fileName, String localName, String path) {
        return null;
    }
}
