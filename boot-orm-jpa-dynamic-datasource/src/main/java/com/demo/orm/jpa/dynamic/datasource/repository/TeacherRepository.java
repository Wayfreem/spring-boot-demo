package com.demo.orm.jpa.dynamic.datasource.repository;

import com.demo.orm.jpa.dynamic.datasource.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
