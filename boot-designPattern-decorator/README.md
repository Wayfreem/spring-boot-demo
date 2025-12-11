## 介绍下装饰器模式
> 装饰器模式（Decorator Pattern） 也称为包装模式(Wrapper Pattern) 是指在不改变原有对象的基础之上，将功能附加到对象上，提供了比继承更有弹性的替代方案(扩展原有对象的功能)，属于结构型模式。

装饰器模式的核心是功能扩展，使用装饰器模式可以透明且动态地扩展类的功能。

### 通俗理解：
**就是 我不动原先的业务东西，但是 又想给这个业务东西 加点额外的职责东西。**
装饰器模式有两个特点：**就是无入侵，可以拼凑**，但是注意最好不要包含超过三层，这里不是说不可以这样子去写代码，而是这样子包太多层了之后，很难维护。

## 代码示例

### 源码结构
![源码结构](https://i-blog.csdnimg.cn/direct/0ee077c0690f4083bfd51a918f02a75b.png)

### 基础框架
我们先搭建一个框架出来，下面是模拟销售场景下的发票业务，下面有销售单以及零售单两种场景

```java
/**
 * 发票接口
 */
public interface InvoiceInterface {

    /**
     * 打印发票
     */
    void printInvoice();
}

@Service
public class SaleOrderInvoice implements InvoiceInterface {

    @Override
    public void printInvoice() {
        System.out.println("打印销售发票");
    }
}

@Service
public class RetailOrderInvoice implements InvoiceInterface {

    @Override
    public void printInvoice() {
        System.out.println("打印零售发票");
    }
}
```
然后我们写一个 controller 来做测试，通过 @Qualifier 来指定对应的业务来打印发票就好

```java
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

}
```
测试下基础的结构，浏览器访问
```
http://localhost:8080/test/printInvoice
```
![测试基础框架](https://i-blog.csdnimg.cn/direct/b88798a5368a41daa009b7a37fd6805e.png)

### 第一次包装
由于我们现在，需要在原有增加额外的业务逻辑，那么我们就需要将 `saleOrderInvoice` 进行包装

```java
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
```
通过装饰器包装之后，我们看下怎么调用，这个时候我们在 `TestController` 中增加一个访问接口：

```java
/**
  * 打印发票，包含装饰器
  */
 @GetMapping("/printInvoiceWithDecorator")
 public void printInvoiceWithDecorator() {
     InvoiceInterface saleOrderInvoiceDecorator = new InvoiceDecorator(saleOrderInvoice);
     saleOrderInvoiceDecorator.printInvoice();
 }
```
测试下第一次包装的结构，浏览器访问：
```
	http://localhost:8080/test/printInvoiceWithDecorator
```
![测试第一次包装](https://i-blog.csdnimg.cn/direct/648f686fa47844e0bcfc1dc1aa3e654d.png)
### 第二次包装
上面我们是包装了一次，接下来我们来看下怎么进行第二次包装（也就是多层包装）, 这里我们直接继承 `InvoiceDecorator` 就好，使用多态的方式实现减少代码冗余。
```java
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
```
那再看下怎么调用的，这样子就可以实现多层包装
```java
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
```
我们现在来测试下第二次包装：
```
http://localhost:8080/test/printInvoiceWithSecondDecorator
```
![测试第二次包装](https://i-blog.csdnimg.cn/direct/82e765090afd4091991f61def6dc8c56.png)

好了，现在就是就已经实现了多层包装了，不太建议在项目上面包太多层，不然我们使用设计模式的初衷就变了（因为导致源码难以维护了）。
