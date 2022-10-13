package com.demo.servlet.filter.servletFilter.registry;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 自定义的测试Filter
 */
public class TestSingleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("自定义过滤器 TestSingleFilter 加载，拦截 init。。。" );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        System.out.println("自定义过滤器 TestSingleFilter 触发，拦截 url:" + request.getRequestURI());
        filterChain.doFilter(servletRequest, servletResponse);  // 执行后续的 filter
    }

    @Override
    public void destroy() {
    }
}

