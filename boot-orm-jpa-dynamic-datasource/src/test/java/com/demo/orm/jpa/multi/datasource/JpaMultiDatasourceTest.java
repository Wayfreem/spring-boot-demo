package com.demo.orm.jpa.multi.datasource;

import com.demo.orm.jpa.multi.datasource.model.primary.Student;
import com.demo.orm.jpa.multi.datasource.model.second.Teacher;
import com.demo.orm.jpa.multi.datasource.repository.primary.StudentRepository;
import com.demo.orm.jpa.multi.datasource.repository.second.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JpaMultiDatasourceTest {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Test
    public void userSave() {
        Student studentDO = new Student();
        studentDO.setName("bug creator");
        studentDO.setSex(1);
        studentDO.setGrade("一年级");
        studentRepository.save(studentDO);

        Teacher teacherDO = new Teacher();
        teacherDO.setName("Java乐园");
        teacherDO.setSex(2);
        teacherDO.setOffice("语文");
        teacherRepository.save(teacherDO);
    }
}
