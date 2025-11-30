package com.demo.task.multiTreadSimple.task;

import com.demo.task.multiTreadSimple.entiy.TaskDO;
import com.demo.task.multiTreadSimple.enums.TaskSyncTypeEnum;

/**
 * 任务接口
 */
public interface SimpleTask {

    /**
     * 是否支持
     *
     * @return Boolean
     */
    boolean support(String type);

    /**
     * 任务队列类型
     *
     * @return 任务队列类型
     */
    TaskSyncTypeEnum getTaskType();

    /**
     * 是否需要删除任务
     *
     * @return Boolean
     */
    boolean clear();


    /**
     * 任务处理
     *
     * @param taskQueueDO 任务队列DO
     * @return 执行结果
     * @throws RuntimeException 执行异常
     */
    String handle(TaskDO taskQueueDO) throws RuntimeException, InterruptedException;

}
