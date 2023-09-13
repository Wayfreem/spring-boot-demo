package com.demo.jwtSecurity.config.security.handle;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.demo.jwtSecurity.entity.AjaxResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证失败处理类 返回未授权
 * @author wuq
 * @Time 2023-9-8 17:44
 * @Description
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        int code = HttpStatus.UNAUTHORIZED.value();
        String msg = StrUtil.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
        ServletUtil.write(response, JSONUtil.toJsonStr(AjaxResult.error(code, msg)), "application/json; charset=utf-8");
    }
}
