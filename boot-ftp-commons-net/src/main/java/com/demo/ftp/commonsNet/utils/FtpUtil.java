package com.demo.ftp.commonsNet.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;

@Component
public class FtpUtil {

    private final static String HOST = "14.23.148.134";
    private final static int PORT = 8084;
    private final static String USERNAME = "ftpuser";
    private final static String PWD = "selsesfu4";
    private final static String FILE_PATH = "/files";


    /**
     * Description: 向FTP服务器上传文件
     *
     * @param filename 上传到FTP服务器上的文件名
     * @param input    输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(String filename, InputStream input) {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        try {

            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.connect(HOST, PORT);    // 连接FTP服务器
            ftp.enterLocalPassiveMode();
            ftp.login(USERNAME, PWD);   // 登录
            ftp.setConnectTimeout(1000 * 30);//设置连接超时时间
            ftp.setControlEncoding("utf-8");//设置ftp字符集

            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
            //切换到上传目录
            if (!ftp.changeWorkingDirectory(FILE_PATH)) {
                //如果目录不存在创建目录
                String tempPath = FILE_PATH;

                // 服务器文件存放路径
                if (!ftp.changeWorkingDirectory(tempPath)) {
                    if (ftp.makeDirectory(tempPath)) {
                        ftp.changeWorkingDirectory(tempPath);
                    } else {
                        return result;
                    }
                }
            }

            //设置上传文件的类型为二进制类型
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.sendCommand("OPTS UTF8", "ON");

            //上传文件，解决中文乱码问题
            if (!ftp.storeFile(new String(filename.getBytes("UTF-8"), "ISO-8859-1"), input)) {
                return result;
            }
            input.close();
            ftp.logout();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    new RuntimeException("ftp文件读写错误:" + ioe);
                }
            }
        }
        return result;
    }

    /**
     * Description: 从FTP服务器下载文件
     *
     * @param remotePath FTP服务器上的相对路径
     * @param fileName   要下载的文件名
     * @param localPath  下载后保存到本地的路径
     * @return
     */
    public static boolean downloadFile(String remotePath, String fileName, String localPath) {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        try {

            ftp.connect(HOST, PORT);    // 连接FTP服务器
            ftp.login(USERNAME, PWD);   // 登录
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
            ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    File localFile = new File(localPath + "/" + ff.getName());

                    OutputStream is = new FileOutputStream(localFile);
                    ftp.retrieveFile(ff.getName(), is);
                    is.close();
                }
            }

            ftp.logout();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return result;
    }

    /**
     * 下载
     *
     * @param remoteFileName 远程的文件名称
     * @param localFileName  下载之后的文件名称
     * @param remoteDir      文件存放的地址
     */
    public static ResponseEntity<Object> download(String remoteFileName, String localFileName, String remoteDir) throws Exception {
        FTPClient ftp = new FTPClient();

        try {
            ftp.connect(HOST, PORT);    // 连接FTP服务器
            ftp.login(USERNAME, PWD);   // 登录

            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(("连接文件服务器失败！"));
            }

            InputStream inputStream = ftp.retrieveFileStream(remoteDir);  // 找到获取对应的文件
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
        }finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
    }


}
