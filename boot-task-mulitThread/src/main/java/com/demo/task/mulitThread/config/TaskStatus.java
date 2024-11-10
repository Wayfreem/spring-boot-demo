package com.demo.task.mulitThread.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态
 */
@Getter
@AllArgsConstructor
public enum TaskStatus {
    INIT(0, "初始化") {
        @Override
        public boolean isTrue(Integer status) {
            return getStatus().equals(status);
        }
    },
    COMPLETE(1, "完成") {
        @Override
        public boolean isTrue(Integer status) {
            return getStatus().equals(status);
        }
    },
    EXCEPTION(2, "异常") {
        @Override
        public boolean isTrue(Integer status) {
            return getStatus().equals(status);
        }
    },
    ;
    private final Integer status;
    private final String msg;

    public abstract boolean isTrue(Integer status);
}
