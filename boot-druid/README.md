## 说明

在 Spring Boot 中集成 druid 实现 SQL 监控。[参考文档](https://mp.weixin.qq.com/s/bXuy7lkPJYtcWhbrXQPnjg)

## 具体集成步骤

### 第一步： POM 文件依赖

引入 druid 的依赖包

```xml
<properties>
    <alibabaDruidStarter.version>1.2.11</alibabaDruidStarter.version>
</properties>

<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>${alibabaDruidStarter.version}</version>
</dependency>
```

配置属性

- 配置 Druid 数据源（连接池）： 如同c3p0、dbcp数据源可以设置数据源连接初始化大小、最大连接数、等待时间、最小连接数 等一样，Druid数据源同理可以进行设置。
- 配置 Druid web 监控filter（WebStatFilter）： 这个过滤器的作用就是统计web应用请求中所有的数据库信息，比如 发出的sql语句，sql执行的时间、请求次数、请求的url地址、以及seesion监控、数据库表的访问次数等等。
- 配置 Druid 后台管理Servlet（StatViewServlet）： Druid数据源具有监控的功能，并提供了一个web界面方便用户查看，类似安装 路由器 时，人家也提供了一个默认的web页面；需要设置Druid的后台管理页面的属性，比如 登录账号、密码等。


### 第二步：增加配置文件

```yaml
# spring 配置
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: admin
    password: 123456
    url: jdbc:mysql://192.168.152.129:3306/study?useUnicode=true&characterEncoding=utf8&useSSL=false
    # 连接池配置
    druid:
      # 初始化大小，最小，最大
      initial-size: 5
      min-idle: 5
      max-active: 20
      # 配置获取连接等待超时的时间
      max-wait: 60000
      # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位毫秒
      time-between-eviction-runs-millis: 60000
      # 配置一个连接在池中最小生存时间
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1 FROM user
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      # 打开 PSCache，并且指定每个连接上 PSCache 的大小
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
      # 配置监控统计拦截的 Filter，去掉后监控界面 SQL 无法统计，wall 用于防火墙
      filters: stat,wall,slf4j
      # 通过 connection-properties 属性打开 mergeSql 功能；慢 SQL 记录
      connection-properties: druid.stat.mergeSql\=true;druid.stat.slowSqlMillis\=5000
      # 配置 DruidStatFilter
      web-stat-filter:
        enabled: true
        url-pattern: /*
        exclusions: .js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*
      # 配置 DruidStatViewServlet
      stat-view-servlet:
        url-pattern: /druid/*
        # IP 白名单，没有配置或者为空，则允许所有访问
        allow: 127.0.0.1
        # IP 黑名单，若白名单也存在，则优先使用
        deny: 192.168.31.253
        # 禁用 HTML 中 Reset All 按钮
        reset-enable: false
        # 登录用户名/密码
        login-username: root
        login-password: 123456
        # 需要设置enabled=true,否则会报出There was an unexpected error (type=Not Found, status=404).错误，或者将druid-spring-boot-starter的版本降低到1.1.10及以下
        # 是否启用StatViewServlet（监控页面）默认值为false（考虑到安全问题默认并未启动，如需启用建议设置密码或白名单以保障安全）
        enabled: true
```

上述配置文件的参数可以在 `com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties` 和 `org.springframework.boot.autoconfigure.jdbc.DataSourceProperties` 中找到。

### 第二步：配置 Filter

可以通过 `spring.datasource.druid.filters=stat,wall,log4j ...` 的方式来启用相应的内置Filter，不过这些Filter都是默认配置。如果默认配置不能满足需求，可以放弃这种方式，通过配置文件来配置Filter，如下所示：

```properties
# 配置StatFilter 
spring.datasource.druid.filter.stat.enabled=true
spring.datasource.druid.filter.stat.db-type=h2
spring.datasource.druid.filter.stat.log-slow-sql=true
spring.datasource.druid.filter.stat.slow-sql-millis=2000

# 配置WallFilter 
spring.datasource.druid.filter.wall.enabled=true
spring.datasource.druid.filter.wall.db-type=h2
spring.datasource.druid.filter.wall.config.delete-allow=false
spring.datasource.druid.filter.wall.config.drop-table-allow=false
```
目前为以下Filter提供了配置支持，根据（spring.datasource.druid.filter.*）进行配置。

    StatFilter
    WallFilter
    ConfigFilter
    EncodingConvertFilter
    Slf4jLogFilter
    Log4jFilter
    Log4j2Filter
    CommonsLogFilter

不想使用内置的Filters，要想使自定义Filter配置生效需要将对应Filter的enabled设置为true，Druid Spring Boot Starter默认禁用StatFilter，可以将其enabled设置为true来启用它。

