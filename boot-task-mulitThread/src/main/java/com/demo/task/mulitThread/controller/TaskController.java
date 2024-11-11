package com.demo.task.mulitThread.controller;

import com.demo.task.mulitThread.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @RequestMapping("sendMsg")
    public void sendMsg() {
        taskService.sendMsg();
    }

    @RequestMapping("sendEmail")
    public void sendEmail() {
        taskService.sendEmail();
    }


    /**
     * 执行任务, 模拟定时任务执行
     */
    @RequestMapping("execute")
    public void execute() {
        taskService.executeTask();
    }
}
