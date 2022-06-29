package com.demo.event.cutomize.eventConfig;

import org.springframework.context.ApplicationEvent;

public abstract class BaseEvent<T> extends ApplicationEvent {

    protected T eventData;

    public BaseEvent(Object source) {
        super(source);
    }

    public BaseEvent(Object source, T eventData) {
        super(source);
        this.eventData = eventData;
    }

    public T getEventData(){
        return this.eventData;
    }

    public void setEventData(T eventData){
        this.eventData = eventData;
    }
}
