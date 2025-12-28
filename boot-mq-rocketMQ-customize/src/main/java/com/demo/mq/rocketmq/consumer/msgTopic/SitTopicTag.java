package com.demo.mq.rocketmq.consumer.msgTopic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SitTopicTag {

    BATCH_HUB_ASSIGN_TAG("batch_hub_assign", "包裹批量分配快递员"),
    SITE_TRACK_FIX_TAG("site_track_fix", "站点签入补轨迹"),
    ;

    private String tag;

    private String desc;
}
