package com.demo.mq.rocketmq.consumer.msgTopic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderTopicTag {

    WAYBILL_TRACK_TAG("waybill_track", "运单轨迹"),

    ADD_BACH_WAY_BILL_TAG("add_bach_way_bill", "新增批量运单"),

    ROUTE_PLAN_OPERATION_LOG_TAG("route_plan_operation_log", "路由计划操作日志"),
    ;

    private String tag;

    private String desc;
}
