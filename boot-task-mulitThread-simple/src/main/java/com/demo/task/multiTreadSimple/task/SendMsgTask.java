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
public class SendMsgTask implements SimpleTask {

    @Override
    public boolean support(String type) {
        return TaskTypeEnum.SEND_MSG.getCode().equals(type);
    }

    @Override
    public TaskSyncTypeEnum getTaskType() {
        return TaskSyncTypeEnum.ASYNC;
    }

    @Override
    public boolean clear() {
        return true;
    }

    @Override
    public String handle(TaskDO taskQueueDO) throws RuntimeException, InterruptedException {
        // 模拟任务执行时间
        log.info("开始执行信息任务....");
        TimeUnit.SECONDS.sleep(5);
        log.info("完成执行信息任务....");

        return null;
    }
}
