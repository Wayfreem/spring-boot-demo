package com.demo.orm.jpa.multi.datasource.repository.primary;

import com.demo.orm.jpa.multi.datasource.model.primary.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
