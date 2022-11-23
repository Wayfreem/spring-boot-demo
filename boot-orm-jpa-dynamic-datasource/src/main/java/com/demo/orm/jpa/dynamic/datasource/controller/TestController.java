package com.demo.orm.jpa.dynamic.datasource.controller;

import com.demo.orm.jpa.dynamic.datasource.annotation.DataSource;
import com.demo.orm.jpa.dynamic.datasource.model.Student;
import com.demo.orm.jpa.dynamic.datasource.repository.StudentRepository;
import com.demo.orm.jpa.dynamic.datasource.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;


    @GetMapping("/{name}/list")
    public List<Student> list(@PathVariable("name") String name){
        System.out.println(name);
        return studentRepository.findAll();
    }

    @DataSource(name="primary-source")
    @PostMapping(value="/primary")
    public Object findAll() {
        return studentRepository.findAll();
    }

    @DataSource(name="second-source")
    @PostMapping(value="/second")
    public Object findAll2() {
        return teacherRepository.findAll();
    }
}
