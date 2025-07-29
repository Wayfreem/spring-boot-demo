package com.demo.mq.disruptor.event;

import com.lmax.disruptor.EventHandler;

/**
 * 定义事件处理器
 */
public class MyEventHandler implements EventHandler<MyEvent> {

    @Override
    public void onEvent(MyEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("Received event: " + event.getMessage());
    }
}