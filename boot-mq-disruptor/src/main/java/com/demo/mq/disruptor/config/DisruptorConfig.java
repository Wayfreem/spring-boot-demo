package com.demo.mq.disruptor.config;

import com.demo.mq.disruptor.event.MyEvent;
import com.demo.mq.disruptor.event.MyEventFactory;
import com.demo.mq.disruptor.event.MyEventHandler;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class DisruptorConfig {
    private static final int BUFFER_SIZE = 1024;

    @Bean
    public Disruptor<MyEvent> disruptor() {
        // 创建线程池
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // 创建事件工厂
        MyEventFactory factory = new MyEventFactory();
        // 创建 Disruptor
        Disruptor<MyEvent> disruptor = new Disruptor<>(factory, BUFFER_SIZE, executor, ProducerType.SINGLE, new BlockingWaitStrategy());
        // 设置事件处理器
        disruptor.handleEventsWith(new MyEventHandler());
        // 启动 Disruptor
        disruptor.start();
        return disruptor;
    }
}