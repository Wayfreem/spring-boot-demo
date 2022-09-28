package com.demo.orm.jpa.multi.datasource.model.primary;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Student")
public class Student implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name") // 若实体属性和表字段名称一致时，可以不用加@Column注解
    private String name;

    @Column(name = "sex")
    private int sex;

    @Column(name = "grade")
    private String grade;
}
