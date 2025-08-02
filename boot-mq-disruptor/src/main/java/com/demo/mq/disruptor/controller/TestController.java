package com.demo.mq.disruptor.controller;

import com.demo.mq.disruptor.producer.MyEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @Autowired
    private MyEventProducer producer;

    @GetMapping("/publish/{message}")
    public String publishMessage(@PathVariable String message) {
        producer.publishEvent(message);
        return "Message published: " + message;
    }
}