package com.demo.servlet.listener.servletListener.registry;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Date;


public class TestSessionListener implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent arg0) {
        System.out.println("session  Created:" + "," + new Date());
        Object lineCount = arg0.getSession().getServletContext().getAttribute("lineCount");
        Integer count = 0;
        if (lineCount == null) {
            lineCount = "0";
        }
        count = Integer.valueOf(lineCount.toString());
        count++;
        System.out.println("新上线一人，历史在线人数：" + lineCount + "个,当前在线人数有： " + count + " 个");
        arg0.getSession().getServletContext().setAttribute("lineCount", count);
    }

    public void sessionDestroyed(HttpSessionEvent arg0) {
        System.out.println("session  Destroyed:" + "," + new Date());
        Object lineCount = arg0.getSession().getServletContext().getAttribute("lineCount");
        Integer count = Integer.valueOf(lineCount.toString());
        count--;
        System.out.println("一人下线。历史在线人数：" + lineCount + "个，当前在线人数: " + count + " 个");
        arg0.getSession().getServletContext().setAttribute("lineCount", count);
    }
}
