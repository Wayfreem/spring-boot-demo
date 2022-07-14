package com.demo.redis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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

    /**
     * 如果成功加上锁就返回为 true
     * @param lockId 锁的编码
     * @return Boolean 加锁是否成功
     */
    public Boolean lock(String lockId){
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockId, "lock");
        return success != null && success;
    }

    public Boolean lock(String lockId, long second){
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockId, "lock", second, TimeUnit.SECONDS);
        return success != null && success;
    }

}
