package com.demo.retry.config;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

/**
 * 使用 RetryTemple 时，需要增加监听器，监听重试的过程
 * @author wuq
 * @Time 2022-10-9 15:27
 * @Description
 */
public class SimpleRetryListener extends RetryListenerSupport {

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        System.out.println("=======================================================================");
         System.out.println("监听到重试过程开启了");
        return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
         System.out.println("监听到重试过程关闭了");
         System.out.println("=======================================================================");
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
         System.out.println("监听到重试过程错误了");
    }


}