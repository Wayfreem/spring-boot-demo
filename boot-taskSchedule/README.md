## 简介
ThreadPoolTaskScheduler 这个类是在 org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler 这个包中。

ThreadPoolTaskScheduler 是 spring taskSchedule 接口的实现，可以用来做定时任务使用。

## 具体实现

首先我们需要向 spring 容器中注入一个 ThreadPoolTaskScheduler 的bean，用于调度定时任务，以及需要增加一个 缓存用于存入当前执行任务的 scheduleFuture 对象

```java
@Configuration
public class ScheduleConfig {

    // 用来存入线程执行情况, 方便于停止定时任务时使用
    public static ConcurrentHashMap<String, ScheduledFuture> map = new ConcurrentHashMap<String, ScheduledFuture>();

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);                        // 线程池大小
        threadPoolTaskScheduler.setThreadNamePrefix("taskExecutor-");   // 线程名称
        threadPoolTaskScheduler.setAwaitTerminationSeconds(60);         // 等待时长
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);  // 调度器shutdown被调用时等待当前被调度的任务完成
        return threadPoolTaskScheduler;
    }
}
```

增加用于外部访问的接口 controller

```java
@RestController
public class TestController {

    @Autowired
    private DynamicTask task;

    @RequestMapping("start")
    public void startTask() {
        task.startCron();
    }

    // 测试访问： http://localhost:8080/stopById?taskId=任务一
    @RequestMapping("stopById")
    public void stopById(String taskId) {
        task.stop(taskId);
    }

    @RequestMapping("stopAll")
    public void stopAll() {
        task.stopAll();
    }
}
```

**核心逻辑**

在这里当在执行 `threadPoolTaskScheduler.schedule()` 时，会传入一个自定义的 task，以及一个 trigger。
调用完成之后会返回一个 scheduledFuture，这个就是当前的任务调度器，停止的时候需要找到这个调度器，用这个调用器来终止。

`threadPoolTaskScheduler.schedule(task, cron)` 用来调度任务

`boolean cancelled = scheduledFuture.isCancelled();` 用来判断是否已经取消

`scheduledFuture.cancel(true)` 用来将当前的任务取消


下面是核心代码逻辑：
```java
package com.example.task;

import com.example.task.config.ScheduleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * @author wuq
 * @Time 2022-5-26 17:13
 * @Description
 */
@Component
public class DynamicTask {
    private final static Logger logger = LoggerFactory.getLogger(DynamicTask.class);

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;    // 注入线程池任务调度类

    public void startCron(){

        getTasks().forEach(customizeTask -> {
            // 开始执行调度
            ScheduledFuture scheduledFuture = threadPoolTaskScheduler.schedule(customizeTask, new CronTrigger(customizeTask.getCron()));
            // 将 scheduledFuture 保存下来用于停止任务使用
            ScheduleConfig.cache.put(customizeTask.getName(), scheduledFuture);
        });
    }

    public void stop(String taskId) {
        if (ScheduleConfig.cache.isEmpty()) return;
        if (ScheduleConfig.cache.get(taskId) == null) return;

        ScheduledFuture scheduledFuture = ScheduleConfig.cache.get(taskId);

        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);   // 这里需要使用指定的 scheduledFuture 来停止当前的线程
            ScheduleConfig.cache.remove(taskId);        // 移除缓存
        }
    }

    public void stopAll(){
        if (ScheduleConfig.cache.isEmpty()) return;
        ScheduleConfig.cache.values().forEach(scheduledFuture -> scheduledFuture.cancel(true) );
    }

    private List<CustomizeTask> getTasks(){
        return Arrays.asList(new CustomizeTask("任务一", "0/2 * * * * ?"),
                new CustomizeTask("任务二", "0/3 * * * * ?"));
    }

    // 自定义任务，这里用来对任务进行封装
    private class CustomizeTask implements Runnable {
        private String name;    // 任务名字
        private String cron;    // 触发条件

        CustomizeTask(String name, String cron) {
            this.name = name;
            this.cron = cron;
        }

        public String getCron(){
            return this.cron;
        }

        public String getName(){
            return this.name;
        }

        @Override
        public void run() {
            logger.info("当前任务名称：{}", name );
        }
    }
}
```