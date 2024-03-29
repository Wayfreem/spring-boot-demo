package com.demo.orm.jpa.domainEvents.repository;

import com.demo.orm.jpa.domainEvents.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findAllEmployee();
}
