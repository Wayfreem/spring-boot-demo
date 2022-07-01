## 简介

使用 JPA 链接MySQL 数据库。

## 具体实现

### 第一步：集成相关依赖

**pom.xml**

集成JPA、spring boot、mysql 相关的依赖包

```xml
<dependencies>
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter</artifactId>
   </dependency>

   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
   </dependency>

   <!--  jpa 依赖     -->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-jpa</artifactId>
   </dependency>

   <dependency>
       <groupId>mysql</groupId>
       <artifactId>mysql-connector-java</artifactId>
       <version>8.0.29</version>
       <scope>runtime</scope>
   </dependency>
</dependencies>
```

### 第二步：创建模型以及 repository

