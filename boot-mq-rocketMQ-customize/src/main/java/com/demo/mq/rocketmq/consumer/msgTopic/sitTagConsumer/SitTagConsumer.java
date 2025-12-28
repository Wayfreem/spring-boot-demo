package com.demo.mq.rocketmq.consumer.msgTopic.sitTagConsumer;

import org.apache.rocketmq.common.message.MessageExt;

public interface SitTagConsumer {

    /**
     * 消费消息
     *
     * @param messageExt 消息
     */
    void consume(MessageExt messageExt);

    /**
     * 是否支持该topic
     *
     * @param topic topic
     * @return 是否支持
     */
    boolean support(String topic);
}
