package com.demo.orm.mybatis.dynamic.datasource.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Teacher implements Serializable{

    private Long id;

    private String name;

    private int sex;

    private String office;
}
