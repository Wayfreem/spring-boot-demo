
## 简介
Spring 通过任务执行器（TaskExecutor）来实现多线程和并发编程。使用 ThreadPoolTaskExecutor 可实现一个基于线程池的 TaskExecutor。而实际开发中任务一般是非阻碍的，即异步的，所以我们要在配置类中通过 @EnableAsync 开启对异步任务的支持，并通过在实际执行的 Bean 的方法中使用 @Async 注解来声明其是一个异步任务。

## 代码示例

**配置类**

在配置类中，利用 `@EnableAsync` 注解，开启对异步任务支持。配置类实现 `AsyncConfigurer` 接口，并重写 `getAsyncExecutor()` 方法，返回一个 `ThreadPoolTaskExecutor`，这样子我们就获得了一个基于线程池的 `TaskExecutor` 。

```java
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration  // 声明是一个配置类
@EnableAsync    //开启异步的支持
public class TaskExecutorConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {    //配置类实现 AsyncConfigurer 接口，重写 getAsyncExecutor， 通过配置返回一个 taskExecutor 线程池
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(25);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
```

注意：上面的例子中，ThreadPoolTaskExecutor并没有被Spring容器管理，可以在getAsyncExecutor() 上添加@Bean注解让它变成Spring管理的Bean。如果加入到Spring容器，那么就不需要手动调用executor.initialize() 做初始化了，因为在Bean初始化的时候会自动调用这个方法。


**任务执行类**

通过 `@Async` 注解表明该方法是个异步方法，如果注解在 class 上（类级别上），则表明该类所有的方法都是异步方法，而这里的方式是自动被注入使用 ThreadPoolTaskExecutor 作为 TaskExecutor,
```java
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncTaskService {

    @Async // 通过 Async 注解表明该方法是一个异步的方法，如果注解在类级别，则表明该类下的所有方法都是 异步
    public void executeAsyncTask(Integer i){
        System.out.println("执行异步任务: "+i);
    }

    @Async
    public void executeAsyncTaskPlus(Integer i){
        System.out.println("执行异步任务+1: "+(i+1));
    }
}
```

**任务运行类**

```
@RestController
public class TestController {

    @Autowired
    private AsyncTaskService asyncTaskService;

    @RequestMapping("test")
    public Map test(){
        for(int i =0 ;i<10;i++){
            asyncTaskService.executeAsyncTask(i);
            asyncTaskService.executeAsyncTaskPlus(i);
        }
        return Map.of("msg", "success");
    }
}
```

**通过访问测试**

执行结果如下：
```
执行异步任务: 0
执行异步任务: 1
执行异步任务: 3
执行异步任务: 2
执行异步任务: 4
执行异步任务+1: 2
执行异步任务: 5
执行异步任务+1: 6
执行异步任务: 6
执行异步任务+1: 7
执行异步任务: 7
执行异步任务+1: 8
执行异步任务: 8
执行异步任务+1: 9
执行异步任务: 9
执行异步任务+1: 10
执行异步任务+1: 5
执行异步任务+1: 4
执行异步任务+1: 3
执行异步任务+1: 1
```


## 观察异步执行情况

目的：为了更好的观察到是异步执行，所在程序上面增加 `sleep()` ，方便于观察

在 `AsyncTaskService` 的方法中增加 `sleep()`

```java
@Service
public class AsyncTaskService {

    @Async // 通过 Async 注解表明该方法是一个异步的方法，如果注解在类级别，则表明该类下的所有方法都是 异步
    public void executeAsyncTask(Integer i) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("执行异步任务: "+i);
    }

    @Async
    public void executeAsyncTaskPlus(Integer i) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("执行异步任务+1: "+(i+1));
    }
}
```

我们再次访问的时候，就可以看到前端已经返回对应的消息，控制台就慢慢的打印执行消息。