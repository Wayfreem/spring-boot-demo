package com.demo.task.multiTreadSimple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskTypeEnum {

    /**
     * 发送邮件
     */
    SEND_EMAIL("send_email", "发送邮件"),
    /**
     * 发送短信
     */
    SEND_MSG("send_msg", "发送短信"),
    ;


    /**
     * 类型
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据类型获取描述
     *
     * @param type 类型
     * @return 描述
     */
    public static String getNameByType(String type) {
        for (TaskTypeEnum taskTypeEnum : TaskTypeEnum.values()) {
            if (taskTypeEnum.getCode().equals(type)) {
                return taskTypeEnum.getDesc();
            }
        }
        return null;
    }
}
