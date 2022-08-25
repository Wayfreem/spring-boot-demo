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

### 注解驱动

#### 第一步：创建一个配置类，增加我们对应的 bean

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

#### 第二步：创建一个 注解类
```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(HelloWorldConfiguration.class)
public @interface EnableHelloWorld {
}
```


#### 第三步：修改引导类的写法

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

#### 引导类说明

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

### 基于接口驱动实现

#### 第一步：创建选择器导入类
这里基于之前的源码结构来做实现，需要新增一个导入 Bean 的类：

```java
/**
 * 主要是实现 ImportSelector 类用来做导入
 */
public class HelloWorldImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{HelloWorldConfiguration.class.getName()};       // 这个地方返回我们具体的 config类
    }

}
```

#### 第二步：修改 @Enable 类

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(HelloWorldImportSelector.class)     // 修改点在这里
public @interface EnableHelloWorld {
}
```

#### 启动程序测试

我们可以在程序启动的时候增加 `HelloWorldImportSelector` 断点，用来测试。控制台输出
```shell
hello worldhell world 2022
```
