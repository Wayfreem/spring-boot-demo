package com.demo.mq.rocketmq.config;

import com.demo.mq.rocketmq.config.hook.RqConsumerMessageHook;
import com.demo.mq.rocketmq.consumer.RocketMsgListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.remoting.RPCHook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;

/**
 * RocketMQ消费者配置类
 */
@Slf4j
@Configuration
public class ConsumerConfig {

    @Resource
    private RocketMqConfig rocketMqConfig;
    @Resource
    private RocketMsgListener msgListener;

    @Bean
    public DefaultMQPushConsumer getRocketMQConsumer() {
        // 获取RocketMQ消费者配置
        RocketMqConfig.Consumer consumerConfig = rocketMqConfig.getConsumer();

        RPCHook rpcHook = null;
        if (consumerConfig.getAccessKey() != null && consumerConfig.getSecretKey() != null) {
            rpcHook = new AclClientRPCHook(new SessionCredentials(consumerConfig.getAccessKey(), consumerConfig.getSecretKey()));
        }

        DefaultMQPushConsumer consumer;
        if (rpcHook != null) {
            consumer = new DefaultMQPushConsumer(consumerConfig.getGroupName(), rpcHook);
        } else {
            consumer = new DefaultMQPushConsumer(consumerConfig.getGroupName());
        }

        if (!consumerConfig.getIsOnOff()) {
            return consumer;
        }
        consumer.setNamesrvAddr(consumerConfig.getNamesrvAddr());
        consumer.setConsumeThreadMin(consumerConfig.getConsumeThreadMin());
        consumer.setConsumeThreadMax(consumerConfig.getConsumeThreadMax());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setConsumeMessageBatchMaxSize(consumerConfig.getConsumeMessageBatchMaxSize());
        consumer.getDefaultMQPushConsumerImpl().registerConsumeMessageHook(new RqConsumerMessageHook());

        // 注册消息监听器
        consumer.registerMessageListener(msgListener);
        try {
            // 订阅的 topic 和 tag
            List<String> topics = consumerConfig.getTopics();
            for (String topicTags : topics) {
                String[] topicTag = topicTags.split("~");
                consumer.subscribe(topicTag[0], topicTag[1]);
            }
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return consumer;
    }
}
