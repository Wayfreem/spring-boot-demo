## 说明

我们在最开始学 J2EE 的时候，就会涉及到 servlet 中的 listener 配置。这里就结合 SpringBoot 来看下是怎么集成使用的。

在具体的集成中有两种方式可以做集成：

- 继承 ServletRequestListener、ServletContextListener、HttpSessionListener 接口来实现，然后通过 `ServletListenerRegistrationBean` 注入到 Spring 容器中
- 使用 `@WebListener` 注解方式

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

### 第一步：增加 自定义的 Listener

由于监听听 listener 有不同的作用域，
- request 当前的请求的访问内可以访问
- session 当前的会话可以访问
- servlet 就是存在服务端中，服务端可以访问
这个感觉和我们之前在学习 JSP 的时候一样，作用域的范围不同，所以我们就建立多个监听器

**TestContextListener**  用于监听程序加载与销毁

```java
public class TestContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(TestContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("程序加载中 。。。。");

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("程序销毁中 。。。。");
    }
}
```

**TestRequestListener** 用于监听当前的请求

```java
public class TestRequestListener implements ServletRequestListener {

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        System.out.println("requestDestroyed" + "," + new Date());
        System.out.println("当前訪问次数：" + servletRequestEvent.getServletContext().getAttribute("count"));
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        System.out.println("requestInitialized" + "," + new Date());
        Object count = servletRequestEvent.getServletContext().getAttribute("count");

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequestEvent.getServletRequest();
        httpServletRequest.getSession();        // 触发 session 操作

        Integer cInteger = 0;
        if (count != null) {
            cInteger = Integer.valueOf(count.toString());
        }
        System.out.println("历史訪问次数：：" + count);
        cInteger++;
        servletRequestEvent.getServletContext().setAttribute("count", cInteger);
    }
}
```

**TestSessionListener** 用于当前会话监听

```java
public class TestSessionListener implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent arg0) {
        System.out.println("sessionCreated" + "," + new Date());
        Object lineCount = arg0.getSession().getServletContext().getAttribute("lineCount");
        Integer count = 0;
        if (lineCount == null) {
            lineCount = "0";
        }
        count = Integer.valueOf(lineCount.toString());
        count++;
        System.out.println("新上线一人，历史在线人数：" + lineCount + "个,当前在线人数有： " + count + " 个");
        arg0.getSession().getServletContext().setAttribute("lineCount", count);
    }

    public void sessionDestroyed(HttpSessionEvent arg0) {
        System.out.println("sessionDestroyed" + "," + new Date());
        Object lineCount = arg0.getSession().getServletContext().getAttribute("lineCount");
        Integer count = Integer.valueOf(lineCount.toString());
        count--;
        System.out.println("一人下线。历史在线人数：" + lineCount + "个，当前在线人数: " + count + " 个");
        arg0.getSession().getServletContext().setAttribute("lineCount", count);
    }
}
```

### 第二步：将自定义的 Filter 注入到 Spring 的容器中

```java
@Configuration
public class ListenerRegistry {

    @Bean
    public ServletListenerRegistrationBean<?> userContextListener(){
        ServletListenerRegistrationBean<TestContextListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new TestContextListener());
        return bean;
    }

    @Bean
    public ServletListenerRegistrationBean<TestRequestListener> userRequestListener(){
        ServletListenerRegistrationBean<TestRequestListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new TestRequestListener());
        return bean;
    }

    @Bean
    public ServletListenerRegistrationBean <TestSessionListener> userSessionListener(){
        ServletListenerRegistrationBean<TestSessionListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new TestSessionListener());
        return bean;
    }
}
```

### 第三步：增加 controller

```java
@RestController
public class ListenerController {

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

```

## 使用 `@WebListener` 注解方式

### 第一步：新增注解式的 Listener
```java
@WebListener
public class AnnotationServletListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("WebListener.UserListener ---->>>  ServletContext 初始化 ");
    }

    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("WebListener.UserListener ---->>>  ServletContext 销毁 ");
    }
}
```

### 第二步：开启对 `webServlet` 的支持

增加 `@ServletComponentScan` 支持

```java
@ServletComponentScan       // 开启对 webServlet 支持
@SpringBootApplication
public class FilterApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilterApplication.class, args);
    }
}
```

### 测试

按照上面的请求测试就好