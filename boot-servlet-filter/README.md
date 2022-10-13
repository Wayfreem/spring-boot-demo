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

**TestAllFilter**  用于作为通用的拦截器

```java
public class TestAllFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        System.out.println("自定义过滤器 TestAllFilter 加载，拦截 init。。。" );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        System.out.println("自定义过滤器 TestAllFilter 触发，拦截url:" + request.getRequestURI());
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
```

**TestSingleFilter** 用作为单个请求的拦截器

```java
public class TestSingleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        System.out.println("自定义过滤器 TestSingleFilter 加载，拦截 init。。。" );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        System.out.println("自定义过滤器 TestSingleFilter 触发，拦截 url:" + request.getRequestURI());
        filterChain.doFilter(servletRequest, servletResponse);  // 执行后续的 filter
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
```

### 第二步：将自定义的 Filter 注入到 Spring 的容器中

```java
@Configuration
public class ServletRegistry {

    @Bean
    public FilterRegistrationBean<TestAllFilter> userFilterRegistry() {
        FilterRegistrationBean<TestAllFilter> bean = new FilterRegistrationBean<>();

        bean.setFilter(new TestAllFilter());       //注册自定义过滤器
        bean.setName("TestAllFilter");     //过滤器名称
        bean.addUrlPatterns("/*");  //过滤所有路径
        bean.setOrder(1);           //优先级，最顶级
        return bean;
    }

    @Bean
    public FilterRegistrationBean<TestSingleFilter> userTestFilterRegistry() {
        FilterRegistrationBean<TestSingleFilter> bean = new FilterRegistrationBean<>();

        bean.setFilter(new TestSingleFilter());       //注册自定义过滤器
        bean.setName("TestSingleFilter");     //过滤器名称
        bean.addUrlPatterns("/getUser/*");      //过滤所有路径
        bean.setOrder(6);               //优先级，越低越优先
        return bean;
    }

}
```

### 第三步：增加 controller

```java
@RestController
public class FilterController {

    @RequestMapping("find")
    public String find(){
        return "查询所有";
    }

    @RequestMapping("getUser")
    public Map getUser(String id){
       return Map.of("id", id);
    }
}
```

### 测试

**程序启动时** 控制台中输出如下

```console
自定义过滤器 TestAllFilter 加载，拦截 init。。。
自定义过滤器 TestSingleFilter 加载，拦截 init。。。
```

**请求访问时** 控制台输出

当请求地址为：`http://localhost:8080/getUser?id=2`

```console
自定义过滤器 TestAllFilter 触发，拦截url:/getUser
自定义过滤器 TestSingleFilter 触发，拦截 url:/getUser
```

当请求地址为：`http://localhost:8080/find`

```console
自定义过滤器 TestAllFilter 触发，拦截url:/find
```

## 使用 `@WebFilter` 注解方式

