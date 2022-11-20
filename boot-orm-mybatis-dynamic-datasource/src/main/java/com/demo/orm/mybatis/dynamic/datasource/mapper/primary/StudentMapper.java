package com.demo.orm.mybatis.dynamic.datasource.mapper.primary;

import com.demo.orm.mybatis.dynamic.datasource.model.Student;
import org.apache.ibatis.annotations.Param;

public interface StudentMapper {

    int save(@Param("studentDO") Student student);
}
