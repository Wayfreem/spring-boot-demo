package com.demo.mongoDB.controller;

import com.demo.mongoDB.model.User;
import com.demo.mongoDB.service.UserService;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuq
 * @Time 2022-7-15 14:37
 * @Description
 */
@RestController
public class TestController {

    @Autowired
    UserService userService;

    @RequestMapping("save")
    public User save() {
        return userService.save();
    }

    @RequestMapping("findById")
    public User findById(String id) {
        return userService.findById(id);
    }

    @RequestMapping("findAll")
    public FindIterable<Document> findAll() {
        return userService.findAll();
    }
}
