## 简介

[Flyway 官网地址](https://flywaydb.org/documentation/)

使用 spring boot 集成 flyway 项目，实现对数据库版本的控制。

## FLyway 工作流程

1. 项目启动，应用程序完成数据库连接池的建立后，Flyway自动运行。
2. 初次使用时，flyway会创建一个 flyway_schema_history 表，用于记录sql执行记录。
3. Flyway 会扫描项目指定路径下(默认是 classpath:db/migration )的所有 sql 脚本，与 flyway_schema_history 表脚本记录进行比对。如果数据库记录执行过的脚本记录，与项目中的sql脚本不一致，Flyway会报错并停止项目执行。
4. 如果校验通过，则根据表中的sql记录最大版本号，忽略所有版本号不大于该版本的脚本。再按照版本号从小到大，逐个执行其余脚本。

### 相关规范
SQL文件的命名需要遵从一定的规范，否则运行的时候会报错。

1. 仅需要执行一次的SQL： V开头，后面跟上数字，数字之间可以是"."或者“_"分开。然后再以两个下划线分割。eg: `V1__init.sql`
2. 可重复运行的SQL，以”R“开头，后面再跟两个下划线。eg: `R__init_redo.sql`

其中，V 开头的 SQL 执行优先级比 R 开头的 SQL 执行优先级高。

## 集成的步骤

### 第一步：引入依赖包

pom 文件

```xml
<!--  数据链接  -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.29</version>
    <scope>runtime</scope>
</dependency>

<!--   flyway 依赖  -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<!--  引入 flyway-mysql依赖，处理对 mysql 8.0 的支持  -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
    <version>8.5.7</version>
</dependency>
```

### 第二步：增加配置文件内容
```yml
spring:
  # 数据库连接配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://192.168.152.129:3306/study?characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: admin
    password: 123456

  ## flyway 配置
  flyway:
    # 是否启用flyway
    enabled: true
    # 编码格式，默认UTF-8
    encoding: UTF-8
    # 迁移sql脚本文件存放路径，默认db/migration
    locations: classpath:db/migration
    # 迁移sql脚本文件名称的前缀，默认V
    sql-migration-prefix: V
    # 迁移sql脚本文件名称的分隔符，默认2个下划线__
    sql-migration-separator: __
    # 迁移sql脚本文件名称的后缀
    sql-migration-suffixes: .sql
    # 迁移时是否进行校验，默认true
    validate-on-migrate: true
    # 当迁移发现数据库非空且存在没有元数据的表时，自动执行基准迁移，新建 schema_version 表
    baseline-on-migrate: true
```

### 第三步：创建脚本迁移文件

在工程 resources 文件夹下创建文件夹：`db/migration/`, 这个路径是用来存放我们数据库脚本的。

在 `db/migration/` 路径下，再去创建文件夹不会影响 flyway 对 sql 的识别与执行。

### 第四步：增加 sql 文件，执行

在 `db/migration/` 路径下，增加我们需要执行的 sql 脚本，


### 错误记录

在程序启动的时候发现有一个报错：
```consle
Caused by: org.flywaydb.core.api.FlywayException: Unsupported Database: MySQL 8.0
```

这里引入新的依赖就可以了
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
    <version>8.5.7</version>
</dependency>
```