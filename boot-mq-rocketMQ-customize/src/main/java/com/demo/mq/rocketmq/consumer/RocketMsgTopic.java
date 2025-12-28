package com.demo.mq.rocketmq.consumer;

import org.apache.rocketmq.common.message.MessageExt;

/**
 * 监听主队列topic接口
 */
public interface RocketMsgTopic extends ListenerTopic {
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
