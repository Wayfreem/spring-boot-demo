package com.demo.mq.rocketmq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * RocketMQ配置类
 * 使用@ConfigurationProperties绑定rocketmq前缀的配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMqConfig {

    /**
     * 生产者配置
     */
    private Producer producer;

    /**
     * 消费者配置
     */
    private Consumer consumer;


    /**
     * 生产者配置内部类
     */
    @Data
    public static class Producer {
        /**
         * 消费者开关 配置 on 开启（true），off 关闭（false）
         */
        private String isOnOff;

        /**
         * 发送同一类消息的设置为同一个group，保证唯一
         */
        private String groupName;

        /**
         * 服务地址
         */
        private String namesrvAddr;

        /**
         * 消息最大长度 默认1024*1024(1G)
         */
        private Integer maxMessageSize;

        /**
         * 发送消息超时时间,默认3000
         */
        private Integer sendMsgTimeout;

        /**
         * 发送消息失败重试次数，默认2
         */
        private Integer retryTimesWhenSendFailed;
    }

    /**
     * 消费者配置内部类
     */
    @Data
    public static class Consumer {
        /**
         * 消费者开关 配置 on 开启（true），off 关闭（false）
         */
        private Boolean isOnOff;

        /**
         * 官方建议：确保同一组中的每个消费者订阅相同的主题。
         */
        private String groupName;

        /**
         * 服务地址
         */
        private String namesrvAddr;

        /**
         * 接收该 Topic 下所有 Tag
         */
        private List<String> topics;

        /**
         * 消费线程最小数量
         */
        private Integer consumeThreadMin;

        /**
         * 消费线程最大数量
         */
        private Integer consumeThreadMax;

        /**
         * 设置一次消费消息的条数，默认为1条
         */
        private Integer consumeMessageBatchMaxSize;
    }
}
