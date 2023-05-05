package com.demo.mq.rocketmq.producer;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wuq
 * @Time 2023-5-5 14:12
 * @Description
 */
@Component
public class SimpleProducer {

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    /**
     * 发送同步消息
     *
     * @param topic 主题
     * @param msg   消息体
     */
    public void sendSyncMsg(String topic, String msg) {
        rocketMQTemplate.convertAndSend(topic, msg);
    }

    /**
     * 异步消息
     *
     * @param topic 主题
     * @param msg   消息体
     */
    public void sendAsyncMsg(String topic, String msg) {
        rocketMQTemplate.asyncSend(topic, msg, new SendCallback() {
            //消息发送成功的回调
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println(sendResult);
            }

            //消息发送失败的回调
            @Override
            public void onException(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
    }

    /**
     * 发送异步消息
     *
     * @param topic        主题
     * @param msg          消息体
     * @param sendCallback 回调方式
     */
    public void sendAsyncMsg(String topic, String msg, SendCallback sendCallback) {
        rocketMQTemplate.asyncSend(topic, msg, sendCallback);
    }
}
