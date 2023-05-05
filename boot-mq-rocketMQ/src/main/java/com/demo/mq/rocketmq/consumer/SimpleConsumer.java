package com.demo.mq.rocketmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 *
 * 实现 RocketMQListener 监听器是为了接受到发送过来的消息，泛型是消息的类型
 *
 * @author wuq
 * @Time 2023-5-5 14:16
 * @Description
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "springboot-mq", consumerGroup = "${rocketmq.consumer.group}")
public class SimpleConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("Receive message：" + message);
    }
}
