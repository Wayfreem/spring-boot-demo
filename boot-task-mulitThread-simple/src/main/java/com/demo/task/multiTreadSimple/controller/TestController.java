package com.demo.task.multiTreadSimple.controller;

import com.alibaba.fastjson2.JSONObject;
import com.demo.task.multiTreadSimple.config.TaskConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TaskConfig taskConfig;

    /**
     * 获取线程池监控信息
     *
     * @return 线程池监控信息
     */
    @RequestMapping("/monitor")
    public JSONObject getMonitor() {
        return taskConfig.monitor();
    }
}
