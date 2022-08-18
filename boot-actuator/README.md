## 简介

spring boot 中集成 actuator 实现监控，运维管理

## 集成步骤

### 增加依赖

pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
 ```

### 访问

浏览器访问

```http request
localhost:8080/actuator
```

### 配置文件修改

使用 `management.endpoints.web.exposure.include=*` 开放所有的端点
```yml
management:
  endpoints:
    web:
      exposure:
        include: *
```