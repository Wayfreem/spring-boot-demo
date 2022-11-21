package com.demo.orm.mybatis.dynamic.datasource.mapper;

import com.demo.orm.mybatis.dynamic.datasource.model.Teacher;
import org.apache.ibatis.annotations.Param;


public interface TeacherMapper {

    int save(@Param("teacherDO") Teacher teacher);
}
