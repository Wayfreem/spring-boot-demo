package com.demo.jwtSecurity.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.jwtSecurity.config.security.manager.SecurityUtils;
import com.demo.jwtSecurity.entity.LoginBody;
import com.demo.jwtSecurity.entity.SysUser;
import com.demo.jwtSecurity.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

/**
 * @author wuq
 * @Time 2023-9-8 11:35
 * @Description
 */
@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    public SysUser selectUserByUserName(String username) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_name", username);
        return this.getOne(queryWrapper);
    }

    public String register(LoginBody user) {
        SysUser sysUser = selectUserByUserName(user.getUsername());
        if (ObjectUtil.isEmpty(sysUser)) {
            return "保存用户'" + user.getUsername() + "'失败，注册账号已存在";
        }
        sysUser = new SysUser();
        sysUser.setUserName(user.getUsername());
        // 注册的时候密码一定要进行加密，否则注册的账号是不能用的
        sysUser.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        this.save(sysUser);
        return null;
    }
}
