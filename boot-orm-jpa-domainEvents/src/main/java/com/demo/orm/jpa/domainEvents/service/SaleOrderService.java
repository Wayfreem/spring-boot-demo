package com.demo.orm.jpa.domainEvents.service;

import com.demo.orm.jpa.domainEvents.model.SaleOrder;
import com.demo.orm.jpa.domainEvents.repository.SaleOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wuq
 * @Time 2022-10-24 14:25
 * @Description
 */
@Service
public class SaleOrderService {

    @Autowired
    private SaleOrderRepository orderRepository;

    @Transactional
    public SaleOrder save(SaleOrder orderEntity){
        System.out.println("***** CustomerId: " + orderEntity.getCustomerId());
        orderEntity = orderRepository.saveAndFlush(orderEntity);
        return orderEntity;
    }
}
