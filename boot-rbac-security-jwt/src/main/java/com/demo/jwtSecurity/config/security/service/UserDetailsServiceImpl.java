package com.demo.jwtSecurity.config.security.service;

import cn.hutool.core.util.ObjectUtil;
import com.demo.jwtSecurity.config.security.entity.LoginUser;
import com.demo.jwtSecurity.entity.SysUser;
import com.demo.jwtSecurity.exception.ServiceException;
import com.demo.jwtSecurity.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户验证处理(登录的时候会问这里，来通过username查询账号)
 *
 * @author wuq
 * @Time 2023-9-8 11:33
 * @Description
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.selectUserByUserName(username);
        if (ObjectUtil.isEmpty(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("登录用户：" + username + " 不存在");
        }

        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user) {

        // 这里为了测试先写死 role
        Set<GrantedAuthority> authorities = new HashSet<>();
        if ("admin".equals(user.getUserName())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + "ADMIN"));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + "USER"));
        } else if ("user".equals(user.getUserName())){
            authorities.add(new SimpleGrantedAuthority("ROLE_" + "USER"));
        }

        return new LoginUser(user.getUserId(), user, authorities);
    }
}
