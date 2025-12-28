package com.demo.mq.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 名称后续需要调整，改为符合业务的命名规范
 * RocketMQ消息监听器
 */
@Slf4j
@Component
public class RocketMsgListener implements MessageListenerConcurrently {

    @Autowired
    private List<RocketMsgTopic> rocketMsgTopics;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
        if (CollectionUtils.isEmpty(list)) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt messageExt = list.get(0);
        String messageBody = new String(messageExt.getBody());
        String tags = messageExt.getTags();
        String topic = messageExt.getTopic();
        Map<String, String> properties = messageExt.getProperties();
        String msgId = messageExt.getMsgId();
        log.info("=====RocketMsgListener==== topic:{}, tag:{}", topic, tags);
        log.info("消息消费 topic:{}, tag:{}, body:{}, msgId:{}, properties:{}", topic, tags, messageBody, msgId, JSON.toJSONString(properties));

        int reConsume = messageExt.getReconsumeTimes();
        // 消息已经重试了3次，如果不需要再次消费，则返回成功
        if (reConsume == 3) {
            log.error("消息消费重试三次异常topic:{}, tag:{},msgId:{}", topic, tags, msgId);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        // 遍历所有的 RocketMsgTopic 实现类，判断是否支持该 topic
        for (RocketMsgTopic rocketMsgTopic : rocketMsgTopics) {
            if (rocketMsgTopic.support(topic)) {
                rocketMsgTopic.consume(messageExt);
                break;
            }
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
