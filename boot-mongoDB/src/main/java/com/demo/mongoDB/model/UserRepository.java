package com.demo.mongoDB.model;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author wuq
 * @Date 2021-7-14
 */
public interface UserRepository extends MongoRepository<User, String> {
}
