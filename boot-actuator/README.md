[推荐一个不错的链接](https://blog.csdn.net/Nicholas_GUB/article/details/121434730)

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

浏览器访问值
```json
{
  "_links":{
    "self":{
      "href":"http://localhost:8080/actuator",
      "templated":false
    },
    "health":{
      "href":"http://localhost:8080/actuator/health",
      "templated":false
    },
    "health-path":{
      "href":"http://localhost:8080/actuator/health/{*path}",
      "templated":true
    }
  }
}
```

到这里其实就是表示已经集成完成了。下面是介绍其他的知识点。

### 开放所有端点

使用 `management.endpoints.web.exposure.include=*` 开放所有的端点

```yml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

再次访问

```http request
localhost:8080/actuator
```

浏览器输出

```json5
{
  "_links":{
    "self":{
      "href":"http://localhost:8080/actuator",
      "templated":false
    },
    "beans":{
      "href":"http://localhost:8080/actuator/beans",
      "templated":false
    },
    "caches-cache":{
      "href":"http://localhost:8080/actuator/caches/{cache}",
      "templated":true
    },
    "caches":{
      "href":"http://localhost:8080/actuator/caches",
      "templated":false
    },
    "health-path":{
      "href":"http://localhost:8080/actuator/health/{*path}",
      "templated":true
    },
    "health":{
      "href":"http://localhost:8080/actuator/health",
      "templated":false
    },
    "info":{
      "href":"http://localhost:8080/actuator/info",
      "templated":false
    },
    "conditions":{
      "href":"http://localhost:8080/actuator/conditions",
      "templated":false
    },
    "configprops":{
      "href":"http://localhost:8080/actuator/configprops",
      "templated":false
    },
    "configprops-prefix":{
      "href":"http://localhost:8080/actuator/configprops/{prefix}",
      "templated":true
    },
    "env":{
      "href":"http://localhost:8080/actuator/env",
      "templated":false
    },
    "env-toMatch":{
      "href":"http://localhost:8080/actuator/env/{toMatch}",
      "templated":true
    },
    "loggers":{
      "href":"http://localhost:8080/actuator/loggers",
      "templated":false
    },
    "loggers-name":{
      "href":"http://localhost:8080/actuator/loggers/{name}",
      "templated":true
    },
    "heapdump":{
      "href":"http://localhost:8080/actuator/heapdump",
      "templated":false
    },
    "threaddump":{
      "href":"http://localhost:8080/actuator/threaddump",
      "templated":false
    },
    "metrics":{
      "href":"http://localhost:8080/actuator/metrics",
      "templated":false
    },
    "metrics-requiredMetricName":{
      "href":"http://localhost:8080/actuator/metrics/{requiredMetricName}",
      "templated":true
    },
    "scheduledtasks":{
      "href":"http://localhost:8080/actuator/scheduledtasks",
      "templated":false
    },
    "mappings":{
      "href":"http://localhost:8080/actuator/mappings",
      "templated":false
    }
  }
}
```

相关说明

| No.     | endpoint       | 描述                                       |
|:--------|:---------------|:-----------------------------------------|
| 1       | beans          | 注册到 Spring 容器中的 Bean 对象集合                |
| 2       | caches         | 缓存信息                                     |
| 3       | health         | 应用的健康状态                                  |
| 4       | info           | 应用的基本信息，需要手工配置                           |
| 5       | conditions     | 自动配置生效的条件                                |
| 6       | configprops    | 获取所有的配置属性                                |
| 7       | auditevents    | 显示应用暴露的审计事件 (比如认证进入、订单失败)                |
| 8       | metrics        | 应用多样的度量信息                                |
| 9       | loggers        | 日志配置                                     |
| 10      | httptrace      | HTTP 足迹，显示最近 100个 HTTP request/repsponse |
| 11      | env            | 当前的环境特性                                  |
| 12      | flyway         | 显示数据库迁移路径的详细信息                           |
| 13      | shutdown       | 关闭应用                                     |
| 14      | mappings       | 所有的 @RequestMapping 路径                   |
| 15      | scheduledtask  | 应用中的调度任务                                 |
| 16      | threaddump     | 线程信息                                     |
| 17      | heapdump       | JVM 堆 dump                               |

### 指定开放端点

```properties
management.endpoint.<NAME>.enabled=true
management.endpoint.<NAME>.enabled=false
```
我在测试的时候发现，需要先开启所有的端点才能去关闭其中的端点，例如

```yml
management:
  endpoints:
    web:
      exposure:
        include: "*"

  ## 通过开发指定的端点
  ### 开放  management.endpoint.<NAME>.enabled=true
  ### 关闭  management.endpoint.<NAME>.enabled=false
  endpoint:
    info:
      enabled: false
```

当设置 info 断点为关闭的时候，再去访问下 `localhost:8080/actuator/info` 可以发现，已经访问不了了。

### 服务信息  InfoContributor

这里需要在开放 info 断点的前提下测试。

我们可以通过实现 `InfoContributor` 接口来实现自定义的服务信息返回

```java
@Component
public class ServerInfoContributor implements InfoContributor {


    @Override
    public void contribute(Info.Builder builder) {
        // 这里可以访问数据库读取数据库中的信息
        builder.withDetail("date", LocalDateTime.now());
    }
}
```

访问

```http request
localhost:8080/actuator/info
```

### 健康检查 HealthIndicator

actuator 提供了健康断言接口 HealthIndicator，实现此接口可以编写服务健康的判断逻辑。

```java
@Component
public class DefaultHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean isHealth = true;
        // 此处应判断各个组件是否连接正常,其中一个连接异常则设置isHealth = false
        isHealth = false; // 假如redis连接异常
        String errorMsg = "redis连接异常";
        if(!isHealth) {
            return Health.down().withDetail("message",errorMsg).build();
        }
        return Health.up().build();
    }
}
```

修改application.yml文件，显示不健康时的异常信息。

```yml
management:
  endpoint:
    health:
      show-details: always # 总是显示异常信息
