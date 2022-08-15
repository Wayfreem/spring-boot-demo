package com.demo.task.annotationSchedule.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

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
