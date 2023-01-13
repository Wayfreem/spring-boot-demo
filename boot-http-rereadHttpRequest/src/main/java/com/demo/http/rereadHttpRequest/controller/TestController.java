package com.demo.http.rereadHttpRequest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wuq
 * @Time 2023-1-13 11:07
 * @Description
 */
@RestController
public class TestController {

    @RequestMapping("test")
    public String test(HttpServletRequest request){

        return "请求成功";
    }
}
