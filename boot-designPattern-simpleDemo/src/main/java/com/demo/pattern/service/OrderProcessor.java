package com.demo.pattern.service;

import com.demo.pattern.model.SaleOrder;

public interface OrderProcessor {

    void process(SaleOrder saleOrder);

    boolean support(String type);
}
