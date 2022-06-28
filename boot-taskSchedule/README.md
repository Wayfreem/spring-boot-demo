## 简介
ThreadPoolTaskScheduler 这个类是在 org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler 这个包中。

ThreadPoolTaskScheduler 是 spring taskSchedule 接口的实现，可以用来做定时任务使用。

ThreadPoolTaskScheduler 四个版本定时任务方法：

- schedule(Runnable task, Date stateTime)，在指定时间执行一次定时任务

- schedule(Runnable task, Trigger trigger)，动态创建指定表达式cron的定时任务，threadPoolTaskScheduler.schedule(() -> {}, triggerContext -> newCronTrigger("").nextExecutionTime(triggerContext));

- scheduleAtFixedRate，指定间隔时间执行一次任务，间隔时间为前一次执行开始到下次任务开始时间

- scheduleWithFixedDelay，指定间隔时间执行一次任务，间隔时间为前一次任务完成到下一次开始时间


## 具体实现

### 第一步

首先我们需要向 spring 容器中注入一个 ThreadPoolTaskScheduler 的 bean，用于调度定时任务，以及需要增加一个缓存用于存入当前执行任务的 scheduleFuture 对象。

将 ScheduledFuture 对象缓存的原因是在于，为了方面于停止对应的任务。

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
### 第二步

增加用于外部访问的接口 controller

```java
@RestController
public class TestController {

    @Autowired
    private DynamicTask task;

    @RequestMapping("start")
    public void startTask() {
        com.demo.task.startCron();
    }

    // 测试访问： http://localhost:8080/stopById?taskId=任务一
    @RequestMapping("stopById")
    public void stopById(String taskId) {
        com.demo.task.stop(taskId);
    }

    @RequestMapping("stopAll")
    public void stopAll() {
        com.demo.task.stopAll();
    }
}
```

### 第三步

**核心逻辑**

在这里当在执行 `threadPoolTaskScheduler.schedule()` 时，会传入一个自定义的 com.demo.task，以及一个 trigger。
调用完成之后会返回一个 scheduledFuture，这个就是当前的任务调度器，停止的时候需要找到这个调度器，用这个调用器来终止。

`threadPoolTaskScheduler.schedule(com.demo.task, cron)` 用来调度任务

`boolean cancelled = scheduledFuture.isCancelled();` 用来判断是否已经取消

`scheduledFuture.cancel(true)` 用来将当前的任务取消


下面是核心代码逻辑：
```java
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

对于上面的任务类 `CustomizeTask` 可以自定义进行封装，可以增加成员属性调用的 服务方法类 以及 调用参数，在 run 方法上面就可以去调用对应的服务方法了

```java
private class CustomizeTask implements Runnable {
    private String name;    // 任务名字
    private String cron;    // 触发条件
    private String data;    // 传输的数据参数
    private String method;  // 需要调用的方法
    
    CustomizeTask(String name, String cron, String data, String method) {
        this.name = name;
        this.cron = cron;
        this.data = data;
        this.method = method;
    }

    public String getCron(){
        return this.cron;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public void run() {
        // 通过反射获取到调用的方法，传入调用的参数 data
        logger.info("当前任务名称：{}", name );
    }
}
```