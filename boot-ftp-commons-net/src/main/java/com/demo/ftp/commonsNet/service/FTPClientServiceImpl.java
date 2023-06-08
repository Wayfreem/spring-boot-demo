package com.demo.ftp.commonsNet.service;

import com.demo.ftp.commonsNet.model.FileEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDate;


@Service
@Slf4j
public class FTPClientServiceImpl implements FTPClientService {

    @Autowired
    private FTPClient ftpClient;

    @Autowired
    private FileEntity fileEntity;

    /**
     * 下载
     *
     * @param remoteFileName 远程的文件名称
     * @param localFileName  下载之后的文件名称
     * @param remoteDir      文件存放的地址
     */
    public ResponseEntity<Object> download(String remoteFileName, String localFileName, String remoteDir) throws Exception {
        try {
            boolean flag = ftpClient.changeWorkingDirectory("./files");
            if (flag) {
                System.out.println("-------切换成功");
            }

            FTPFile[] ftpFiles = ftpClient.listFiles("./files", file -> file.isFile() && file.getName().equals(remoteFileName));
            for (FTPFile ftpFile: ftpFiles){
                System.out.println(ftpFile.getName());
            }

            InputStream inputStream = ftpClient.retrieveFileStream(remoteDir);  // 找到获取对应的文件
            if (inputStream == null) throw new RuntimeException("没有找到文件");

            try (FastByteArrayOutputStream out = new FastByteArrayOutputStream()) {
                int len;
                byte[] buffer = new byte[1024];                     // 缓冲区
                while ((len = inputStream.read(buffer)) != -1) {    // 将接受的数据写入缓冲区数组buffer
                    out.write(buffer, 0, len);               // 将缓冲区buffer写入byte数组输出流
                }
                inputStream.close();

                return ResponseEntity
                        .ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + URLEncoder.encode(LocalDate.now() + localFileName + ".xls", "UTF-8") + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                        .header(HttpHeaders.CONTENT_LENGTH, out.size() + "")
                        .header("Connection", "close")
                        .body(out.toByteArray());
            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(("文件下载失败！" + e.getMessage()).getBytes("UTF-8"));
        }
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
        try {
            // 由于是单列模式，每次切换了之后如果不进入根目录的话，就会在上一次的目录继续创建文件
            boolean flag = ftpClient.changeWorkingDirectory("/");
            if (flag) {
                System.out.println("-------切换成功");
            }
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
