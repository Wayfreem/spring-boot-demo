package com.demo.pattern.service.process;

import com.demo.pattern.model.SaleOrder;
import com.demo.pattern.service.AbstractOrderProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 支付宝订单
 */
@Slf4j
@Service
public class AlipayOrderProcessor extends AbstractOrderProcessor {

    @Override
    public void process(SaleOrder saleOrder) {
        // 执行处理
        System.out.println("支付宝下单成功");
    }


    private void alipayBeforeProcess(SaleOrder saleOrder) {
        // 支付宝下单前的处理

    }

    @Override
    public boolean support(String type) {
        return "alipay".equalsIgnoreCase(type);
    }
}
