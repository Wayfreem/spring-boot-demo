package com.demo.undertow.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @RequestMapping("get")
    public Map get(){
        return Map.of("key", "No.1");
    }
}
