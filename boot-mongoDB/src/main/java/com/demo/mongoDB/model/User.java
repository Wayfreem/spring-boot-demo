package com.demo.mongoDB.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author wuq
 * @Time 2022-7-15 14:23
 * @Description
 */
@Data
@Document(collation = "test")   // 这里指定对应的集合名称
public class User implements Serializable {

    @Id
    private String id;

    private String userId;

    private String fileName;

    public User(){
    }

    public User(String id, String userId, String fileName) {
        this.id = id;
        this.userId = userId;
        this.fileName = fileName;
    }
}
