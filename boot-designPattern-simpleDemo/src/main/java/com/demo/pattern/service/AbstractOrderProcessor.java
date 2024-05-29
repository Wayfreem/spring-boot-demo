package com.demo.pattern.service;

import com.demo.pattern.model.SaleOrder;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public abstract class AbstractOrderProcessor implements OrderProcessor {

    /**
     * 抽象类的前置处理
     */
    protected void beforeProcess(SaleOrder saleOrder, Consumer<SaleOrder> consumer) {

        // 执行通用的处理，比如：判断金额
        if (saleOrder.getAmount() < 0) {
            throw new RuntimeException("金额不能小于0");
        }

        log.info("===执行通用处理===");

        // 执行回调
        consumer.accept(saleOrder);
    }


    /**
     * 抽象类的后置处理
     */
    protected void afterProcess() {

    }


}
