## 集成 ftp

### pom 添加 依赖包
```xml
 <dependency>
    <groupId>commons-net</groupId>
    <artifactId>commons-net</artifactId>
    <version>3.6</version>
</dependency>
```

### 增加配置文件信息
```propertie
#ftp
# ftp地址-ftp请求。ftp://192.168.3.127:22

ftp.host=192.168.62.129
ftp.port=21
ftp.username=freem
ftp.password=123456

# ftp请求读取写入的文件路径
ftp.filepath=/home/upload

# http请求路径。http://192.168.3.127:82
ftp.web.host=192.168.3.127
ftp.web.port=22
```

### 添加一个bean
```java
@Component
@Data
public class FileEntity {

    /**
     * ftp站点
     */
    @Value("${ftp.host}")
    private String ftpHost;

    /**
     * ftp端口号
     */
    @Value("${ftp.port}")
    private int ftpPort;

    /**
     * ftp访问用户名
     */
    @Value("${ftp.username}")
    private String ftpUsername;

    /**
     * ftp访问密码
     */
    @Value("${ftp.password}")
    private String ftpPassword;

    /**
     * ftp访问文件路径
     */
    @Value("${ftp.filepath}")
    private String ftpFilepath;

    /**
     * ftp提供的http方式访问地址
     */
    @Value("${ftp.web.host}")
    private String ftpWebHost;

    /**
     * ftp提供的http方式访问的端口号
     */
    @Value("${ftp.web.port}")
    private String ftpWebPort;
}
```

### 添加一个 FtpConfig
```java
@Configuration
@Slf4j
public class FtpConfig {

    @Autowired
    FileEntity fileEntity;

    @Bean
    public FTPClient ftpClient() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(1000 * 30);//设置连接超时时间
        ftpClient.setControlEncoding("utf-8");//设置ftp字符集
//        ftpClient.enterLocalPassiveMode();//设置被动模式，文件传输端口设置
        try {
//            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);//设置文件传输模式为二进制，可以保证传输的内容不会被改变
//            ftpClient.connect(beansEntity.getIp()+":"+beansEntity.getPort());
            ftpClient.setDefaultPort(fileEntity.getFtpPort());
            ftpClient.connect(fileEntity.getFtpHost(), fileEntity.getFtpPort());
            ftpClient.login(fileEntity.getFtpUsername(), fileEntity.getFtpPassword());
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                ftpClient.disconnect();
                log.error("未连接到FTP，用户名或密码错误!");
                return null;
            } else {
                log.info("FTP连接成功!");
                return ftpClient;
            }
        } catch (SocketException socketException) {
            log.error("FTP的IP地址可能错误，请正确配置!");
            return null;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            log.error("FTP的端口错误,请正确配置!");
            return null;
        }
    }
}
```

### 具体实现就参看 service 包下面的了

## 测试方法
BootFtpApplicationTests.java
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class BootFtpApplicationTests {

    @Autowired
    private FTPClientService ftpClientService;

    @Autowired
    private FileEntity fileEntity;

    @Test
    public void test1(){
        try {
            InputStream inputStream = new FileInputStream(new File("E:\\test\\img\\150A02.png"));
            ftpClientService.upload(inputStream,"004.png","2019");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```

后端用于接收文件上传的类的写法
```java
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@Slf4j
public class FileUploadController {

    @RequestMapping("upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.error("上传文件为空");
            return null;
        }

        String fileName = file.getName();
        String fileOriginalName = file.getOriginalFilename();
        return null;
    }

    @RequestMapping("uploadWithPath")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value="value",required=false) String value){
        if (file.isEmpty()) {
            log.error("上传文件为空");
            return null;
        }
        String fileName = file.getName();
        String fileOriginalName = file.getOriginalFilename();
        return null;
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

## 附
ftp 连接池文章链接：https://www.jb51.net/article/153276.htm
