package com.demo.mq.rocketmq.producer;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author wuq
 * @Time 2023-5-5 14:12
 * @Description
 */
@Slf4j
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
//        rocketMQTemplate.send(topic + ":tag1", MessageBuilder.withPayload(user).build()); // 等价于上面一行
    }

    /**
     * 发送同步消息（阻塞当前线程，等待broker响应发送结果，这样不太容易丢失消息）
     * （msgBody也可以是对象，sendResult为返回的发送结果）
     */
    public SendResult sendMsg(String topic, String msg) {
        SendResult sendResult = rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(msg).build());
        log.info("【sendMsg】sendResult={}", JSON.toJSONString(sendResult));
        return sendResult;
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

    /**
     * 发送延时消息（上面的发送同步消息，delayLevel的值就为0，因为不延时）
     * 在start版本中 延时消息一共分为18个等级分别为：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     *
     * @param topic          主题
     * @param msgBody        消息体
     * @param messageTimeOut 消息超时时间
     * @param delayLevel     延时等级
     */
    public void sendDelayMsg(String topic, String msgBody, int messageTimeOut, int delayLevel) {
        rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(msgBody).build(), messageTimeOut, delayLevel);
    }


    /**
     * 发送单向消息
     *
     * @param topic 主题
     * @param msg   消息体
     */
    public void sendOneWayMsg(String topic, String msg) {
        rocketMQTemplate.sendOneWay(topic, MessageBuilder.withPayload(msg).build());
    }


    /**
     * 发送带tag的消息，直接在topic后面加上":tag"
     *
     * @param topic   主题
     * @param tag     标签
     * @param msg 消息体
     * @return SendResult
     */
    public SendResult sendTagMsg(String topic, String tag, String msg) {
        return rocketMQTemplate.syncSend(topic + ":" + tag, MessageBuilder.withPayload(msg).build());
//        return rocketMQTemplate.syncSend(topic + ":tag2", MessageBuilder.withPayload(msg).build());
    }
}
