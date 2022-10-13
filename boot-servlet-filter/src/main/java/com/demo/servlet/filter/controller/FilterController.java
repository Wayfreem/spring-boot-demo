package com.demo.servlet.filter.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author wuq
 * @Time 2022-10-13 9:46
 * @Description
 */
@RestController
public class FilterController {

    @RequestMapping("find")
    public String find(){
        return "查询所有";
    }

    @RequestMapping("getUser")
    public Map getUser(String id){
       return Map.of("id", id);
    }
}