```

访问：`http://localhost:8080/actuator/health`, 此时 status 的值为down，服务是不健康的并且在details中显示了异常原因。


### heapdump 信息
SpringBoot程序运行在JVM上，是JVM的一个执行线程，Actuator提供了 heapdump 端点可以获取到 Springboot 所运行 JVM 的实时情况。

访问：`http://localhost:8080/actuator/heapdump`, 可进行 dump 文件的下载。

使用 VisualVm 可以打开dump文件，在 jdk6 到 jdk8 中，VisualVm 是默认集成的，从jdk9开始，jdk 默认不再集成 VisualVm，需要到 github下载安装[（下载地址）](https://visualvm.github.io/download.html)。

打开 VisualVm，点击左上角 `file -> load` 导入 dump 文件即可查看 jvm 运行信息（导出 dump 文件时刻的 jvm 信息）。


### 自定义 Endpoint

Actuator 提供有 @Endpoint 注解帮助开发者定义自己的 Endpoint，自定义端点的请求方式也分为以下三种：

| No.  | 注解                 | 请求方式     | 描述     |
|:-----|:-------------------|:---------|:-------|
| 1    | @ReadOperation     | get      | 读取数据   |
| 2    | @WriteOperation    | get      | 写入数据   |
| 3    | @DeleteOperation   | get      | 删除数据   |

新建 SelfEndpoint 类，添加 @Endpoint 注解声明 endpoint 并命名。
```java
@Configuration
@Endpoint(id = "self")
public class SelfEndpoint {

    @ReadOperation
    public Map<String, Object> endpoint(@Selector String selector) { // 接收参数
        Map<String, Object> endpointMap = new HashMap<>();
        endpointMap.put("author", "NicholasGUB");
        endpointMap.put("selector", selector);
        return endpointMap;
    }
}
```