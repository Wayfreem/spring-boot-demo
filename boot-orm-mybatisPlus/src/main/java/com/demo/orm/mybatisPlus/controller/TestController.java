package com.demo.orm.mybatisPlus.controller;

import com.demo.orm.mybatisPlus.mapper.UserMapper;
import com.demo.orm.mybatisPlus.model.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wuq
 * @Time 2022-7-21 14:46
 * @Description
 */
@RestController
public class TestController {

    @Resource
    private UserMapper userMapper;


    @RequestMapping("selectUser")
    public List<User> selectUser() {
        List<User> userList = userMapper.selectList(null);
        userList.forEach(System.out::println);
        return userList;
    }

    @RequestMapping("insert")
    public void insert() {
        User user = new User();
        user.setId(2l);
        user.setAge(20);
        user.setName("测试员10");
        int count = userMapper.insert(user);
        System.out.println("插入记录数：" + count);
    }

    @RequestMapping("upate")
    public List<User> update() {
        User user = new User();
        user.setId(10l);
        user.setUpdateTime(LocalDateTime.now());
        int count = userMapper.updateById(user);
        System.out.println("更新记录数：" + count);
        return selectUser();
    }

}
