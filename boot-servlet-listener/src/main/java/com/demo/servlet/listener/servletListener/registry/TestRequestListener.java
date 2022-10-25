package com.demo.servlet.listener.servletListener.registry;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 对当前的请求监听
 */
public class TestRequestListener implements ServletRequestListener {

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        System.out.println("request   Destroyed:" + "," + new Date());
        System.out.println("当前訪问次数：" + servletRequestEvent.getServletContext().getAttribute("count"));
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        System.out.println("request   Initialized:" + "," + new Date());
        Object count = servletRequestEvent.getServletContext().getAttribute("count");

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequestEvent.getServletRequest();
        httpServletRequest.getSession();        // 触发 session 操作

        Integer cInteger = 0;
        if (count != null) {
            cInteger = Integer.valueOf(count.toString());
        }
        System.out.println("历史訪问次数：" + count);
        cInteger++;
        servletRequestEvent.getServletContext().setAttribute("count", cInteger);
    }
}
