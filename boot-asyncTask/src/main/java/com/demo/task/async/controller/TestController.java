package com.demo.task.async.controller;

import com.demo.task.async.service.AsyncTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private AsyncTaskService asyncTaskService;


    @RequestMapping("test")
    public Map test() {

        for(int i =0 ;i<10;i++){
            asyncTaskService.executeAsyncTask(i);
            asyncTaskService.executeAsyncTaskPlus(i);
        }
        return Map.of("msg", "success");
    }
}
