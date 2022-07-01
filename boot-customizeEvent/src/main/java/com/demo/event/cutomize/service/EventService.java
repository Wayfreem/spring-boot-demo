package com.demo.event.cutomize.service;

import com.demo.event.cutomize.eventConfig.SimpleEvent;
import com.demo.event.cutomize.eventConfig.SimpleEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class EventService {

    @Autowired
    SimpleEventPublisher simpleEventPublisher;

    public void send() {
        Map map = Map.of("id", "1", "name", "测试 event");
        simpleEventPublisher.publish(map.toString(), "event#doMsg");
    }

    public void sendAndReceive() {
        Map map = Map.of("id", "1", "name", "测试 event");
        List list = simpleEventPublisher.publishAndReceive(map.toString(), "event#doMsg");
        System.out.println("接收到事件的返回参数为：" + list.get(0));
    }

    @EventListener
    public void doMsg(SimpleEvent<String> simpleEvent) {
        System.out.println("EventService 接收：" + simpleEvent.getEventData());
    }

    @EventListener
    public void doMsgAndBackData(SimpleEvent<String> simpleEvent) {
        if (simpleEvent.getTopic() != "event#doMsg") return;
        System.out.println("EventService 接收并返回：" + simpleEvent.getEventData());
        simpleEvent.setResult(Arrays.asList("返回参数"));
    }
}
