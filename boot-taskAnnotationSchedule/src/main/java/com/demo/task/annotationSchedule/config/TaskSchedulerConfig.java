package com.demo.task.annotationSchedule.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling // 注解开启对计划任务的支持
public class TaskSchedulerConfig {
}