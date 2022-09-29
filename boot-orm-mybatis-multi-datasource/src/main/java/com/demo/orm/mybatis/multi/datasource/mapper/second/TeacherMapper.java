package com.demo.orm.mybatis.multi.datasource.mapper.second;

import com.demo.orm.mybatis.multi.datasource.model.Teacher;
import org.apache.ibatis.annotations.Param;


public interface TeacherMapper {

    int save(@Param("teacherDO") Teacher teacher);
}