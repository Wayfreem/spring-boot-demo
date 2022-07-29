## 简介

[ureport2 学习地址](https://www.w3cschool.cn/ureport/)

使用 spring boot 集成 UReport2 实现打印报表功能开发。

既然是需要连接数据库，就需要安装 MySQL(采用docker 安装) [安装参考链接](https://blog.csdn.net/qq_18948359/article/details/125486934?spm=1001.2014.3001.5502)

## 集成的步骤

### 第一步：引入依赖

**pom 文件**

我们使用数据库来存储打印模板，这里使用 jpa 的方式连接数据库，方便于数据库操作

在官方找到的最新的 ureport2 版本就是这个 2.2.9, 后面没有再更新了
```xml
<dependency>
    <groupId>com.bstek.ureport</groupId>
    <artifactId>ureport2-console</artifactId>
    <version>2.2.9</version>
</dependency>

<!--    引入 Jpa    -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
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

### 第三步：

### 第四步：

### 第五步：
