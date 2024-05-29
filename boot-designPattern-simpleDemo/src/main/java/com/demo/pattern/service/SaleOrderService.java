package com.demo.pattern.service;

import com.demo.pattern.model.SaleOrder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SaleOrderService {


    public Map test(SaleOrder saleOrder) {

        // 获取订单处理器
        OrderProcessor orderProcessor = OrderProcessorFactory.get(saleOrder.getOrderType());

        // 执行订单处理器
        orderProcessor.process(saleOrder);

        return Map.of("code", "200");
    }
}
