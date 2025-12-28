package com.demo.mq.rocketmq.consumer.msgTopic.orderTagConsumer;

import com.demo.mq.rocketmq.consumer.msgTopic.OrderTopicTag;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TrackTagConsumer implements OrderTagConsumer{


    @Override
    public void consume(MessageExt messageExt) {
        String tags = messageExt.getTags();
        log.info("====CirroAddWayBillTagConsumer==== 开始消费：tag: {}", tags);
    }

    @Override
    public boolean support(String tag) {
        return OrderTopicTag.WAYBILL_TRACK_TAG.getTag().equals(tag);
    }
}
