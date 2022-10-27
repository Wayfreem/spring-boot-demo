package com.demo.orm.jpa.domainEvents.controller;

import com.demo.test.jpa.model.SaleOrder;
import com.demo.test.jpa.service.SaleOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuq
 * @Time 2022-10-24 14:26
 * @Description
 */
@RestController
public class SaleOrderController {
    @Autowired
    private SaleOrderService orderService;

    @RequestMapping("/createSaleOrder")
    public long createOrder(@RequestBody SaleOrder orderEntity){
        orderEntity = orderService.save(orderEntity);
        return orderEntity.getId();
    }
}
