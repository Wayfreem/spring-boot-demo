package com.demo.orm.mybatis.controller;

import com.demo.orm.mybatis.entity.User;
import com.demo.orm.mybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wuq
 * @Time 2022-6-29 12:43
 * @Description
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("getUser/{id}")
    public String GetUser(@PathVariable int id){
        return userService.selectById(id).toString();
    }

    /**
     * 访问地址
     * http://localhost:6060/getUserByName?name=管理员
     * @param name
     * @return
     */
    @RequestMapping("getUserByName")
    public List<User> getUserByName(String name) {
        return userService.selectByName(name);
    }

    /**
     * 访问地址
     * http://localhost:6060/getUserByPwd?pwd=123456
     * @param pwd
     * @return
     */
    @RequestMapping("getUserByPwd")
    public List<User> getUserByPwd(String pwd){
        return userService.selectByPwd(pwd);
    }
}
