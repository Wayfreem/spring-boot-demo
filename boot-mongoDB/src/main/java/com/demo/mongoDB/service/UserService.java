package com.demo.mongoDB.service;

import com.demo.mongoDB.model.User;
import com.demo.mongoDB.model.UserRepository;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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


    public List<Document> findAll(){
        // 经过测试这里不能返回 iterable，如果直接返回 iterable， 是获取不到值的
        FindIterable<Document> iterable = mongoTemplate.getCollection("user").find();
        MongoCursor<Document> cursor =  iterable.iterator();
        List<Document> result = new ArrayList<>();
        while (cursor.hasNext()){
            Document document = cursor.next();
            result.add(document);
        }
        return result;
    }

}
