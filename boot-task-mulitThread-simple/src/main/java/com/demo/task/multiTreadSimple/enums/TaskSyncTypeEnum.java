
package com.demo.task.multiTreadSimple.enums;

public enum TaskSyncTypeEnum {

    /**
     * 同步
     */
    SYNC,

    /**
     * 异步
     */
    ASYNC;

    /**
     * 任务队列类型
     *
     * @return 任务队列类型
     */
    public TaskTypeEnum getTaskQueueType() {
        return TaskTypeEnum.valueOf(this.name());
    }

    /**
     * 同步执行
     *
     * @return 同步执行
     */
    public boolean isSync() {
        return this == SYNC;
    }

    /**
     * 异步执行
     *
     * @return 异步执行
     */
    public boolean isAsync() {
        return this == ASYNC;
    }
}
