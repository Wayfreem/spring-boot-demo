package com.demo.orm.jpa.service;

import com.demo.orm.jpa.model.User;
import com.demo.orm.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wuq
 * @Time 2022-7-1 17:39
 * @Description
 */
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User save(){
        User user = new User();
        user.setId("0001");
        user.setEmail("wuq@google.com");
        user.setName("wuq");
        user.setLastname("Q");
        return userRepository.saveAndFlush(user);
    }

    public User findByName(String name){
        return userRepository.findByNameEquals(name);
    }
}
