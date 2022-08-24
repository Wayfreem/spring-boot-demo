## 简介

这里是通过使用 `@Configration` 注解以及结合自定义 `@Enable` 注解，来实现自定义模块装配。就是通过注解驱动注入的方式

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

## 具体步骤

### 第一步：创建一个配置类，增加我们对应的 bean

```java
 //实体bean
@Configuration
public class HelloWorldConfiguration {
    @Bean
    public String helloWorld(){
        return "hell world 2022";
    }
}
```

### 第二步：创建一个 注解类
```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(HelloWorldConfiguration.class)
public @interface EnableHelloWorld {
}
```


### 第三步：修改引导类的写法

```java
@EnableHelloWorld
public class EnableAutowireApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext content = new SpringApplicationBuilder(EnableAutowireApplication.class)
                .web(WebApplicationType.NONE).run(args);
        String hellWorld  =content.getBean("helloWorld",String.class);
        System.out.println("hello world" + hellWorld);
        content.close();
    }
}
```

程序启动之后，就可以在控制台看到内容了

```
hello worldhell world 2022
```

### 引导类说明

我们知道如果是按照传统的 SpringBoot 项目，程序自动去扫描我们使用了 `@Configration` 注解的类，把里面的 `@Bean` 的内容注入到Spring容器中（新增一个容器的 bean）。
关于这个具体的实例，可以看下 `taskSchedule` 中的内容。

所以我们需要修改下引导类的写法，如上面的写法所示。

```java
@SpringBootApplication
public class EnableAutowireApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnableAutowireApplication.class, args);
    }
}
```