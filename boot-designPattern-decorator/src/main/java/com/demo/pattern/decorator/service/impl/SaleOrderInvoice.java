package com.demo.pattern.decorator.service.impl;

import com.demo.pattern.decorator.service.InvoiceInterface;
import org.springframework.stereotype.Service;

@Service
public class SaleOrderInvoice implements InvoiceInterface {

    @Override
    public void printInvoice() {
        System.out.println("打印销售发票");
    }
}
