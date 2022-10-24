package com.demo.servlet.listener.servletListener.annotation;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AnnotationServletListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("WebListener.UserListener ---->>>  ServletContext 初始化 ");
    }

    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("WebListener.UserListener ---->>>  ServletContext 销毁 ");
    }
}
