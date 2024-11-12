package com.demo.task.mulitThread.task;

import com.demo.task.mulitThread.config.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 发送消息任务
 */
@Slf4j
@Component
public class SendEmailTask extends AbstractTask<String> {

    @Override
    protected Class<String> typeClass() {
        return String.class;
    }

    @Override
    protected TaskType getType() {
        return TaskType.SEND_EMAIL;
    }

    @Override
    protected boolean clear() {
        return true;
    }

    @Override
    protected String handle(String hashCode, String command) throws Throwable {
        // 模拟任务执行时间
        log.info("开始执行发送邮件任务....");
        TimeUnit.SECONDS.sleep(5);
        log.info("完成执行发送邮件任务....");
        return null;
    }
}
