package com.demo.pattern.decorator.controller;

import com.demo.pattern.decorator.service.InvoiceDecorator;
import com.demo.pattern.decorator.service.InvoiceInterface;
import com.demo.pattern.decorator.service.SecondDecorator;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    @Qualifier("saleOrderInvoice")
    private InvoiceInterface saleOrderInvoice;

    @Autowired
    @Qualifier("retailOrderInvoice")
    private InvoiceInterface retailInvoice;

    @GetMapping("/printInvoice")
    public void printInvoice() {
        saleOrderInvoice.printInvoice();
        retailInvoice.printInvoice();
    }

    /**
     * 打印发票，包含装饰器
     */
    @GetMapping("/printInvoiceWithDecorator")
    public void printInvoiceWithDecorator() {
        InvoiceInterface saleOrderInvoiceDecorator = new InvoiceDecorator(saleOrderInvoice);
        saleOrderInvoiceDecorator.printInvoice();
    }


    /**
     * 打印发票，第二级装饰器
     */
    @GetMapping("/printInvoiceWithSecondDecorator")
    public void printInvoiceWithSecondDecorator() {
        // 第一次包装
        InvoiceInterface saleOrderInvoiceDecorator = new InvoiceDecorator(saleOrderInvoice);

        // 第二次包装
        InvoiceInterface secondDecorator = new SecondDecorator(saleOrderInvoiceDecorator);
        secondDecorator.printInvoice();
    }

}
