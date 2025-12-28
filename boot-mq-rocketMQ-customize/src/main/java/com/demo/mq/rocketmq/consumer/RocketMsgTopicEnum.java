package com.demo.mq.rocketmq.consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RocketMsgTopicEnum {

    PROBLEM_OPERATION("problem_operation", "问题操作topic"),
    SITE_OPERATION("site_operation", "站点操作topic"),
    ORDER_OPERATION("order_operation", "订单操作topic"),
    COURIER_OPERATION("courier_operation", " курьер操作topic"),
    TASK_TOPIC("task_topic", "任务topic"),
    // ...
    ;

    private final String topic;

    private final String desc;

}
