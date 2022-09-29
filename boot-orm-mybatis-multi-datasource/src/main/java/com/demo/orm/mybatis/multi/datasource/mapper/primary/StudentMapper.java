package com.demo.orm.mybatis.multi.datasource.mapper.primary;

import com.demo.orm.mybatis.multi.datasource.model.Student;
import org.apache.ibatis.annotations.Param;

public interface StudentMapper {

    int save(@Param("studentDO") Student student);
}