package com.demo.task.mulitThread.service;

import com.demo.task.mulitThread.config.TaskManager;
import com.demo.task.mulitThread.config.TaskType;
import com.demo.task.mulitThread.entity.Task;
import com.demo.task.mulitThread.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    @Autowired
    private TaskManager taskManager;
    @Autowired
    private TaskMapper taskMapper;

    /**
     * 模拟发送消息
     */
    public void sendAsyncMsg() {
        // 构建任务，延时执行
        Map map = Map.of("customer", "Jim", "content", "Hello World! This is a msg!");
        taskManager.noRunTask(TaskType.SEND_MSG, map);
    }

    public void sendSyncMsg(){
        Map map = Map.of("customer", "Jim", "content", "Hello World! This is a msg!");
        taskManager.runTask(TaskType.SEND_MSG, map);
    }

    /**
     * 模拟邮件消息
     */
    public void sendAsyncEmail() {
        // 构建任务，延时执行
        Map map = Map.of("customer", "Tom", "content", "Hello World! This is a email!");
        taskManager.noRunTask(TaskType.SEND_MSG, map);
    }

    public void sendSyncEmail() {
        // 构建任务，延时执行
        Map map = Map.of("customer", "Tom", "content", "Hello World! This is a email!");
        taskManager.runTask(TaskType.SEND_MSG, map);
    }



    /**
     * 模拟执行任务
     */
    public void executeTask() {
        List<Task> allTaskList = taskMapper.getAllTaskList();
        for (Task task : allTaskList) {
            taskManager.execute(task.getId());
        }
    }
}
