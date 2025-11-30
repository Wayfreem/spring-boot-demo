package com.demo.task.multiTreadSimple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TaskStatusEnum {

    PENDING(0, "待执行"),
    EXECUTING(1, "执行中"),
    SUCCESS(2, "执行成功"),
    FAIL(3, "执行失败"),

    ;
    /**
     * 类型
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据 code 值返回对应的 desc
     *
     * @param code 类型代码
     * @return 对应的描述，如果未找到则返回 null
     */
    public static String getNameByType(Integer code) {
        return Arrays.stream(TaskStatusEnum.values())
                .filter(type -> type.getType().equals(code))
                .map(TaskStatusEnum::getDesc)
                .findFirst()
                .orElse(null);
    }

}
