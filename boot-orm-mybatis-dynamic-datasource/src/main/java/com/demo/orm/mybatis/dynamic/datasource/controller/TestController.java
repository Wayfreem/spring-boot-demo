package com.demo.orm.mybatis.dynamic.datasource.controller;

import com.demo.orm.mybatis.dynamic.datasource.annotation.DataSource;
import com.demo.orm.mybatis.dynamic.datasource.mapper.StudentMapper;
import com.demo.orm.mybatis.dynamic.datasource.mapper.TeacherMapper;
import com.demo.orm.mybatis.dynamic.datasource.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @GetMapping("/{name}/list")
    public List<Student> list(@PathVariable("name") String name){
        System.out.println(name);
        return studentMapper.queryAll();
    }

    @DataSource(name="primary-source")
    @PostMapping(value="/primary")
    public Object findAll() {
        return studentMapper.queryAll();
    }

    @DataSource(name="second-source")
    @PostMapping(value="/second")
    public Object findAll2() {
        return teacherMapper.queryAll();
    }
}
