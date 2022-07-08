package com.demo.redis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author wuq
 * @Time 2022-7-8 14:20
 * @Description
 */
@Service
public class RedisOperator {

    @Autowired
    private RedisTemplate redisTemplate;

    public String getKey(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void setKey(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

}
