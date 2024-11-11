package com.demo.task.mulitThread.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.demo.task.mulitThread.entity.SingleResult;
import com.demo.task.mulitThread.entity.Task;
import com.demo.task.mulitThread.mapper.TaskMapper;
import com.demo.task.mulitThread.task.AbstractTask;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务管理器，用于构建线程池，以及执行任务
 */
@Slf4j
@Component
public class TaskManager implements InitializingBean, DisposableBean {

    @Resource
    private TaskMapper taskMapper;
    @Resource
    private DistributeTask distributeTask;
    @Resource
    private List<AbstractTask<?>> abstractTaskList;

    private ThreadPoolExecutor threadPoolExecutor;

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private static final AtomicInteger threadNumber = new AtomicInteger(1);

    @Override
    public void afterPropertiesSet() throws Exception {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        String namePrefix = String.format("task-exec-%d", poolNumber.getAndIncrement());
        threadPoolExecutor = new ThreadPoolExecutor(
                8,
                16,
                30,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(16),
                r -> {
                    String name = namePrefix + threadNumber.getAndIncrement();
                    Thread t = new Thread(group, r, name, 0L);
                    t.setDaemon(true);
                    return t;
                },
                (r, executor) -> {
                });
        threadPoolExecutor.allowCoreThreadTimeOut(false);
        log.info("TaskManager.threadPoolExecutor.init");
    }

    @Override
    public void destroy() throws Exception {
        threadPoolExecutor.shutdown();
        while (!threadPoolExecutor.isTerminated()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        log.info("TaskManager.threadPoolExecutor.shutdown");
    }

    public JSONObject monitor() {
        log.info("正在工作的线程数：{}", threadPoolExecutor.getActiveCount());
        log.info("当前存在的线程数：{}", threadPoolExecutor.getPoolSize());
        log.info("历史最大的线程数：{}", threadPoolExecutor.getLargestPoolSize());
        log.info("已提交的任务总数：{}", threadPoolExecutor.getTaskCount());
        log.info("已完成的任务数：{}", threadPoolExecutor.getCompletedTaskCount());
        log.info("队列中的任务数：{}", threadPoolExecutor.getQueue().size());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("正在工作的线程数", threadPoolExecutor.getActiveCount());
        jsonObject.put("当前存在的线程数", threadPoolExecutor.getPoolSize());
        jsonObject.put("历史最大的线程数", threadPoolExecutor.getLargestPoolSize());
        jsonObject.put("已提交的任务总数", threadPoolExecutor.getTaskCount());
        jsonObject.put("已完成的任务数", threadPoolExecutor.getCompletedTaskCount());
        jsonObject.put("队列中的任务数", threadPoolExecutor.getQueue().size());
        return jsonObject;
    }

    /**
     * 多线程执行异步任务
     */
    public void execute(Long id) {
        threadPoolExecutor.execute(() -> distributeTask.handle(null, id));
    }

    /**
     * 执行同步任务
     */
    public <T> void execute(T command) throws Throwable {
        for (AbstractTask abstractTask : abstractTaskList) {
            abstractTask.executeSync(command);
        }
    }

    /**
     * 构建任务并执行
     */
    public <T> SingleResult<Long> runTask(TaskType taskType, T dto) {
        return build(taskType, dto, true, false);
    }

    /**
     * 构建任务不执行
     */
    public <T> SingleResult<Long> noRunTask(TaskType taskType, T dto) {
        return build(taskType, dto, false, false);
    }

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

}
