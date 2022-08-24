## 简介

这里是通过使用 `@Configration` 注解以及结合自定义 `@Enable` 注解，来实现自定义模块装配

## 说明

我们可以通过 `@EnableWebMvc` 来先了解下大致的内容

```java
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE)
@Documented 
@Import(DelegatingWebMvcConfiguration.class) 	// 这里之直接引入 DelegatingWebMvcConfiguration 类
public @interface EnableWebMvc {
}
```

接下来，我们看下 `DelegatingWebMvcConfiguration`

```java
@Configuration
public class DelegatingWebMvcConfiguration extends
 WebMvcConfigurationSupport {
 ...
}
```

