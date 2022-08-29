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

在 resources 文件下创建 一个 META-INF 的文件夹，然后再 META-INF 文件夹里面创建 spring.factories 文件，这里是告诉程序自动装配的地址

```factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.demo.configuration.HelloWorldAutoConfiguration
```