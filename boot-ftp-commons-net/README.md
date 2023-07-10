## 集成 ftp

### pom 添加 依赖包
```xml
 <dependency>
    <groupId>commons-net</groupId>
    <artifactId>commons-net</artifactId>
    <version>3.6</version>
</dependency>
```

### 这里为了方便于测试，就直接使用一个工具类

在工具类中增加对应的上传，下载的方法，具体的看下代码实现

```java
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
```

### 增加一个 controller 用于测试文件上传

```java
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@Slf4j
public class FileController {

    /**
     * 多文件上传
     *
     * @param file 前端传入一个文件列表
     * @return map
     */
    @RequestMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(@RequestParam("files") MultipartFile[] file) {
        Map<String, Object> returnMap = new HashMap<>();
        if (file.length == 0) {
            log.error("上传文件为空");
            returnMap.put("msg", "上传文件为空");
            returnMap.put("code", "error");
            return returnMap;
        }

        for (MultipartFile uploadFile : file) {
            String fileOriginalName = uploadFile.getOriginalFilename();

            try {
                InputStream inputStream = uploadFile.getInputStream();
                FtpUtil.uploadFile(fileOriginalName, inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("文件传换位输入流报错！");
            }
        }

        returnMap.put("msg", "上传文件上传成功");
        returnMap.put("code", "success");
        return returnMap;
    }

    @RequestMapping("download")
    public ResponseEntity<Object> download() throws Exception {
        String fileName = "tmp001.xls";
        String localName = "测试下载文件";
        String path = "./files/tmp001.xls";

        return FtpUtil.download(fileName, localName, path);
    }
}
```

## 对于 static 文件夹说明
文件夹下有一个用于文件上传的页面，测试文件上传，后端接收之后解析使用。

```html
<form action="http://127.0.0.1:8080/uploadWithPath" method="post" enctype="multipart/form-data">

    <div>
        <label for="value">文件名路径：</label>
         <!-- 这个地方的值与后端接收方法中注解参数的值，需要保持一致  @RequestParam(value="value",required=false) -->
        <input id="value" name="value" type="text" placeholder="请输入文件路径"/>
    </div>

    <div style="margin-top: 20px">
        <input type="file" name="file" />
    </div>
    <div style="margin-top: 20px">
        <input type="submit" value="提交"/>
    </div>
</form>
```

#
