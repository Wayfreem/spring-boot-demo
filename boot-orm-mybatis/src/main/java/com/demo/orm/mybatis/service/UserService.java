package com.demo.orm.mybatis.service;

import com.demo.orm.mybatis.entity.User;
import com.demo.orm.mybatis.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wuq
 * @Time 2022-6-29 12:45
 * @Description
 */
@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public User selectById(String id) {
        return userMapper.selectById(id);
    }

    public List<User> selectByName(String name){
        return userMapper.selectByName(name);
    }

    public List<User> getUserByLastname(String lastName){
        return userMapper.getUserByLastname(lastName);
    }
}
