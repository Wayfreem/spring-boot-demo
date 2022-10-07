package com.demo.retry.controller;

import com.demo.retry.service.RetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RetryController {

    @Autowired
    private RetryService retryService;

    @RequestMapping("test")
    public int test(int code){
        return retryService.retry(code);
    }
}
