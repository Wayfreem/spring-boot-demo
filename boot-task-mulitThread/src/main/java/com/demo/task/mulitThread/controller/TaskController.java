package com.demo.task.mulitThread.controller;

import com.demo.task.mulitThread.service.TaskService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api")
public class TaskController {

    @Resource
    private TaskService taskService;

    /**
     * 发送消息，不立即执行
     */
    @RequestMapping("sendAsyncMsg")
    public void sendAsyncMsg() {
        taskService.sendAsyncMsg();
    }

    /**
     * 发送邮件，不立即执行
     */
    @RequestMapping("sendAsyncEmail")
    public void sendAsyncEmail() {
        taskService.sendAsyncEmail();
    }

    /**
     * 发送消息，立即执行
     */
    @RequestMapping("sendSyncMsg")
    public void sendSyncMsg() {
        taskService.sendSyncMsg();
    }

    /**
     * 发送邮件，立即执行
     */
    @RequestMapping("sendSyncEmail")
    public void sendSyncEmail() {
        taskService.sendSyncEmail();
    }


    /**
     * 执行任务, 模拟定时任务执行
     */
    @RequestMapping("execute")
    public void execute() {
        taskService.executeTask();
    }
}
