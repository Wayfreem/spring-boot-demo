package com.demo.orm.mybatis.multi.datasource;

import com.demo.orm.mybatis.multi.datasource.mapper.primary.StudentMapper;
import com.demo.orm.mybatis.multi.datasource.mapper.second.TeacherMapper;
import com.demo.orm.mybatis.multi.datasource.model.Student;
import com.demo.orm.mybatis.multi.datasource.model.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MybatisTest {

    @Autowired
    StudentMapper studentMapper;

    @Autowired
    TeacherMapper teacherMapper;

    @Test
    public void userSave() {
        Student studentDO = new Student();
        studentDO.setName("Mybatis");
        studentDO.setSex(1);
        studentDO.setGrade("一年级");
        studentMapper.save(studentDO);

        Teacher teacherDO = new Teacher();
        teacherDO.setName("Mybatis");
        teacherDO.setSex(2);
        teacherDO.setGrade("语文");
        teacherMapper.save(teacherDO);
    }
}
