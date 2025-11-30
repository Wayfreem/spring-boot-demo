package com.demo.task.multiTreadSimple.config;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class TaskConfig implements InitializingBean, DisposableBean {

    private ThreadPoolExecutor threadPoolExecutor;

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private static final AtomicInteger threadNumber = new AtomicInteger(1);

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }


    @Override
    public void afterPropertiesSet() {
        initThreadPool();
    }

    private void initThreadPool() {
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
                    log.error("任务队列线程池已满，任务{}被拒绝", r);
                });
        threadPoolExecutor.allowCoreThreadTimeOut(false);
        log.info("TaskQueueManager.threadPoolExecutor.init");
    }

    @Override
    public void destroy() throws Exception {
        threadPoolExecutor.shutdown();
        while (!threadPoolExecutor.isTerminated()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        log.info("TaskQueueManager.threadPoolExecutor.shutdown");
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
}
