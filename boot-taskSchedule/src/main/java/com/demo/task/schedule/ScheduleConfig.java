package com.demo.task.schedule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 这里是配置线程调度任务类
 *
 * @author wuq
 * @Time 2022-5-26 17:17
 * @Description
 */
@Configuration
public class ScheduleConfig {

    // 用来存入线程执行情况, 方便于停止定时任务时使用
    public static ConcurrentHashMap<String, ScheduledFuture> cache = new ConcurrentHashMap<>();


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
