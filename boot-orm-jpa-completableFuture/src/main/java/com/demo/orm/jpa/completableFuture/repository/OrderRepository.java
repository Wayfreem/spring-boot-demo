package com.demo.orm.jpa.completableFuture.repository;

import com.demo.orm.jpa.completableFuture.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
