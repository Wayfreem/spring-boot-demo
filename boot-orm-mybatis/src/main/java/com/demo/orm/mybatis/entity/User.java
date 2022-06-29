package com.demo.orm.mybatis.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {

    private Integer id;

    private String name;

    private String origin; //操作帐号

    private String password; //密码

    private String email;   // 邮件

}
