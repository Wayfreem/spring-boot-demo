package com.demo.orm.jpa.dynamic.datasource.repository;

import com.demo.orm.jpa.dynamic.datasource.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
