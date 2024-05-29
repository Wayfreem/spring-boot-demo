package com.demo.pattern.model;

import lombok.Data;

@Data
public class SaleOrder {


    /**
     * 订单ID
     */
    private String id;

    /**
     * 订单金额
     */
    private Double amount;


    /**
     * 订单类型
     */
    private String orderType;


    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 订单创建时间
     */
    private Long createTime;
}
