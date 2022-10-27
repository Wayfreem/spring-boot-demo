package com.demo.orm.jpa.domainEvents.controller;

import com.demo.orm.jpa.domainEvents.model.Order;
import com.demo.orm.jpa.domainEvents.service.OrderService;
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
public class OrderController {
    @Autowired
    private OrderService orderService;

    @RequestMapping("/createOrder")
    public long createOrder(@RequestBody Order orderEntity){
        orderEntity = orderService.save(orderEntity);
        return orderEntity.getId();
    }
}
