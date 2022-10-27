package com.demo.orm.jpa.domainEvents.repository;

import com.demo.orm.jpa.domainEvents.model.SaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleOrderRepository extends JpaRepository<SaleOrder, Long> {
}
