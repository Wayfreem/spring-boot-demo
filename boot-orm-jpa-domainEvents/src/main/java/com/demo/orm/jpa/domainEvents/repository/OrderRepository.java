package com.demo.orm.jpa.domainEvents.repository;

import com.demo.test.jpa.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
