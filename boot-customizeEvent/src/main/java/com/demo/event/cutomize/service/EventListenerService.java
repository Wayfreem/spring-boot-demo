package com.demo.event.cutomize.service;

import com.demo.event.cutomize.eventConfig.SimpleEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @author wuq
 * @Time 2022-6-29 18:59
 * @Description
 */
@Service
public class EventListenerService {

    @EventListener
    private void eventListener(SimpleEvent simpleEvent){
        System.out.println("EventListenerService 接收：" + simpleEvent.getEventData());
    }
}
