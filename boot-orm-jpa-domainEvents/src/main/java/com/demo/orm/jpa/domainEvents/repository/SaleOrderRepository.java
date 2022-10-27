package com.demo.orm.jpa.domainEvents.repository;

import com.demo.test.jpa.model.SaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleOrderRepository extends JpaRepository<SaleOrder, Long> {
}
