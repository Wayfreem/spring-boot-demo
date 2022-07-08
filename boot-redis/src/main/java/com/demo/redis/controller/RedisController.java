package com.demo.redis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuq
 * @Time 2022-7-8 14:18
 * @Description
 */
@RestController
public class RedisController {

    @RequestMapping("getKey")
    public String getKey(){
        return "";
    }
}
