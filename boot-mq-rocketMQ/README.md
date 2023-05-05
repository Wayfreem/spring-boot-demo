[博客地址](https://github.com/Wayfreem/spring-boot-demo/blob/main/boot-mq-rocketMQ/README.md)

前面我们介绍了怎么使用 [docker 安装 rocketMQ](https://blog.csdn.net/qq_18948359/article/details/130419629?spm=1001.2014.3001.5501)，现在我们就来试试使用 SpringBoot 集成之后，怎么发送消息和消费消息。


## 集成步骤

### 工程结构
![工程结构图](https://img-blog.csdnimg.cn/b1bf64ff45ae4be0ac0ef030f43f9783.png)

### 第一步：引入相关依赖
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

### 第二步：增加配置文件
```yml
rocketmq:
  name-server: 192.168.152.130:9876
  producer:
    group: SpringBoot_Group
    send-message-timeout: 3000
    retry-times-when-send-failed: 3
    retry-times-when-send-async-failed: 3
  consumer:
    group: SpringBoot_Group
```

### 第三步：增加消息的发送者

发送消息其实也比较好理解，就是通过 `RocketMQTemplate ` 来操作，由于 Spring 中封装了一层，所以我们操作起来就比较简单，具体的代码向下看就好。

```java
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimpleProducer {

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    /**
     * 发送同步消息
     *
     * @param topic 主题
     * @param msg   消息体
     */
    public void sendSyncMsg(String topic, String msg) {
        rocketMQTemplate.convertAndSend(topic, msg);
    }

    /**
     * 异步消息
     *
     * @param topic 主题
     * @param msg   消息体
     */
    public void sendAsyncMsg(String topic, String msg) {
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

    /**
     * 发送异步消息
     *
     * @param topic        主题
     * @param msg          消息体
     * @param sendCallback 回调方式
     */
    public void sendAsyncMsg(String topic, String msg, SendCallback sendCallback) {
        rocketMQTemplate.asyncSend(topic, msg, sendCallback);
    }
}
```

### 第四步：增加消息的消费者
我们使用注解 `@RocketMQMessageListener` 来作为监听指定的 topic 以及 consumerGroup 的消息，另外我们需要实现 `RocketMQListener` 来处理回调消息，还是比较简单的，具体的代码如下：
```java
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 *
 * 实现 RocketMQListener 监听器是为了接受到发送过来的消息，泛型是消息的类型
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "springboot-mq", consumerGroup = "${rocketmq.consumer.group}")
public class SimpleConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("Receive message：" + message);	// 打印传递的消息
    }
}

```

到这里呢，其实已经算是搞完了，接下来我们来测试下消息。

先启动程序服务，然后进行下来的测试

### 编写测试类
```java
import com.demo.mq.rocketmq.producer.SimpleProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author wuq
 * @Time 2023-5-5 13:42
 * @Description
 */
@SpringBootTest
public class ProducerMsgTest {

    @Autowired
    SimpleProducer simpleProducer;

    @Test
    public void testSync(){
        simpleProducer.sendSyncMsg("springboot-mq", "发送同步消息");
    }

    @Test
    public void testAsync(){
        simpleProducer.sendAsyncMsg("springboot-mq", "发送异步消息");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
