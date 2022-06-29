package com.demo.event.cutomize.eventConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wuq
 * @create 2019-09-21 15:34
 */
@Component
public class SimpleEventPublisher<T> {

    @Autowired
    ApplicationContext applicationContext;

    public void publish(T msg) {
        applicationContext.publishEvent(new SimpleEvent(this, msg));
    }

    public void publish(T msg, String topic) {
        applicationContext.publishEvent(new SimpleEvent(this, msg, topic));
    }

    public List publishAndReceive(T msg, String topic) {
        SimpleEvent simpleEvent = new SimpleEvent(this, msg, topic);
        applicationContext.publishEvent(simpleEvent);
        return simpleEvent.getResult();
    }
}
