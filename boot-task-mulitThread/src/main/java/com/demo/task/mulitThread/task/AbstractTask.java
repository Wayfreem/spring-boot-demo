package com.demo.task.mulitThread.task;

import com.alibaba.fastjson2.JSONObject;
import com.demo.task.mulitThread.config.TaskStatus;
import com.demo.task.mulitThread.config.TaskType;
import com.demo.task.mulitThread.entity.Task;
import com.demo.task.mulitThread.mapper.TaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
public abstract class AbstractTask<T> {

    @Resource
    protected TaskMapper taskMapper;

    protected abstract Class<T> typeClass();

    protected abstract TaskType getType();

    /**
     * 是否需要删除任务
     * @return Boolean
     */
    protected abstract boolean clear();

    /**
     * 执行任务
     * @param hashCode
     * @param command
     * @return
     * @throws Throwable
     */
    protected abstract String handle(String hashCode, T command) throws Throwable;

    protected boolean lock(Task task){
        return taskMapper.updateLock(task) == 1;
    }

    /**
     * 异步任务
     */
    public void execute(Task task){
        if (!getType().isTrue(task.getType())){
            return;
        }
        try {
            // 锁定任务
            if (lock(task)) {
                // 执行任务
                T command = JSONObject.parseObject(task.getParams(), typeClass());
                String paramHash = task.getParamHash();
                AbstractTask abstractTask = (AbstractTask) AopContext.currentProxy();
                String handle = abstractTask.handle(paramHash, command);
                // 是否需要删除任务
                if (clear()){
                    delete(task);
                    return;
                }
                // 完成更新
                update(task, handle, TaskStatus.COMPLETE);
            }
        } catch (RuntimeException e){
            // 任务未完成
            update(task, e.getMessage(), TaskStatus.INIT);
        } catch (Throwable e) {
            // 异常更新
            update(task, e.getMessage(), TaskStatus.EXCEPTION);
        }
    }

    /**
     * 同步任务
     */
    public void executeSync(T command) throws Throwable {
        if (typeClass().isAssignableFrom(command.getClass()) && TaskType.SYNC_TASK.equals(getType())){
            AbstractTask abstractTask = (AbstractTask) AopContext.currentProxy();
            abstractTask.handle(null, command);
        }
    }

    protected void update(Task task, String result, TaskStatus taskStatus){
        task.setCreateTime(LocalDateTime.now());
        task.setResult(result);
        task.setStatus(taskStatus.getStatus());

        int count = taskMapper.updateStatus(task);
        if(count != 1){
            throw new RuntimeException("任务更新失败");
        }
    }

    protected void delete(Task task){
        int count = taskMapper.deleteByPrimaryKey(task.getId());
        if(count != 1){
            throw new RuntimeException("任务删除失败");
        }
    }

}
