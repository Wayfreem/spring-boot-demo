package com.demo.pattern.decorator.service;

/**
 * 第二级装饰器
 */
public class SecondDecorator implements InvoiceInterface {

    private final InvoiceInterface invoiceInterface;

    /**
     * 构造函数
     *
     * @param invoiceInterface 被装饰的发票接口
     */
    public SecondDecorator(InvoiceInterface invoiceInterface) {
        this.invoiceInterface = invoiceInterface;
    }

    @Override
    public void printInvoice() {
        System.out.println("第二次包装：调用第一级装饰器逻辑之前执行");

        invoiceInterface.printInvoice();

        System.out.println("第二次包装：调用第一级装饰器逻辑之后执行");
    }
}
