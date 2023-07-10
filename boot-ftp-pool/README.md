## 集成 ftp 连接池

使用连接池的方式可以减少频繁创建对象的开销。下面是具体集成的步骤

## 具体实现步骤

### 第一步：引入基础依赖

```xml
<!-- 这个是为了方便引入配置文件的内容到指定的类中 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>

<!-- 这个是 ftp 工具类的包 -->
<dependency>
    <groupId>commons-net</groupId>
    <artifactId>commons-net</artifactId>
    <version>3.6</version>
</dependency>

<!-- 这个连接池的包 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```
### 第二步：增加配置文件内容

```properties
ftp.host=14.23.148.134
ftp.port=8084
ftp.userName=ftpuser
ftp.passWord=selsesfu4
ftp.root=/
ftp.workDir=/test
ftp.encoding=UTF-8
ftp.maxTotal=10
ftp.minIdel=2
ftp.maxIdle=5
ftp.maxWaitMillis=30000
```

### 第三步：创建 FtpConfig

```java
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
```


### 第四步：创建FTP 连接池相关类

**Ftp 工厂类**

```java
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
```

**连接池类**

这里是将 Ftp 连接对象池化

```java
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
```

### 第五步：提供 FtpUtils 工具类

这里是有一个上传的方法，下载的方法可以参看下 ``
