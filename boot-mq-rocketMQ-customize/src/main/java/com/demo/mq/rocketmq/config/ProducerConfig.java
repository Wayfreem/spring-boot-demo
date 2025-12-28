package com.demo.mq.rocketmq.config;

import com.demo.mq.rocketmq.config.hook.RqSendMessageHook;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.remoting.RPCHook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 生产者配置类
 * 使用@ConfigurationProperties绑定rocketmq.producer前缀的配置
 */
@Slf4j
@Configuration
public class ProducerConfig {

    @Resource
    private RocketMqConfig rocketMqConfig;

    @Bean(destroyMethod = "shutdown")
    public DefaultMQProducer rocketMQProducer() {
        // 获取生产者配置
        RocketMqConfig.Producer producerConfig = rocketMqConfig.getProducer();

        // 在RocketMQ 4.x版本中，需要通过创建RPCHook来设置访问凭证
        RPCHook rpcHook = null;
        if (producerConfig.getAccessKey() != null && producerConfig.getSecretKey() != null) {
            rpcHook = new AclClientRPCHook(new SessionCredentials(producerConfig.getAccessKey(), producerConfig.getSecretKey()));
        }

        // 初始化生产者
        DefaultMQProducer producer;
        if (rpcHook != null) {
            producer = new DefaultMQProducer(producerConfig.getGroupName(), rpcHook);
        } else {
            producer = new DefaultMQProducer(producerConfig.getGroupName());
        }

        producer.setNamesrvAddr(producerConfig.getNamesrvAddr());
        producer.getDefaultMQProducerImpl().registerSendMessageHook(new RqSendMessageHook());

        // 初始化生产者
        initProducer(producerConfig, producer);
        return producer;
    }

    /**
     * 初始化生产者
     *
     * @param producerConfig 生产者配置
     * @param producer       生产者实例
     */
    private static void initProducer(RocketMqConfig.Producer producerConfig, DefaultMQProducer producer) {

        //如果需要同一个jvm中不同的producer往不同的mq集群发送消息，需要设置不同的instanceName
        if (producerConfig.getMaxMessageSize() != null) {
            producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        }
        if (producerConfig.getSendMsgTimeout() != null) {
            producer.setSendMsgTimeout(producerConfig.getSendMsgTimeout());
        }
        //如果发送消息失败，设置重试次数，默认为2次
        if (producerConfig.getRetryTimesWhenSendFailed() != null) {
            producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        }
        try {
            producer.start();
        } catch (MQClientException e) {
            log.error("producer start error : {}", e);
            e.printStackTrace();
        }
    }

}
