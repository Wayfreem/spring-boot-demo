package com.demo.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.demo.security.entity.Menu;
import com.demo.security.entity.Role;
import com.demo.security.entity.Users;
import com.demo.security.mapper.UserInfoMapper;
import com.demo.security.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 根据账号查询用户密码，顺便判断账户是否存在。
 * 将从数据库查询出来的账号密码，放到 User 对象当中并返回。
 *
 * UserDetailsService 接口：主要作用就是返回主体，并且主体当中会携带授权（授权这个权可以是菜单权限，也可以是角色权限）。
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        QueryWrapper<Users> wrapper = new QueryWrapper();
        wrapper.eq("username", s);
        Users users = usersMapper.selectOne(wrapper);

        if (users == null) {
            throw new UsernameNotFoundException("用户名不存在！");
        }
        // 获取用户角色、菜单列表
        List<Role> roles = userInfoMapper.selectRoleByUserId(users.getId());
        List<Menu> menus = userInfoMapper.selectMenuByUserId(users.getId());

        // 声明一个集合List<GrantedAuthority>, 将角色和菜单权限都加入进去
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        // 处理角色
        for (Role role:roles){
            // 这个地方品拼接的 "ROLE_" 不能删除
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + role.getName());
            grantedAuthorityList.add(simpleGrantedAuthority);
        }
        // 处理权限
        for (Menu menu:menus){
            grantedAuthorityList.add(new SimpleGrantedAuthority(menu.getPermission()));
        }
        return new User(users.getUsername(), users.getPassword(), grantedAuthorityList);
    }
}
