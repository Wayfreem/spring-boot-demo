package com.demo.emailThymeleaf.controller;

import com.demo.emailThymeleaf.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@Validated
public class TestController {

    @Autowired
    TestService testService;

    @GetMapping("test")
    public String test(){
        testService.sendTemplateEmail();
        return "hello";
    }
}
