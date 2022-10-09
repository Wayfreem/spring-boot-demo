
## 链接收集

- [官网地址](https://github.com/spring-projects/spring-retry)

- [博文视点 CSDN](https://blog.csdn.net/broadview2006/article/details/80129764?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-80129764-blog-106751310.pc_relevant_3mothn_strategy_recovery&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-80129764-blog-106751310.pc_relevant_3mothn_strategy_recovery&utm_relevant_index=2)

## 说明

Spring Retry 为 Spring 应用程序提供了声明性重试支持。

在日常开发工作中，总会遇到存在有调用一个接口或者服务方法报错，然后需要重新调用该接口或者服务方法的情况。

常用的方法就是使用 `try{}catch{}` 或者 `while` 循环之类的语法来进行相应的操作。但是这个不是一个好的处理方案，不能做到统一管理。这时，我们可以使用 `Spring Retry` 来实现。

在项目中使用 `Spring Retry` 框架，有两种方式

- 注解方式：使用 `@Retryable`
- 声明方式：使用 `RetryTemplate`

## `@Retryable` 集成说明

项目这里主要是使用注解 `@Retryable` 方式实现

### 第一步：引入依赖

```xml
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

### 第二步：启用 `@Retryable`

```java
@EnableRetry
@SpringBootApplication
public class RetryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetryApplication.class, args);
    }
}
```


### 第三步: 创建一个 controller

```java
@RestController
public class RetryController {

    @Autowired
    private RetryService retryService;

    @RequestMapping("test")
    public int test(int code){
        return retryService.retry(code);
    }
}
```

### 第四步：创建 service

```java
@Service
public class RetryService {

    /**
     * value：抛出指定异常才会重试
     * include：和 value 一样，默认为空，当 exclude 也为空时，默认所有异常
     * exclude：指定不处理的异常
     * maxAttempts：最大重试次数，默认3次
     * backoff：重试等待策略，
     * 默认使用 @Backoff，@Backoff 的 value 默认为 1000L，我们设置为 2000； 以毫秒为单位的延迟（默认 1000）
     * multiplier（指定延迟倍数）默认为 0，表示固定暂停 1秒后进行重试，如果把 multiplier 设置为 1.5，则第一次重试为 2秒，第二次为 3秒，第三次为4.5秒。
     *
     * @param code 调用参数
     * @return code
     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public int retry(int code) {
        System.out.println("调用 retry() ，时间：" + LocalTime.now());
        if (code == 0) {
            throw new RuntimeException("调用失败！");
        }
        System.out.println("正常调用成功");

        return 200;
    }

    /**
     * Spring-Retry 还提供了 @Recover 注解，用于 @Retryable 重试失败后处理方法。
     * 如果不需要回调方法，可以直接不写回调方法，那么实现的效果是，重试次数完了后，如果还是没成功没符合业务判断，就抛出异常。
     * 可以看到传参里面写的是 RuntimeException e，这个是作为回调的接头暗号（重试次数用完了，还是失败，我们抛出这个 RuntimeException e通知触发这个回调方法）。
     * 注意事项：
     * 方法的返回值必须与 @Retryable 方法一致
     * 方法的第一个参数，必须是 Throwable 类型的，建议是与 @Retryable 配置的异常一致，其他的参数，需要哪个参数，写进去就可以了（ @Recover方 法中有的）
     * 该回调方法与重试方法写在同一个实现类里面
     * <p>
     * 由于是基于AOP实现，所以不支持类里自调用方法
     * 如果重试失败需要给 @Recover 注解的方法做后续处理，那这个重试的方法不能有返回值，只能是 void
     * 方法内不能使用try catch，只能往外抛异常
     * </p>
     *
     * @param e    Exception
     * @param code 调用参数
     * @return int
     * @Recover 注解来开启重试失败后调用的方法(注意, 需跟重处理方法在同一个类中)，此注解注释的方法参数一定要是 @Retryable 抛出的异常，否则无法识别，可以在该方法中进行日志处理。
     */
    @Recover
    public int recover(Exception e, int code) {
        System.out.println("回调方法执行！！！！");
        //记日志到数据库 或者调用其余的方法
        System.out.println("异常信息:" + e.getMessage());
        return 400;
    }
}
```

### 测试

启动服务之后测试

```http request
http://localhost:8080/test?code=0
```

控制台输出

```console
调用 retry() ，时间：11:50:32.170148
调用 retry() ，时间：11:50:34.181175700
调用 retry() ，时间：11:50:37.194717100
回调方法执行！！！！
异常信息:调用失败！
```


