package com.demo.jwtSecurity.controller;

import cn.hutool.core.util.StrUtil;
import com.demo.jwtSecurity.config.security.entity.LoginUser;
import com.demo.jwtSecurity.config.security.manager.TokenService;
import com.demo.jwtSecurity.entity.AjaxResult;
import com.demo.jwtSecurity.entity.LoginBody;
import com.demo.jwtSecurity.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gxs
 */
@RestController
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/user/all")
    public AjaxResult getUserAll() {
        return AjaxResult.success(sysUserService.list());
    }

    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        // 用户验证：该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginBody.getUsername(), loginBody.getPassword()));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 生成token
        String token = tokenService.createToken(loginUser);

        Map<String, String> resultMap = new HashMap<>(2);
        resultMap.put("token", token);
        resultMap.put("username", loginBody.getUsername());
        return AjaxResult.success(resultMap);
    }

    @PostMapping("/register")
    public AjaxResult register(@RequestBody LoginBody user) {
        String msg = sysUserService.register(user);
        return StrUtil.isEmpty(msg) ? AjaxResult.success() : AjaxResult.error(msg);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String user(){
        return "Hello USER!";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin(){
        return "Hello ADMIN!";
    }
}
