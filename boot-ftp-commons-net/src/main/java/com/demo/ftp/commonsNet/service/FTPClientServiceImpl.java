package com.demo.ftp.commonsNet.service;

import com.demo.ftp.commonsNet.model.FileEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;


@Service
@Slf4j
public class FTPClientServiceImpl implements FTPClientService {

    @Autowired
    private FTPClient ftpClient;

    @Autowired
    private FileEntity fileEntity;

    @Override
    public void download(String remoteFileName, String localFileName, String remoteDir) {

    }

    /**
     * 上传文件
     *
     * @param inputStream 待上传文件的输入流
     * @param originName  文件保存时的名字
     * @param remoteDir   文件要存放的目录
     */
    @Override
    public boolean upload(InputStream inputStream, String originName, String remoteDir) {
        if (ftpClient == null) return false;

        try {
            // 由于是单列模式，每次切换了之后如果不进入根目录的话，就会在上一次的目录继续创建文件
            ftpClient.changeWorkingDirectory(fileEntity.getFtpFilepath());
            boolean isChange = ftpClient.changeWorkingDirectory(remoteDir); //进入到文件保存的目录
            if (!isChange) {// 判断文件夹是否存在，不存在就需要创建
                ftpClient.makeDirectory(remoteDir);
                ftpClient.changeWorkingDirectory(remoteDir);
            }

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            Boolean isSuccess = ftpClient.storeFile(originName, inputStream);//保存文件
            if (!isSuccess){
                throw new Exception("文件上传失败！");
            }
            log.info("{} ------>>>> 上传成功！", originName);
            return true;
        } catch (IOException e) {
            log.error("{} ------>>>> 上传失败！", originName);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 切换目录，如果不存在就直接创建
     * @param client
     * @param path
     * @throws IOException
     */
    private void createDir(FTPClient client, String path) throws IOException {
        String[] dirs = path.split("/");
        for (String dir : dirs) {
            if (StringUtils.isEmpty(dir)) {
                continue;
            }
            if (!client.changeWorkingDirectory(dir)) {
                client.makeDirectory(dir);
            }
            client.changeWorkingDirectory(dir);
        }
    }
}
