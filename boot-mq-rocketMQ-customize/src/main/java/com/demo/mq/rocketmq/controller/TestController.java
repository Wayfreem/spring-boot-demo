package com.demo.mq.rocketmq.controller;

import com.demo.mq.rocketmq.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping("/hello")
    public String hello() {
        return testService.hello();
    }

    @RequestMapping("/mq")
    public String testMq(){
        return testService.testMq();
    }

    @RequestMapping("/sendMq")
    public String sendMsg(String msg){
        return testService.sendMsg(msg);
    }

    @RequestMapping("/sendMsgAndTag")
    public String sendMsgAndTag(String msg){
        return testService.sendMsgAndTag(msg);
    }

}
