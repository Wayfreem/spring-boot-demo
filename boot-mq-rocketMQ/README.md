## 项目集成

### 第一步：引入依赖

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
        
<!--   rocketMQ 核心依赖包     -->
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>2.2.0</version>
</dependency>

<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.23</version>
</dependency>
```

### 第二步：配置文件
```yml
rocketmq:
  name-server: 192.168.152.130:9876 # 访问地址
  producer:
    group: Pro_Group # 必须指定group
    send-message-timeout: 3000 # 消息发送超时时长，默认 3s
    retry-times-when-send-failed: 3 # 同步发送消息失败重试次数，默认 2
    retry-times-when-send-async-failed: 3 # 异步发送消息失败重试次数，默认 2
```

### 第三步：编写测试类

```java
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProducerTest {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void test1(){
        rocketMQTemplate.convertAndSend("springboot-mq","hello springboot rocketmq");
    }
}
```

### 第四步：编写消费者
`@RocketMQMessageListener` 注解对应的 topic 就是发送消息时设置的 topic
```java
@Slf4j
@Component
@RocketMQMessageListener(topic = "springboot-mq", consumerGroup = "${rocketmq.consumer.group}")
public class Consumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("接受到消息：" + message);
    }
}
```
这样子其实就已经搞完了。


### 发送异步消息

producer向 broker 发送消息时指定消息发送成功及发送异常的回调方法 ，调用 API 后立即返回 ，producer发送消息线程不阻塞 ，消息发送成功或失败的回调任务在一个新的线程中执行 。

```java
/**
 * 异步消息
 * @param topic
 * @param msg
 */
public void sendASyncMsg(String topic,String msg){
    rocketMQTemplate.asyncSend(topic, msg, new SendCallback() {
        //消息发送成功的回调
        @Override
        public void onSuccess(SendResult sendResult) {
            System.out.println(sendResult);
        }
        //消息发送失败的回调
        @Override
        public void onException(Throwable throwable) {
            System.out.println(throwable.getMessage());
        }
    });
}
```
测试：

```java
@Test
public void testSendAsyncMsg(){
    producerSimple.sendAsyncMsg("my-topic","第1条异步消息");
    try {
        Thread.sleep(5000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```
