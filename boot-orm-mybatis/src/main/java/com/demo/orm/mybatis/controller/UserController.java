package com.demo.orm.mybatis.controller;

import com.demo.orm.mybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuq
 * @Time 2022-6-29 12:43
 * @Description
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;


}
