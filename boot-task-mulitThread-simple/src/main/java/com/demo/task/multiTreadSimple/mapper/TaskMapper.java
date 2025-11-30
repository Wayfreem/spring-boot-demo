package com.demo.task.multiTreadSimple.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.scheduling.config.Task;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {

}
