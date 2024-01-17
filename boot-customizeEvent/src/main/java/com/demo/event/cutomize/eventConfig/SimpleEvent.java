package com.demo.event.cutomize.eventConfig;

import java.util.List;

/**
 * 增加额外的属性
 *
 * @author wuq
 * @create 2019-09-21 15:27
 */
public class SimpleEvent<T> extends BaseEvent {

    private String topic;
    private List result;

    public SimpleEvent(Object source) {
        super(source);
    }

    public SimpleEvent(Object source, T eventData) {
        super(source, eventData);
    }

    public SimpleEvent(Object source, T eventData, String topic) {
        super(source, eventData);
        this.topic = topic;
    }

    public String getTopic() {
        return this.topic;
    }

    public List getResult() {
        return result;
    }

    public void setResult(List result) {
        this.result = result;
    }
}
