package com.demo.orm.mybatis.dynamic.datasource.controller;

import com.demo.orm.mybatis.dynamic.datasource.mapper.StudentMapper;
import com.demo.orm.mybatis.dynamic.datasource.mapper.TeacherMapper;
import com.demo.orm.mybatis.dynamic.datasource.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        if(name.equals("primary")){
            return studentMapper.queryAll();
        }else{
            return teacherMapper.queryAll();
        }
    }
}
