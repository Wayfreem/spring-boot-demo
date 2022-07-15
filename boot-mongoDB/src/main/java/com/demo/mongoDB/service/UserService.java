package com.demo.mongoDB.service;

import com.demo.mongoDB.model.User;
import com.demo.mongoDB.model.UserRepository;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @author wuq
 * @Time 2022-7-15 14:38
 * @Description
 */
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    public User save(){
        return userRepository.save(new User("00002", "temp02", "java"));
    }

    public User findById(String id){
        return userRepository.findById(id).get();
    }


    public FindIterable<Document> findAll(){
       return mongoTemplate.getCollection("user").find();
    }
}
