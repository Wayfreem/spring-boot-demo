package com.demo.orm.mybatis.mapper;

import com.demo.orm.mybatis.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wuq
 * @create 2019-06-26 10:56
 */
@Repository
@Mapper
public interface UserMapper {

    User selectById(String id);

    List<User> selectByName(String name);

    @Select("select * from user where lastname = #{lastname}")
    List<User> getUserByLastname(@Param("lastname") String lastname);
}
