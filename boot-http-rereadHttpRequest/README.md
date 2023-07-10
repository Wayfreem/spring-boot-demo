## 说明

由于采用 `application/json` 传输参数时，HttpServletRequest只能读取一次 body 中的内容。因为是读的字节流，读完就没了，因此需要需要做特殊处理。下面介绍下如何重复读取~~

## 具体实现步骤

### 第一步：引入基础依赖

pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>

<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

### 第二步：自定义 HttpServletRequestWrapper 包装类

由于采用采用 `application/json` 传输参数时，HttpServletRequest 只能读取一次 body 中的内容。因为是读的字节流，读完就没了，因此需要需要做特殊处理。

为实现述多次读取 Request 中的 Body 内容，需继承 HttpServletRequestWrapper 类，读取 Body 的内容，然后缓存到 byte[] 中；这样就可以实现多次读取 Body 的内容了。

```java
public class RequestWrapper extends HttpServletRequestWrapper {

    // 参数字节数组
    private byte[] requestBody;

    // Http请求对象
    private HttpServletRequest request;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        /**
         * 每次调用此方法时将数据流中的数据读取出来，然后再回填到 InputStream 之中
         * 解决通过 @RequestBody 和 @RequestParam （POST方式） 读取一次后控制器拿不到参数问题
         */
        if (null == this.requestBody) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(request.getInputStream(), baos);   // 复制一份流内容
            this.requestBody = baos.toByteArray();
        }

        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }

            @Override
            public int read() {
                return bais.read();
            }
        };
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
```


### 第三步：增加过滤器，转换 request 为自定义的 request

这里新增 Filter 的逻辑可以去看下，`boot-servlet-filter` 工程。

```java
public class RequestBodyReaderFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            ServletRequest requestWrapper = null;
            if (request instanceof HttpServletRequest) {
                requestWrapper = new RequestWrapper((HttpServletRequest) request);      // 调用我们的的包装类
            }
            if (requestWrapper == null) {
                chain.doFilter(request, response);
            } else {
                chain.doFilter(requestWrapper, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
    }

}
```


### 第四步：注册 Filter

```java
@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<RequestBodyReaderFilter> requestBodyReaderFilter() {
        FilterRegistrationBean<RequestBodyReaderFilter> registrationBean = new FilterRegistrationBean<>();
        RequestBodyReaderFilter filter = new RequestBodyReaderFilter();
        registrationBean.setFilter(filter);     // 注册 Filter

        ArrayList<String> urls = new ArrayList<>();
        urls.add("/*"); //配置过滤规则
        registrationBean.setUrlPatterns(urls);
        registrationBean.setOrder(3);
        return registrationBean;

    }
}
```

### 测试

#### 增加工具类

将 HttpServletRequest 中的 body 内容转为 string 格式

```java
public class RequestReadUtils {
    private static final int BUFFER_SIZE = 1024 * 8;

    public static String read(HttpServletRequest request) throws IOException {
        BufferedReader bufferedReader = request.getReader();
        for (Enumeration<String> iterator = request.getHeaderNames(); iterator.hasMoreElements();) {
            String type = iterator.nextElement();
            System.out.println(type+" = "+request.getHeader(type));
        }
        System.out.println();
        StringWriter writer = new StringWriter();
        write(bufferedReader,writer);
        return writer.getBuffer().toString();
    }

    public static long write(Reader reader,Writer writer) throws IOException {
        return write(reader, writer, BUFFER_SIZE);
    }

    public static long write(Reader reader, Writer writer, int bufferSize) throws IOException {
        int read;
        long total = 0;
        char[] buf = new char[bufferSize];
        while( ( read = reader.read(buf) ) != -1 ) {
            writer.write(buf, 0, read);
            total += read;
        }
        return total;
    }
}
```

#### 创建一个 Controller

```java
@RestController
public class TestController {

    @RequestMapping("test")
    public String test(HttpServletRequest request) throws IOException {
        String body = IOUtils.toString(request.getInputStream(), Charset.defaultCharset());
        System.out.println(body);
        return "请求成功";
    }

    @RequestMapping("testRepeat")
    public String testRepeat(HttpServletRequest request) throws IOException {
        // 先读取一次 request 中的内容
        String body = RequestReadUtils.read(request);
        System.out.println(body);

        // 使用 IOUtils 包中的方法读取
        String data = IOUtils.toString(request.getInputStream(), Charset.defaultCharset());
        System.out.println(data);
        return "请求成功";
    }
}
```

#### 通过 HTTP 请求

修改下 filter 中的判断，请求就可以看到效果了

```java
if (requestWrapper == null) {
    chain.doFilter(request, response);
} else {
    chain.doFilter(request, response);

    // 用于测试重复读取 chain.doFilter(request, response);
}
```

```http request
http://localhost:8080/testRepeat

{"orderNo":"0002035","customerId":"125435","status":"1"}
```
