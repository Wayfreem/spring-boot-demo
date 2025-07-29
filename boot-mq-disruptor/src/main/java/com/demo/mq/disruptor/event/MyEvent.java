package com.demo.mq.disruptor.event;

/**
 * 定义事件类
 */
public class MyEvent {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
