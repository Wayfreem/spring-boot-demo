package com.demo.orm.mybatis.multi.datasource.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class Student implements Serializable{

    private Long id;

    private String name;

    private int sex;

    private String grade;
}