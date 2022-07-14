package com.demo.redis.controller;

import com.demo.redis.config.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuq
 * @Time 2022-7-8 14:18
 * @Description
 */
@RestController
public class RedisController {

    @Autowired
    RedisOperator redisOperator;

    @RequestMapping("setKey")
    public void setKey(){
        redisOperator.setKey("tempKey", "value");
    }

    @RequestMapping("getKey")
    public String getKey(){
        return redisOperator.getKey("tempKey");
    }

    @RequestMapping("lock")
    public Boolean lock(){
        return redisOperator.lock("tempKey");
    }

    @RequestMapping("lockWithSecond")
    public Boolean lockWithSecond(){
        return redisOperator.lock("tempKey", 3);
    }

    @RequestMapping("sendMsg")
    public void sendMsg(String msg){
        redisOperator.sendMsg(msg);
    }
}
