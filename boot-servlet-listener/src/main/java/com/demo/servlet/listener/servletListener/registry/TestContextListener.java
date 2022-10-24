package com.demo.servlet.listener.servletListener.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 用于监听程序的启动与销毁
 */
public class TestContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(TestContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("程序加载中 。。。。");

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("程序销毁中 。。。。");
    }
}