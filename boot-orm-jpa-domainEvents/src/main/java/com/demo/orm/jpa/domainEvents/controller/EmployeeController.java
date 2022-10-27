package com.demo.orm.jpa.domainEvents.controller;

import com.demo.orm.jpa.domainEvents.model.Employee;
import com.demo.orm.jpa.domainEvents.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wuq
 * @Time 2022-10-21 15:09
 * @Description
 */
@RestController
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @RequestMapping("save")
    public Employee save() {
        return employeeService.save();
    }

    @RequestMapping("findAllEmployee")
    public List<Employee> findAllEmployee() {
        return employeeService.findAllEmployee();
    }

    @RequestMapping("findAllNamed")
    public List<Employee> findAllNamed() {
        return employeeService.findAllNamed();
    }
}
