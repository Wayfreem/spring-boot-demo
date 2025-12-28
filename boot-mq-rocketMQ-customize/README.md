## 项目说明
之前有一个模块是使用的 `rocketMQTemplate` 发送消息的，但是那个模块是使用的默认的配置，现在这个模块是使用的自定义的配置。并且项目之前是使用的注解的方式来接收消息，现在需要改为使用 `MessageListener` 来接收消息。

这里说明下，我们使用自定义的方式来定义 producer，以及使用 `MessageListener` 来接收消息。

以及接收到消息之后，我们使用策略的方式对 topic 进行分类，根据不同的 topic 来进行不同的处理。以及对于不同的 `tag` 我们也使用策略的方式来进行分类，根据不同的 tag 来进行不同的处理。

## 项目结构


## 项目集成

### pom 依赖

除了正常的依赖以外，这里需要添加 rocketmq-client 以及 rocketmq-common 这两个依赖。

```xml
<!--    rocketMQ 依赖    -->
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-client</artifactId>
    <version>4.7.0</version>
</dependency>
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-common</artifactId>
    <version>4.7.0</version>
</dependency>
```

### rocketmq 配置

```yaml
server:
  port: 8081

rocketmq:
  # 生产者配置
  producer:
    # 访问密钥
    accessKey: rocketmq2
    # 密钥
    secretKey: 12345678
    # on（解析之后为 true）或者 off（解析之后为 false）
    isOnOff: on
    # 发送同一类消息的设置为同一个group，保证唯一
    groupName: defaultGroup
    # 服务地址
    namesrvAddr: 127.0.0.1:9876
    # 消息最大长度 默认1024*1024(1G)
    maxMessageSize: 1048576
    # 发送消息超时时间,默认3000
    sendMsgTimeout: 3000
    # 发送消息失败重试次数，默认2
    retryTimesWhenSendFailed: 2
  # 消费者配置
  consumer:
    # 访问密钥
    accessKey: rocketmq2
    # 密钥
    secretKey: 12345678
    # on（解析之后为 true）或者 off（解析之后为 false）
    isOnOff: on
    # 官方建议：确保同一组中的每个消费者订阅相同的主题。
    groupName: defaultGroup
    # 服务地址
    namesrvAddr: 127.0.0.1:9876
    # 接收该 Topic 下所有 Tag
    topics:
      - problem_operation~*
      - order_operation~*
      - courier_operation~*
      - site_operation~*
      - task_topic~*
    consumeThreadMin: 20
    consumeThreadMax: 64
    # 设置一次消费消息的条数，默认为1条
    consumeMessageBatchMaxSize: 1
```

### 新增 RocketMQ 配置类

为了方便后续的使用，我们新增一个 RocketMQ 配置类，用来配置生产者以及消费者的相关参数。

```java
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

```
### 增加 RocketMQ 生产者配置 bean

生产者配置为我们后续发送消息提供方法

```java
import com.demo.refactor.mq.config.hook.RqSendMessageHook;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
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

    @Resource
    private RocketMqCirroConfig rocketMqCirroConfig;

    @Bean(destroyMethod = "shutdown")
    public DefaultMQProducer rocketMQProducer() {
        // 获取生产者配置
        RocketMqConfig.Producer producerConfig = rocketMqConfig.getProducer();

        // 初始化生产者
        DefaultMQProducer producer = new DefaultMQProducer(producerConfig.getGroupName());
        producer.setNamesrvAddr(producerConfig.getNamesrvAddr());
        producer.getDefaultMQProducerImpl().registerSendMessageHook(new RqSendMessageHook());

        // 初始化生产者
        initProducer(producerConfig, producer);
        return producer;
    }

    @Bean(name = "rocketMQProducerTrack", destroyMethod = "shutdown")
    public DefaultMQProducer rocketMQProducerTrack() {
        // 获取生产者配置
        RocketMqConfig.Producer producerConfig = rocketMqConfig.getProducer();

        DefaultMQProducer producer = new DefaultMQProducer(rocketMqCirroConfig.getCirroGroupTrack());
        producer.setNamesrvAddr(producerConfig.getNamesrvAddr());

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
```
