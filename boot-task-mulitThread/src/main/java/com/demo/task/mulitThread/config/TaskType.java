package com.demo.task.mulitThread.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 任务类型
 */
@Getter
@AllArgsConstructor
public enum TaskType {

    NIL(-100, "分发任务") {
        @Override
        public boolean isTrue(Integer type) {
            return getType().equals(type);
        }
    },
    SYNC_TASK(-1, "同步任务") {
        @Override
        public boolean isTrue(Integer type) {
            return getType().equals(type);
        }
    },
    SEND_MSG(1, "发送短信") {
        @Override
        public boolean isTrue(Integer type) {return getType().equals(type);}
    },
    SEND_EMAIL(2, "发送邮件") {
        @Override
        public boolean isTrue(Integer type) {return getType().equals(type);}
    },
    ;

    private final Integer type;
    private final String msg;

    public abstract boolean isTrue(Integer type);

    public static String getMsg(Integer type){
        return Arrays.stream(TaskType.values()).filter(s -> s.isTrue(type))
                .findFirst().map(TaskType::getMsg).orElse("");
    }
}
