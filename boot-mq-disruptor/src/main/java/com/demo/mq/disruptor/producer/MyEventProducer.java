package com.demo.mq.disruptor.producer;

import com.demo.mq.disruptor.event.MyEvent;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyEventProducer {
    private final RingBuffer<MyEvent> ringBuffer;

    @Autowired
    public MyEventProducer(Disruptor<MyEvent> disruptor) {
        this.ringBuffer = disruptor.getRingBuffer();
    }

    public void publishEvent(String message) {
        long sequence = ringBuffer.next();
        try {
            MyEvent event = ringBuffer.get(sequence);
            event.setMessage(message);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}