package com.demo.redis.listener;

import com.demo.redis.config.RedisMessageListener;
import org.springframework.stereotype.Service;

/**
 * @Author wuq
 * @Date 2021-8-18
 */
@Service
public class OrderListener {

    @RedisMessageListener(topic = "order::getState")
    public void getState(String msg){
        System.out.println("OrderListener --->  getState ---->" + msg);
    }

    @RedisMessageListener(topic = "order::getState")
    public void onMessage(String msg){
        System.out.println("OrderListener --->  onMessage ---->" + msg);
    }
}
