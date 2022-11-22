package com.demo.orm.jpa.dynamic.datasource.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Student implements Serializable{

    private Long id;

    private String name;

    private int sex;

    private String grade;
}
