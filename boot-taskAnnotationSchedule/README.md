
## 简介
从 Spring 3.1 开始，计划任务在 Spring 中的实现变的异常简单。首先通过在配置类注解 `@EnableScheduling` 来开启对计划任务的支持，然后在要执行计划任务的方法上注解 `@Scheduled`， 声明这是一个计划任务。

Spring 通过 `@Scheduled` 支持多种类型的计划任务，包含 cron、fixDelay、fixRate 等。

## 示例

**计划任务执行类**

```java

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000) // 通过 Scheduled 声明该方法时计划任务，使用 fixedRate 属性每隔固定时间执行
    public void reportCurrentTime() {
        System.out.println("每隔五秒执行一次 " + dateFormat.format(new Date()));
    }

    @Scheduled(cron = "0 28 11 ? * *"  ) // 使用 crom属性可以按照指定的时间执行，例子上面是 每天的 11点28分执行
    public void fixTimeExecution(){
        System.out.println("在指定时间 " + dateFormat.format(new Date())+"执行");
    }
}

```

**配置类**

`@EnableScheduling` 注解开启对计划任务的支持

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling // 注解开启对计划任务的支持
public class TaskSchedulerConfig {
}
```


**运行类**

```
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(TaskSchedulerConfig.class);

    }
}
```

控制台输出结果如下：

```
每隔五秒执行一次 15:14:58
每隔五秒执行一次 15:15:03
每隔五秒执行一次 15:15:08
每隔五秒执行一次 15:15:13
每隔五秒执行一次 15:15:18
```
