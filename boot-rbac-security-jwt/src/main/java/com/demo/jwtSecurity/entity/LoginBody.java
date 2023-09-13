package com.demo.jwtSecurity.entity;

import lombok.Data;

/**
 * @author wuq
 * @Time 2023-9-8 11:37
 * @Description
 */
@Data
public class LoginBody {

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;
}
