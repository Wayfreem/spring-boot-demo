package com.demo.pattern.service.process;

import com.demo.pattern.model.SaleOrder;
import com.demo.pattern.service.AbstractOrderProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 银联订单
 */
@Slf4j
@Service
public class UnionPayOrderProcessor extends AbstractOrderProcessor {

    @Override
    public void process(SaleOrder saleOrder) {
        // 银联下单逻辑
        // 微信下单逻辑
        log.info("---微信订单开始执行---");



        log.info("---微信订单执行完毕---");
    }

    private void UnionPayBeforeProcess(SaleOrder saleOrder) {
        // 支付宝下单前的处理
    }

    @Override
    public boolean support(String type) {
        return "unionPay".equalsIgnoreCase(type);
    }
}
