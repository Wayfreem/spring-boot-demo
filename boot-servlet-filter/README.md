## 说明

我们在最开始学 J2EE 的时候，就会涉及到 servlet 中的 filter 配置。这里就结合 SpringBoot 来看下是怎么集成使用的。

在具体的集成中有两种方式可以做集成：

- 使用 FilterRegistrationBean 将我们自定义的 Filter 注入进去
- 使用 `@WebFilter` 注解方式

## 依赖包

这里只需要在 pom 文件中引入基础的 spring web 依赖包就好

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


## FilterRegistrationBean 集成

### 第一步：增加 自定义的 Filter

```java

```