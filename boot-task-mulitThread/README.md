
## 项目说明
工程是使用线程池的方式来实现，与业务解耦。

具体的实现方式如下：
- 定义一个线程池，在初始化的时候，创建一个线程池，并且启动
- 定义一个任务队列，这里我们使用数据库的表来实现
- 定义一个枚举，用来表示任务的类型

## 大致的执行的流程

创建任务，写入到任务表中，然后定时任务去查询任务表中的数据，使用多线程执行任务表中的任务，这样子的好处就是可以解耦，充分利用线程优势。


### 先创建一个任务表

用于程序执行时，先写入到任务表中。类似于我们在 MQ上面写入本地消息的机制。

```sql
CREATE TABLE scm_task
(
    id            BIGINT UNSIGNED auto_increment COMMENT 'primary key' PRIMARY KEY,
    create_time   datetime NOT NULL COMMENT 'create time',
    modified_time datetime NOT NULL COMMENT 'modify time',
    params        text NULL COMMENT '任务参数',
    param_hash    VARCHAR(128) NULL COMMENT '参数哈希',
    result        text NULL COMMENT '任务结果',
    type          TINYINT NULL COMMENT '任务类别',
    STATUS        TINYINT NULL COMMENT '任务状态',
    retries       INT DEFAULT 0 NULL COMMENT '重试次数',
    description   VARCHAR(32) NULL COMMENT '任务描述'
) COMMENT '异步任务表' COLLATE = utf8mb4_general_ci row_format = DYNAMIC;
```

### 程序执行

通过接口的方式，来执行任务，可以调用下面的接口查看效果

```java
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
```
在对应的 service 中，实现对应的方法，这里我们模拟发送邮件和发送消息的方法，具体的实现方式如下：
```java
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
```

### 任务核心代码说明
通过 [TaskManager.java](src/main/java/com/demo/task/mulitThread/config/TaskManager.java) 来构建任务，具体的实现方式如下： 
```java
/**
     * 构建任务
     * @param taskType 任务类型
     * @param dto 任务参数
     * @param run 是否立即执行任务
     * @param uuid 是否生成uuid
     * @return 任务id
     * @param <T> 任务参数类型
     */
    private <T> SingleResult<Long> build(TaskType taskType, T dto, boolean run, boolean uuid) {
        String generate = UUID.randomUUID().toString();
        Task taskDO = new Task();
        taskDO.setCreateTime(LocalDateTime.now());
        taskDO.setModifiedTime(LocalDateTime.now());
        taskDO.setParams(JSON.toJSONString(dto));
        taskDO.setParamHash(generate);
        if (uuid) {
            generate = UUID.randomUUID().toString();
            taskDO.setParamHash(generate);
        }
        taskDO.setType(taskType.getType());
        taskDO.setStatus(TaskStatus.INIT.getStatus());
        taskDO.setRetries(0);
        taskDO.setDescription(taskType.getMsg());
        if (BooleanUtils.toBoolean(taskMapper.insertIgnore(taskDO))) {
            if (run) {
                // 异步执行任务
                execute(taskDO.getId());
            }
            return SingleResult.success(taskDO.getId());
        }
        Long taskId = taskMapper.findByHash(generate);
        if (taskId == null || taskId == 0) {
            return SingleResult.error(9999, "操作失败");
        }
        if (run) {
            // 异步执行任务
            execute(taskDO.getId());
        }
        return SingleResult.success(taskId);
    }

```

通过 [TaskManager.java](src/main/java/com/demo/task/mulitThread/config/TaskManager.java) 使用线程池来执行任务，具体的实现方式如下：
```java
/**
     * 多线程执行异步任务
     */
    public void execute(Long id) {
        threadPoolExecutor.execute(() -> distributeTask.handle(null, id));
    }
```

在 [DistributeTask.java](src/main/java/com/demo/task/mulitThread/config/DistributeTask.java) 中，通过依赖注入的方式 `private List<AbstractTask<?>> abstractTaskList` 来获取所有的任务的执行器, 具体的实现方式如下：

```java
@Slf4j
@Component
public class DistributeTask extends AbstractTask<Long> {

    /**
     * 当应用程序启动的时候，会通过依赖注入的方式，将所有的任务注入到这个类中
     */
    @Resource
    private List<AbstractTask<?>> abstractTaskList;

    @Override
    protected Class<Long> typeClass() {
        return Long.class;
    }

    @Override
    protected TaskType getType() {
        return TaskType.NIL;
    }

    @Override
    protected boolean clear() {
        return false;
    }

    @Override
    protected String handle(String hashCode, Long id) {
        Task taskDO = taskMapper.selectByPrimaryKey(id);
        if (Objects.isNull(taskDO)){
            return Boolean.TRUE.toString();
        }
        for (AbstractTask<?> abstractTask : abstractTaskList) {
            abstractTask.execute(taskDO);
        }
        return Boolean.TRUE.toString();
    }

}
```
