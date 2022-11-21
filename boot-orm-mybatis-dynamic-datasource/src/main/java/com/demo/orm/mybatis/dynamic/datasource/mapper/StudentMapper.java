package com.demo.orm.mybatis.dynamic.datasource.mapper;

import com.demo.orm.mybatis.dynamic.datasource.model.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentMapper {

    int save(@Param("studentDO") Student student);

    List<Student> queryAll();
}
