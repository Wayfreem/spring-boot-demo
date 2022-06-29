package com.demo.event.cutomize.controller;

import com.demo.event.cutomize.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuq
 * @Time 2022-6-29 17:18
 * @Description
 */
@RestController
public class EventController {

    @Autowired
    private EventService eventService;

    @RequestMapping("send")
    public void send() {
        eventService.send();
    }


    @RequestMapping("sendAndReceive")
    public void sendAndReceive() {
        eventService.sendAndReceive();
    }
}
