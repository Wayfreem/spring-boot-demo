package com.demo.mq.rocketmq.consumer.msgTopic;

import com.demo.mq.rocketmq.consumer.RocketMsgTopic;
import com.demo.mq.rocketmq.consumer.RocketMsgTopicEnum;
import com.demo.mq.rocketmq.consumer.msgTopic.orderTagConsumer.OrderTagConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OrderTopicAdapt implements RocketMsgTopic {

    @Autowired
    private List<OrderTagConsumer> orderTagConsumers;

    @Override
    public void consume(MessageExt messageExt) {
        String topic = messageExt.getTopic();
        String tags = messageExt.getTags();
        log.info("OrderTopicAdapt 开始消费： topic:{}, tags:{}, msg:{}", topic, tags, messageExt);

        // 找到对应的 OrderTagConsumer 如果支持就直接调用 consumer
        for (OrderTagConsumer consumer : orderTagConsumers) {
            if (consumer.support(tags)) {
                consumer.consume(messageExt);
                break;
            }
        }
    }

    @Override
    public boolean support(String topic) {
        return RocketMsgTopicEnum.ORDER_OPERATION.getTopic().equals(topic);
    }

}
