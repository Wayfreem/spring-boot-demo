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

### 新增消费者的配置类

在消费者的配置中，我们需要增加一个消息的监听器，我们接收消息都是通过监听器来接收

```java
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
```

### 我们增加两个 hook

hook 用于在消息发送以及消息消费的时候做一层拦截，分为前置拦截以及后置拦截，具体看下代码就好，这个比较好理解

这里是为了增加一个 xid 到消息的用户属性中，后续在消费的时候可以从用户属性中获取到 xid，用于在日志中打印 xid，方便定位问题

**消息发送时候的 hook**

```java
/**
 * RocketMQ 发送消息钩子
 * 用于在发送消息前，将 MDC 中的 xid 放入消息的用户属性中
 */
@Slf4j
public class RqSendMessageHook implements SendMessageHook {

    @Override
    public String hookName() {
        return "RqSendMessageHook";
    }

    @Override
    public void sendMessageBefore(SendMessageContext context) {
        // 这里暂时先写死，后续再根据需求调整 xid
        String rid = MDC.get("xid");
        if(StringUtils.isNotBlank(rid)) {
            context.getMessage().putUserProperty("xid", rid);
        }
    }

    @Override
    public void sendMessageAfter(SendMessageContext context) {
    }
}
```
**消息消费时候的 hook**

```java
@Slf4j
public class RqConsumerMessageHook implements ConsumeMessageHook {
    @Override
    public String hookName() {
        return "RqConsumerMessageHook";
    }

    @Override
    public void consumeMessageBefore(ConsumeMessageContext context) {
        List<MessageExt> msgList = context.getMsgList();
        if (CollectionUtil.isEmpty(msgList)) {
            return;
        }
        for (MessageExt next : msgList) {
            String userProperty = next.getUserProperty("xid");
            if (StringUtils.isNotBlank(userProperty)) {
                MDC.put("xid", userProperty);
            }
        }
    }

    @Override
    public void consumeMessageAfter(ConsumeMessageContext context) {
        List<MessageExt> msgList = context.getMsgList();
        if (CollectionUtil.isEmpty(msgList)) {
            return;
        }

        for (MessageExt next : msgList) {
            String userProperty = next.getUserProperty("xid");
            if (StringUtils.isNotBlank(userProperty)) {
                MDC.remove("xid");
            }
        }
    }
}
```

### 增加监听器

上面我们已经把相关的配置以及 hook 都配置好，接下来我们需要增加一个监听器，用于接收消息，这里是使用了自动注册的方式，

当消息过来的时候，会根据 topic 和 tag 去匹配对应的 RocketMsgTopic 实现类，然后调用实现类的 consume 方法进行消费，这样子就不用我们手动去判断了

```java
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
```
