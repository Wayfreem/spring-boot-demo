## 说明

我们在最开始学 J2EE 的时候，就会涉及到 servlet 中的 filter 配置。这里就结合 SpringBoot 来看下是怎么集成使用的。

## 具体集成步骤

### 第一步：依赖包

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 第二步：