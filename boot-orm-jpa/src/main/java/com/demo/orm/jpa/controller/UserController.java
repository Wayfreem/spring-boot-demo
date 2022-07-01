package com.demo.orm.jpa.controller;

import com.demo.orm.jpa.model.User;
import com.demo.orm.jpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuq
 * @Time 2022-7-1 17:38
 * @Description
 */
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("save")
    public User save(){
        return userService.save();
    }


}
