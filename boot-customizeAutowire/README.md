## 工程说明

这里是自定义个 spring boot starter，更好的掌握 spring boot starter 的知识。了解 spring boot starter 的加载机制。


## 原理

在Spring 框架中有一个 `SpringFactoriesLoader.class`, 这个类会去加载项目工程路径 `META-INF` 下的 `spring.factories` 文件， 

```java
public final class SpringFactoriesLoader {
    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
    private static final Log logger = LogFactory.getLog(SpringFactoriesLoader.class);
    static final Map<ClassLoader, Map<String, List<String>>> cache = new ConcurrentReferenceHashMap();

    public static <T> List<T> loadFactories(Class<T> factoryType, @Nullable ClassLoader classLoader) {
        // 省略....
    }
    // 省略......
}
```

通过上面的 `loadFactories` 的方法进行装载我们需要的配置 bean


## 集成说明

这里采用最简单的集成方式来说明

- 增加一个配置类，配置类中使用 `@Bean` 注解
- 增加 `META-INF/spring.factories` 文件


## 第一步：创建配置类

`HelloWorldConfiguration`，这里的配置类可以不用标注 `@Configuration` 注解

```java
public class HelloWorldConfiguration {

    @Bean
    public String helloWorld(){
        return "hell world 2022";
    }
}
```


### 第二步：创建 factories 文件

在 resources 文件下创建 一个 META-INF 的文件夹，然后再 META-INF 文件夹里面创建 spring.factories 文件，这里是告诉程序自动装配的地址

```factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.demo.customizeAutowire.HelloWorldConfiguration
```


### 第三步：启动类修改

在启动类中需要增加注解 `@EnableAutoConfiguration`，开启自动装配

```java
@EnableAutoConfiguration
public class CustomizeAutowireApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(CustomizeAutowireApplication.class)
                .web(WebApplicationType.NONE).run(args);


        // 获取 Bean
        String hellworld = context.getBean("helloWorld", String.class);
        System.out.println("hello world " + hellworld);

        context.close();
    }
}
```

然后启动测试就好。

#### 补充

对于自动装配机制，其实是可以和 条件注入 `boot-conditionAutowire` 与 `boot-enableAutowire` 里面的内容相结合的，这样子实现的自动注入更加灵活。