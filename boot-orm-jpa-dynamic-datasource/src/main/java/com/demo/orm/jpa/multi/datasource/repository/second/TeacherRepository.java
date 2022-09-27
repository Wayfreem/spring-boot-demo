package com.demo.orm.jpa.multi.datasource.repository.second;

import com.demo.orm.jpa.multi.datasource.model.second.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
