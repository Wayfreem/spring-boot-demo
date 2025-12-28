package com.demo.mq.rocketmq.consumer.msgTopic.orderTagConsumer;

import org.apache.rocketmq.common.message.MessageExt;

public interface OrderTagConsumer {

    /**
     * 消费消息
     *
     * @param messageExt 消息
     */
    void consume(MessageExt messageExt);

    /**
     * 是否支持该 tag
     *
     * @param tag tag
     * @return 是否支持
     */
    boolean support(String tag);
}
