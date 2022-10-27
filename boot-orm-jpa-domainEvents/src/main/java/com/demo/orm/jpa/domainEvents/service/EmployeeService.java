package com.demo.orm.jpa.domainEvents.service;

import com.demo.test.jpa.model.Employee;
import com.demo.test.jpa.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * @author wuq
 * @Time 2022-10-21 15:10
 * @Description
 */
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EntityManager entityManager;


    public Employee save(){
        Employee employee = new Employee();
        employee.setId("T00002");
        employee.setEmail("lxx@qq.com");
        employee.setName("lxx");
        employee.setLastname("Smith");
        return employeeRepository.saveAndFlush(employee);
    }


    public List<Employee> findAllEmployee(){
        return employeeRepository.findAllEmployee();
    }

    public List<Employee> findAllNamed(){
       return (List<Employee>) entityManager.createNamedQuery("findNamedEmployee").getResultList();
    }
}
