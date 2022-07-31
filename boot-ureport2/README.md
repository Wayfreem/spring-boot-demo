## 简介

[ureport2 学习地址](https://www.w3cschool.cn/ureport/)

使用 spring boot 集成 UReport2 实现打印报表功能开发。

既然是需要连接数据库，就需要安装 MySQL(采用docker 安装) [安装参考链接](https://blog.csdn.net/qq_18948359/article/details/125486934?spm=1001.2014.3001.5502)

## 集成的步骤

### 第一步：引入依赖

**pom 文件**

在官方找到的最新的 ureport2 版本就是这个 2.2.9, 后面没有再更新了
```xml
<dependency>
    <groupId>com.bstek.ureport</groupId>
    <artifactId>ureport2-console</artifactId>
    <version>2.2.9</version>
</dependency>

<!--    数据库链接    -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.29</version>
    <scope>runtime</scope>
</dependency>
```

### 第二步：增加对应的配置文件
需要使用 `ServletRegistrationBean` 注入一个 `servlet` 到 spring中
```java
@Configuration
@ImportResource("classpath:ureport-console-context.xml")
public class UReport2Config {

    /**
     * 这里是采用 ServletRegistrationBean 向 spring 容器创建一个 servlet 服务
     * @return
     */
    @Bean
    public ServletRegistrationBean buildUReportServlet() {
        return new ServletRegistrationBean(new UReportServlet(), "/ureport/*");
    }
}
```

### 第三步：创建对应的配置文件
在 resources 下面创建文件 `ureport-context.xml`, 内容如下：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">

    <import resource="classpath:ureport-console-context.xml"/>

    <bean id="propertyConfigurer" parent="ureport.props">
        <property name="location">
            <value>classpath:ureport-config.properties</value>
        </property>
    </bean>
</beans>
```

在 resources 下面创建文件 `ureport-config.properties`
```properties
ureport.disableHttpSessionReportCache=true
ureport.disableFileProvider=false
ureport.fileStoreDir=d:/ureport2files        // 指定文件存储的路劲
ureport.debug=true
```

### 第四步：在 application.properties 文件中增加数据库连接
```properties
spring.datasource.url=jdbc:mysql://192.168.152.129:3306/study?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF8
spring.datasource.username=admin
spring.datasource.password=123456
```

### 第五步：测试
在浏览器上面访问: localhost:8080/ureport/designer

## 将打印模板保存到数据库中

### 引入依赖

pom 文件

我们使用数据库来存储打印模板，这里使用 jpa 的方式连接数据库，方便于数据库操作

```xml
<!--    引入 Jpa    -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### 增加对应的实体类以及 repository
```java
@Data
@Table(name = "U_Report_File")
public class UReportFile {

    @Id
    @Column(name = "id", nullable = false, length = 16)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = 64)
    private String name;    // 模板名称

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "content", columnDefinition = "CLOB")
    private byte[] content; // 模板内容

    private Date createTime;

    private Date updateTime;
}
```

增加对应的 repository

```java
public interface UReportFileRepository extends JpaRepository<UReportFile, Long> {
}
```

### 创建服务类
UReportFileService.java

主要是为对 ureport 扩展保存到数据库中增加对应的逻辑
```java
@Service
public class UReportFileService {

    @Autowired
    private UReportFileRepository uReportFileRepository;

    public UReportFile findByName(String fileName) {
        return uReportFileRepository.findByName(fileName);
    }

    public List<UReportFile> findAll() {
        return uReportFileRepository.findAll();
    }

    public UReportFile save(String fileName,String content){
        UReportFile uReportFile = new UReportFile();
        uReportFile.setName(fileName);
        uReportFile.setContent(content);
        uReportFile.setCreateTime(new Date());
        return uReportFileRepository.saveAndFlush(uReportFile);
    }

    public void deleteByName(String fileName){
        UReportFile uReportFile = uReportFileRepository.findByName(fileName);
        if (uReportFile != null) {
            uReportFileRepository.delete(uReportFile);
        }
    }
    
}
```

### 增加 ureport 扩展

由于是需要保存到数据库中，所以需要实现 ReportProvider 接口，我们按照要求写出具体的实现就好

```java
@Component
public class SqlReportProvider implements ReportProvider {

    @Autowired
    private UReportFileService reportService;

    // 增加头部信息
    private String prefix = "report:";

    @Override
    public String getName() {
        // 返回存储器的名称
        return "报表模板保存到数据库";
    }

    @Override
    public boolean disabled() {
        // 返回是否禁用, 默认为非禁用, 不需要的打印模板就删除掉
        return false;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * 根据报表名加载报表文件
     * @param fileName 报表名称
     * @return
     */
    @Override
    public InputStream loadReport(String fileName) {
        try {
            UReportFile uReportFile= reportService.findByName(removePrefix(fileName));
            if (uReportFile==null) return null;
            return IOUtils.toInputStream(uReportFile.getContent(),"utf-8");
        } catch (Exception e) {
            throw new ReportException(e);
        }
    }

    @Override
    public void deleteReport(String fileName) {
        reportService.deleteByName(fileName);
    }

    @Override
    public List<ReportFile> getReportFiles() {
        List<UReportFile> uReportFiles = reportService.findAll();
        return uReportFiles.stream()
                .map( uReportFile -> new ReportFile(getPrefix()+uReportFile.getContent(), uReportFile.getCreateTime()))
                .collect(Collectors.toList());
    }

    /**
     * 保存打印模板
     * @param fileName 报表名称
     * @param content 报表的XML内容
     */
    @Override
    public void saveReport(String fileName, String content) {
        reportService.save(fileName, content);
    }

    private String removePrefix(String file){
        return file.replace("report:","");
    }
}
```