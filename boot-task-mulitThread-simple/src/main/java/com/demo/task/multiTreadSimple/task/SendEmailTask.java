package com.demo.task.multiTreadSimple.task;

import com.demo.task.multiTreadSimple.entiy.TaskDO;
import com.demo.task.multiTreadSimple.enums.TaskSyncTypeEnum;
import com.demo.task.multiTreadSimple.enums.TaskTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 发送消息任务
 */
@Slf4j
@Component
public class SendEmailTask implements SimpleTask {

    @Override
    public boolean support(String type) {
        return TaskTypeEnum.SEND_EMAIL.getCode().equals(type);
    }

    @Override
    public TaskSyncTypeEnum getTaskType() {
        return TaskSyncTypeEnum.SYNC;
    }

    @Override
    public boolean clear() {
        return false;
    }

    @Override
    public String handle(TaskDO taskQueueDO) throws RuntimeException, InterruptedException {
        // 模拟任务执行时间
        log.info("开始执行发送邮件任务....");
        TimeUnit.SECONDS.sleep(5);
        log.info("完成执行发送邮件任务....");
        return null;
    }
}
