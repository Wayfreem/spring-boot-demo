package com.demo.orm.jpa.completableFuture.service;

import com.demo.orm.jpa.completableFuture.model.Order;
import com.demo.orm.jpa.completableFuture.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public Order findById(Long id){
        return orderRepository.findById(id).get();
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }


    @Transactional
    public void bizOrderMethod(Long id){
        Order order = findById(id);

        //..... 此处模拟一些业务操作，第一次改变 order 里面的值；
        try {
            Thread.sleep(200L);// 加上复杂业务耗时200毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        order.setOrderNo("First_" + order.getOrderNo());
        save(order);


        //..... 此处模拟一些业务操作，第二次改变 order 里面的值；
        try {
            Thread.sleep(300L);// 加上复杂业务耗时300毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        order.setOrderNo("Second_" + order.getOrderNo());
        save(order);
    }
}