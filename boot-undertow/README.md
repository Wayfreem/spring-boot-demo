## 简介

通过 spring boot 集成 undertow 作为 web 服务器来使用，替换传统的 tomcat 作为服务器。

### undertow

Undertow 是红帽公司开发的一款基于 NIO 的高性能 Web 嵌入式服务器。

Undertow 的特点：
- 轻量级：它是一个 Web 服务器，但不像传统的 Web服务器有容器的概念，它是由两个核心 Jar 包组成，加载一个 Web 应用可以小于 10MB。
- Servlet3.1 支持：它提供了对 Servlet3.1 的支持
- WebSocket 支持：对 Web Socket 完全支持，用以满足 Web 应用巨大数量的客户端
- 嵌套性：它不需要容器，只需通过 API 即可快速搭建 Web 服务器

默认情况下 Spring Cloud 使用 Tomcat 作为内嵌 Servlet 容器，可启动一个 Tomcat 的 Spring Boot 程序与一个 Undertow 的 Spring Boot 程序。

通过 VisualVM 工具进行比较，可看到 Undertow 性能优于 Tomcat

## 集成的步骤

### 添加依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>
```

### 配置文件

```yaml
server:
  port: 8080
  servlet:
    context-path: /
  http2:
    enabled: true
    use-forward-headers: true
  undertow:
    accesslog:
      dir: log/undertow/ # Undertow 日志存放目录
      # 是否启动日志
      enabled: false
      # 日志格式
      pattern: common
      # 日志文件名前缀
      prefix: access_log
      # 日志文件名后缀
      suffix: log
      # HTTP POST请求最大的大小
    max-http-post-size: 0
    # 以下的配置会影响buffer,这些buffer会用于服务器连接的IO操作,有点类似netty的池化内存管理
    # 每块buffer的空间大小,越小的空间被利用越充分
    buffer-size: 1024
    # 是否分配的直接内存(NIO直接分配的堆外内存)
    direct-buffers: true
    threads:
      # 设置IO线程数, 它主要执行非阻塞的任务,它们会负责多个连接, 默认设置每个CPU核心一个线程
      io: 2
      # 阻塞任务线程池, 当执行类似servlet请求阻塞操作, undertow会从这个线程池中取得线程,它的值设置取决于系统的负载
      worker: 256
```

到此呢，程序就可以正常启动了。后面介绍下其他的配置相关.

### 额外的配置信息

我们创建一个 config 类来做自定义配置

```java
@Configuration
public class UndertowServletWebServerConfig implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

    /**
     * 缓存区设置
     * @param factory UndertowServletWebServerFactory
     */
    @Override
    public void customize(UndertowServletWebServerFactory factory) {
        factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
            webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(false, 1024));
            deploymentInfo.addServletContextAttribute("io.undertow.websockets.jsr.WebSocketDeploymentInfo", webSocketDeploymentInfo);
        });
    }

    /**
     * 增加额外的配置
     * @return UndertowServletWebServerFactory
     */
    @Bean
    public UndertowServletWebServerFactory undertowServletWebServerFactory() {
        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
        factory.addBuilderCustomizers(
                // 这里可以增加其他的配置信息
                builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                        .setServerOption(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH, true));

        return factory;
    }
}
```


### 通过 web 访问

由于是 web 容器，我们肯定是想通过链接访问，所以增加一个 controller
```java
@RestController
public class TestController {

    @RequestMapping("get")
    public Map get(){
        return Map.of("key", "No.1");
    }
}
```

**访问**

```http request
http://localhost:8080/get
```


### 参考连接

[spring boot内置容器性能比较(Jetty、Tomcat、Undertow)](https://blog.csdn.net/syx1065001748/article/details/98883727)