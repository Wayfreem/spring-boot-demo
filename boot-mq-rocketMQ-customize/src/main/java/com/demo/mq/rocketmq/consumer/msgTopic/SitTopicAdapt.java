package com.demo.mq.rocketmq.consumer.msgTopic;

import com.demo.mq.rocketmq.consumer.RocketMsgTopic;
import com.demo.mq.rocketmq.consumer.RocketMsgTopicEnum;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SitTopicAdapt implements RocketMsgTopic {


    @Override
    public void consume(MessageExt messageExt) {
        String topic = messageExt.getTopic();
        log.info("SitTopicAdapt 开始消费： topic:{},msg:{}", topic, messageExt);
    }

    @Override
    public boolean support(String topic) {
        return RocketMsgTopicEnum.SITE_OPERATION.getTopic().equals(topic);
    }
}
