package com.demo.pattern.decorator.service;

/**
 * 发票装饰器
 */
public class InvoiceDecorator implements InvoiceInterface {
    /**
     * 被装饰的发票接口
     */
    private final InvoiceInterface invoiceInterface;

    /**
     * 构造函数
     * @param invoiceInterface 被装饰的发票接口
     */
    public InvoiceDecorator(InvoiceInterface invoiceInterface) {
        this.invoiceInterface = invoiceInterface;
    }

    @Override
    public void printInvoice() {
        // 装饰前的操作，这里可以调用其他独立的方法
        System.out.println("装饰前的操作");

        invoiceInterface.printInvoice();

        // 装饰后的操作，这里可以调用其他独立的方法
        System.out.println("装饰后的操作");
    }
}
