
不知道大伙有没有这种感觉啊，就是过了一段时间再去看之间自己写的代码，就总有一种这代码我当时是怎么写出来的？做代码重构的时候，总会发现很多代码有一种屎山的味道？

这里呢，我就将之前还在公司里面重构销售业务的代码抽取出来，做一个例子来说明下：

## 工程地址
我把最后的工程放入到了这个地方：[Github 源码路径，点击这里查看](https://github.com/Wayfreem/spring-boot-demo/tree/main/boot-designPattern-simpleDemo)

## 原始的业务处理
原始的业务逻辑处理呢，就是大概就是这样子的写的，每次有一个类型需要增加，就继续增加一个 `if...else...`，显而易见，这个最开始业务少的时候，还可以维护下，等到后面业务多了，想想都可以知道有多头疼

```java
@Service
public class SaleOrderService {


    public SaleOrder execute(SaleOrder saleOrder){
        // 业务逻辑
        System.out.println("执行业务逻辑");

        if ("wechatOrder".equalsIgnoreCase(saleOrder.getOrderType())) {
            //  执行逻辑
        }

        if ("unionPayOrder".equalsIgnoreCase(saleOrder.getOrderType())) {
            //  执行逻辑
        }

        if ("alipayOrder".equalsIgnoreCase(saleOrder.getOrderType())) {
            //  执行逻辑
        }

        System.out.println("执行业务逻辑");

        return saleOrder;
    }

}
```

## 第一次重构设计
这次我们将相关的不同类型抽取出来，然后通过 `if...else..` 判断，这里是初步的抽取了下
```java
public interface OrderProcessor {
    void process(SaleOrder saleOrder);
}

@Service
public class AlipayOrderProcessor implements OrderProcessor {
    @Override
    public void process(SaleOrder saleOrder) {
        System.out.println("支付宝订单处理");
    }
}

@Service
public class WechatOrderProcessor implements OrderProcessor {
    @Override
    public void process(SaleOrder saleOrder) {
        System.out.println("微信订单处理");
    }
}

@Service
public class UnionPayOrderProcessor implements OrderProcessor {
    @Override
    public void process(SaleOrder saleOrder) {
        System.out.println("银联订单处理");
    }
}


@Service
public class SaleOrderService {
	 @Autowired
     private UnionPayOrderProcessor unionPayOrderProcessor ;  
     @Autowired
     private WechatOrderProcessor wechatOrderProcessor;  
     @Autowired
     private AlipayOrderProcessor alipayOrderProcessor;

	public void process(String code) {  
         if ("unionPayOrder".equals(code)) {
             unionPayOrderProcessor.pay();  
         } else if ("wechatOrder".equals(code)) { 
              wechatOrderProcessor.pay();  
         } else if ("alipayOrder".equals(code)) {
              alipayOrderProcessor.pay();  
         } else {
              System.out.println("找不到对应的处理方式");  
         }
     }
}
```

虽然这样子看上去好了蛮多，但是依然还是存在问题，如果后面继续增加其他类型的销售单据，那么这里就会存在更多的 `if...else...`。

比如说，后面我们又接入了建行，兴业银行等等，这里就有需要修改 `SaleOrderService` 中的 `process` 方法。

这里看上去就是违背了 `开闭原则` :

> 开闭原则：对扩展开放，对修改关闭。就是说增加新功能要尽量少改动已有代码。


## 第二次重构设计

这次重构的目的在于，消除掉 `if...else...` 这段处理，我们不需要每次增加的时候还需要去修改这段逻辑。

并且呢，我们在每个实现类中增加 `support` 方法来判断是否可以自己处理的，这里我是使用的 `equalsIgnoreCase()` 来做的判断，如果需要严谨点，可以直接使用 `equals()` 来判断：
```java
public interface OrderProcessor {

    void process(SaleOrder saleOrder);

    boolean support(String type);
}


@Service
public class AlipayOrderProcessor implements OrderProcessor {
    @Override
    public void process(SaleOrder saleOrder) {
        System.out.println("支付宝订单处理");
    }

	 @Override
    public boolean support(String type) {
        return "alipayOrder".equalsIgnoreCase(type);
    }
}

@Service
public class WechatOrderProcessor implements OrderProcessor {
    @Override
    public void process(SaleOrder saleOrder) {
        System.out.println("微信订单处理");
    }

	@Override
    public boolean support(String type) {
        return "unionPayOrder".equalsIgnoreCase(type);
    }
}

@Service
public class UnionPayOrderProcessor implements OrderProcessor {
    @Override
    public void process(SaleOrder saleOrder) {
        System.out.println("银联订单处理");
    }

	@Override
    public boolean support(String type) {
        return "wechatOrder".equalsIgnoreCase(type);
    }
}
```

这里其实没有太大的区别，那么我们看下怎么获取到对应的处理器把：

```java
@Component
public class OrderProcessorFactory implements ApplicationListener<ContextRefreshedEvent> {

    private static List<OrderProcessor> orderProcessList = null;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (orderProcessList == null) {
            orderProcessList = new ArrayList<>();
            Map<String, OrderProcessor> beansOfType = event.getApplicationContext().getBeansOfType(OrderProcessor.class);
            beansOfType.forEach((key, value) -> orderProcessList.add(value));
        }
    }

    public static OrderProcessor get(String orderType) {
        OrderProcessor result = null;
        for (OrderProcessor process : orderProcessList) {
            if (process.support(orderType)) {
                result = process;
                break;
            }
        }
        if (Objects.isNull(result)) throw new RuntimeException(orderType + " 没有找到对应的处理器，请检查");
        return result;
    }
}
```

这段代码实现 `ApplicationListener` 接口，在容器刷新时，获取所有的 OrderProcess 实现类，并存储到 List 中，核心的部分就在于这里`event.getApplicationContext().getBeansOfType(OrderProcessor.class)` 从容器中获取到对应的处理器集合，然后在调用的时候，通过 `get()` 来遍历下，获取到指定的处理器返回。



## 第三次重构

正常情况下，我们能重构到第二步的时候就已经可以了，但是后面发现：
- 首先，我们会有一部分**公共校验的逻辑**在，不能说每个处理器都去处理下，比如校验金额，订单状态等，这样子代码就重复冗余了。
- 其次，走完公共校验了之后，**需要能回调**再去走一次我们每个单据特有的逻辑

这里可以结合源码来看，就清晰点：[Github 源码路径，点击这里查看](https://github.com/Wayfreem/spring-boot-demo/tree/main/boot-designPattern-simpleDemo)

那么我们继续改改，这次我增加了一个抽象类，把公共的处理部分抽取出来，然后使用 Java8 的新特性作为回调：
```java
@Slf4j
public abstract class AbstractOrderProcessor implements OrderProcessor {

    /**
     * 抽象类的前置处理
     */
    protected void beforeProcess(SaleOrder saleOrder, Consumer<SaleOrder> consumer) {
        // 执行通用的处理，比如：判断金额
        if (saleOrder.getAmount() < 0) {
            throw new RuntimeException("金额不能小于0");
        }

        log.info("===执行通用处理===");

        // 执行回调
        consumer.accept(saleOrder);
    }


    /**
     * 抽象类的后置处理
     */
    protected void afterProcess() {

    }
}
```
上面的代码中，将我们需要执行的方法作为一个参数 `Consumer<SaleOrder> consumer` 来回调 `consumer.accept(saleOrder);` 由于我们的 `SaleOrder` 都是同一个指针地址，在公共处理的地方赋值了之后，回调的时候是可以获取到的，这样子可以满足我们获取值的需求。

那我们看下怎么传入调用的：
```java
@Slf4j
@Service
public class WechatOrderProcessor extends AbstractOrderProcessor {

    @Override
    public void process(SaleOrder saleOrder) {
        // 微信下单逻辑
        log.info("---微信订单开始执行---");

        beforeProcess(saleOrder, this::wechatBeforeProcess);

        log.info("---微信订单执行完毕---");
    }

    private void wechatBeforeProcess(SaleOrder saleOrder) {
        // 微信特定的前置处理
        log.info("---微信特定的前置处理---");
    }


    @Override
    public boolean support(String type) {
        return "wechatOrder".equalsIgnoreCase(type);
    }
}
```

在上面的代码中，使用 `this::wechatBeforeProcess` 双冒号的语法，这里如果大家不熟悉这个语法的，可以看下我之前写的博客，[Java8新特性之方法引用中的双冒号](https://blog.csdn.net/qq_18948359/article/details/86361344)，这里就不赘述了，其实也很好理解的


最后看看是怎么调用的

```java
@Service
public class SaleOrderService {


    public Map test(SaleOrder saleOrder) {

        // 获取订单处理器
        OrderProcessor orderProcessor = OrderProcessorFactory.get(saleOrder.getOrderType());

        // 执行订单处理器
        orderProcessor.process(saleOrder);

        return Map.of("code", "200");
    }
}
```

这里是给大家有一个思路在这里，我在项目上面是在订单保存之前有一个处理，保存之后也有一个处理，都是通过双冒号的方式进行回调，这样子就可以做到共用与个性化区分开了。
