package com.demo.task.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuq
 * @Time 2022-5-26 17:48
 * @Description
 */
@RestController
public class TestController {

    @Autowired
    private DynamicTask task;

    @RequestMapping("start")
    public void startTask() {
        task.startCron();
    }

    // http://localhost:8080/stopById?taskId=%E4%BB%BB%E5%8A%A1%E4%B8%80
    @RequestMapping("stopById")
    public void stopById(String taskId) {
        task.stop(taskId);
    }

    @RequestMapping("stopAll")
    public void stopAll() {
        task.stopAll();
    }
}
