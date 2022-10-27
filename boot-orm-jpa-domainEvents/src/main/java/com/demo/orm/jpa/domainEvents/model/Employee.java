package com.demo.orm.jpa.domainEvents.model;

import lombok.Data;

import javax.persistence.*;

/**
 * @author wuq
 * @Time 2022-10-18 17:23
 * @Description
 */
@Entity
@Table(name = "Employee", schema = "HR")
@NamedQueries({
        @NamedQuery(name = "Employee.findAllEmployee", query = "select e from Employee e"),
        @NamedQuery(name = "findNamedEmployee", query = "select e from Employee e")
})
@Data
public class Employee {

    @Id
    private String id;
    private String name;
    private String email;
    private String lastname;
}
