## 简介

spring boot集成 websocket 项目。

## 集成的步骤

### 第一步：引入依赖

pom.xml

```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

### 第二步：创建 配置文件类
```java
@Configuration
public class WebSocketConfig {

    /**
     * 注入一个 ServerEndpointExporter,
     * 该 Bean会自动注册使用 @ServerEndpoint 注解声明的 websocket endpoint
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
```

### 第三步：接受消息