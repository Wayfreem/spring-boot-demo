package com.demo.orm.jpa.dynamic.datasource.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Teacher")
public class Teacher implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name") // 若实体属性和表字段名称一致时，可以不用加@Column注解
    private String name;

    private int sex;

    private String office;
}
