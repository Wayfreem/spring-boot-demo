package com.demo.task.mulitThread.controller;

import com.alibaba.fastjson2.JSONObject;
import com.demo.task.mulitThread.config.TaskManager;
import com.demo.task.mulitThread.entity.SingleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TaskPoolController {

    @Autowired
    private TaskManager taskManager;

    @GetMapping(value = "/task/monitor")
    public SingleResult<JSONObject> monitor() {
        return SingleResult.success(taskManager.monitor());
    }
}
