package com.demo.orm.mybatis.dynamic.datasource.mapper;

import com.demo.orm.mybatis.dynamic.datasource.model.Student;
import com.demo.orm.mybatis.dynamic.datasource.model.Teacher;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface TeacherMapper {

    int save(@Param("teacherDO") Teacher teacher);

    List<Student> queryAll();
}
