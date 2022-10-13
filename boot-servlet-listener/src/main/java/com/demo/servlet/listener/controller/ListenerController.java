package com.demo.servlet.listener.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author wuq
 * @Time 2022-10-13 9:46
 * @Description
 */
@RestController
public class ListenerController {

    @RequestMapping("find")
    public String find(){
        return "查询所有";
    }

    @RequestMapping("getUser")
    public Map getUser(String id){
       return Map.of("id", id);
    }
}
