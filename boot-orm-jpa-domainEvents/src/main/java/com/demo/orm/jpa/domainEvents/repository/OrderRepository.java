package com.demo.orm.jpa.domainEvents.repository;

import com.demo.orm.jpa.domainEvents.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
