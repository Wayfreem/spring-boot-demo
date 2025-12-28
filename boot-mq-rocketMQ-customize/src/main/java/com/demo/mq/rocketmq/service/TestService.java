package com.demo.mq.rocketmq.service;

import com.alibaba.fastjson2.JSONObject;
import com.demo.mq.rocketmq.config.RocketMqConfig;
import com.demo.mq.rocketmq.consumer.RocketMsgTopicEnum;
import com.demo.mq.rocketmq.consumer.msgTopic.OrderTopicTag;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class TestService {

    @Resource
    private RocketMqConfig rocketMqConfig;

    @Resource
    private DefaultMQProducer rocketMQProducer;

    public String hello() {
        return "hello rocketMQ";
    }

    public String testMq() {
        rocketMqConfig.getConsumer().getTopics().forEach(System.out::println);
        return "test mq";
    }


    public String sendMsg(String msg) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", msg);
        jsonObject.put("topic", RocketMsgTopicEnum.ORDER_OPERATION.getTopic());
        jsonObject.put("orderNo", "YT2520021616005119");

        sendMessage(RocketMsgTopicEnum.ORDER_OPERATION.getTopic(), jsonObject.toString());

        return "send msg: " + msg;
    }

    public String sendMsgAndTag(String msg) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", msg);
        jsonObject.put("topic", RocketMsgTopicEnum.ORDER_OPERATION.getTopic());
        jsonObject.put("tag", OrderTopicTag.WAYBILL_TRACK_TAG.getTag());
        jsonObject.put("orderNo", "YT2520021616005119");

        sendMessage(RocketMsgTopicEnum.ORDER_OPERATION.getTopic(), OrderTopicTag.WAYBILL_TRACK_TAG.getTag(), jsonObject.toString());

        return "send msg: " + msg;
    }

    public SendResult sendMessage(String topic, String msgInfo) {
        DefaultMQProducer producer = rocketMQProducer;
        producer.setProducerGroup(rocketMqConfig.getProducer().getGroupName());
        SendResult sendResult = null;
        try {
            Message sendMsg = new Message(topic, msgInfo.getBytes());
            sendResult = producer.send(sendMsg);
        } catch (Exception e) {
            log.error("syncTrackMsg e ", e);
        }
        return sendResult;
    }

    public SendResult sendMessage(String topic, String tag, String msgInfo) {
        DefaultMQProducer producer = rocketMQProducer;
        producer.setProducerGroup(rocketMqConfig.getProducer().getGroupName());
        SendResult sendResult = null;
        try {
            Message sendMsg = new Message(topic, tag, msgInfo.getBytes());
            sendResult = producer.send(sendMsg);

        } catch (Exception e) {
            log.error("sendMessage e ", e);
        }
        return sendResult;
    }

    public SendResult sendMessage(String topic, String tag, String key, String msgInfo) {
        DefaultMQProducer producer = rocketMQProducer;
        producer.setProducerGroup(rocketMqConfig.getProducer().getGroupName());
        SendResult sendResult = null;
        try {
            Message sendMsg = new Message(topic, tag, key, msgInfo.getBytes());
            sendResult = producer.send(sendMsg);

        } catch (Exception e) {
            log.error("sendMessage e ", e);
        }
        return sendResult;
    }
}
