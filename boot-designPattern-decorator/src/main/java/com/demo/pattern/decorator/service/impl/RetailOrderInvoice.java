package com.demo.pattern.decorator.service.impl;

import com.demo.pattern.decorator.service.InvoiceInterface;
import org.springframework.stereotype.Service;

@Service
public class RetailOrderInvoice implements InvoiceInterface {

    @Override
    public void printInvoice() {
        System.out.println("打印零售发票");
    }
}
