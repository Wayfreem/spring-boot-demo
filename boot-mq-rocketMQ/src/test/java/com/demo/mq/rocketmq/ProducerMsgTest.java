package com.demo.mq.rocketmq;

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
