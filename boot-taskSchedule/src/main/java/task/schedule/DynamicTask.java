package task.schedule;

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
