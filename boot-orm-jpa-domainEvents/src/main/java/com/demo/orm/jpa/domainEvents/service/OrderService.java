package com.demo.orm.jpa.domainEvents.service;

import com.demo.test.jpa.model.Order;
import com.demo.test.jpa.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wuq
 * @Time 2022-10-24 14:25
 * @Description
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public Order save(Order orderEntity){
        System.out.println("***** CustomerId: " + orderEntity.getCustomerId());
        orderEntity.confirmReceived();
        orderEntity = orderRepository.saveAndFlush(orderEntity);
        return orderEntity;
    }
}
