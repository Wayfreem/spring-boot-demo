package com.demo.servlet.filter.servletFilter.annotation;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @author wuq
 * @Time 2022-10-13 11:38
 * @Description
 */
@WebFilter(filterName = "annotationFilter", urlPatterns = "/*")
public class AnnotationFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig){
        System.out.println("WebFilter   ------------>>>   init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("WebFilter   ------------>>>   doFilter");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy(){
        System.out.println("WebFilter   ------------>>>   destroy");
    }
}
