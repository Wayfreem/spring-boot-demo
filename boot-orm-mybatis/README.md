
## 简介
使用 spring boot 集成 mybatis 对数据库进行操作。

既然是需要连接数据库，就需要安装 MySQL(采用docker 安装) [安装参考链接](https://blog.csdn.net/qq_18948359/article/details/125486934?spm=1001.2014.3001.5502)

## 集成的步骤

pom 文件
```xml

 <dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
</dependency>
```