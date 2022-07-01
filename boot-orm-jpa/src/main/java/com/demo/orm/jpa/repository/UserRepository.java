package com.demo.orm.jpa.repository;

import com.demo.orm.jpa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author wuq
 * @Time 2022-7-1 17:32
 * @Description
 */
public interface UserRepository extends JpaRepository<User, String> {
}
