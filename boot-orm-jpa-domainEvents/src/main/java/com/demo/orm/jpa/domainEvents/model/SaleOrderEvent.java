package com.demo.orm.jpa.domainEvents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuq
 * @Time 2022-10-24 17:14
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleOrderEvent {
    private Long Id;
    private Long customerId;
    private String eventData;
}
