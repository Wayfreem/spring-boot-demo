package com.demo.pattern.service.process;

import com.demo.pattern.model.SaleOrder;
import com.demo.pattern.service.AbstractOrderProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 微信订单
 */
@Slf4j
@Service
public class WechatOrderProcessor extends AbstractOrderProcessor {

    @Override
    public void process(SaleOrder saleOrder) {
        // 微信下单逻辑
        log.info("---微信订单开始执行---");

        beforeProcess(saleOrder, this::wechatBeforeProcess);

        log.info("---微信订单执行完毕---");
    }

    private void wechatBeforeProcess(SaleOrder saleOrder) {
        // 微信特定的前置处理
        log.info("---微信特定的前置处理---");
    }


    @Override
    public boolean support(String type) {
        return "wechatOrder".equalsIgnoreCase(type);
    }
}
