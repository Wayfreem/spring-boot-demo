package com.demo.mq.disruptor.event;

import com.lmax.disruptor.EventFactory;

/**
 * 定义事件工厂
 */
public class MyEventFactory implements EventFactory<MyEvent> {

    @Override
    public MyEvent newInstance() {
        return new MyEvent();
    }
}