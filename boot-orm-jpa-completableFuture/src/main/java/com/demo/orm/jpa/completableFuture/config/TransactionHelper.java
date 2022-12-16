package com.demo.orm.jpa.completableFuture.config;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Component
public class TransactionHelper {

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class}) //可以根据实际业务情况，指定明确的回滚异常
    public void execute(Consumer consumer, Object o) {
        consumer.accept(o);
    }
}