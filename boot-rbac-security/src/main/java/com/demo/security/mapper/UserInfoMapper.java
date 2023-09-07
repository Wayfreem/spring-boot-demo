package com.demo.security.mapper;

import com.demo.security.entity.Menu;
import com.demo.security.entity.Role;

import java.util.List;

/**
 * @author wuq
 * @Time 2023-9-6 17:04
 * @Description
 */
public interface UserInfoMapper {

    /**
     * 根据用户 Id 查询用户角色
     *
     * @param userId
     * @return
     */
    List<Role> selectRoleByUserId(Long userId);

    /**
     * 根据用户 Id 查询菜单
     *
     * @param userId
     * @return
     */
    List<Menu> selectMenuByUserId(Long userId);
}
