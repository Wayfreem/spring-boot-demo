package com.demo.task.mulitThread.config;

import com.demo.task.mulitThread.entity.Task;
import com.demo.task.mulitThread.task.AbstractTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 分发基础任务
 */
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
