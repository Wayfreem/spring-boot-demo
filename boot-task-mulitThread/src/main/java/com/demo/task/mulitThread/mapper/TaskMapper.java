package com.demo.task.mulitThread.mapper;

import com.demo.task.mulitThread.entity.Task;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TaskMapper {

    @Update("UPDATE scm_task SET status = 1, retries = retries + 1 " +
            "WHERE id = #{id} AND (status = 0 OR status = 2) AND retries < 4")
    int updateLock(Task taskDO);

    @SelectKey(statement = "SELECT last_insert_id()", keyProperty = "id", before = false, resultType = Long.class)
    @Insert("INSERT IGNORE INTO scm_task (create_time, modified_time, " +
            "params, param_hash, type, status, retries, description) " +
            "VALUES (#{createTime}, #{modifiedTime}, #{params}, #{paramHash}, " +
            "#{type}, #{status}, 0, #{description})")
    int insertIgnore(Task taskDO);

    /**
     * 查询所有任务，这里增加限制，防止未执行的任务过多
     */
    @Select("select * from task order by create_Time limit 100")
    List<Task> getAllTaskList();

    @Update("UPDATE scm_task " +
            "SET gmt_modified = #{gmtModified}, result = #{result}, status = #{status} " +
            "WHERE id = #{id}")
    int updateStatus(Task task);

    @Delete("delete from task where id = #{id}")
    int deleteByPrimaryKey(Long task);

    @Select("select * from task where id = #{id}")
    Task selectByPrimaryKey(Long id);

    @Select("select * from task where param_hash = #{generate}")
    Long findByHash(String generate);
}
