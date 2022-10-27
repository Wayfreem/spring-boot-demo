## 说明

这里是说明下使用 JPA 的域模型事件。可以简单的理解就是，在保存或者是做其他操作的时候，发送一个事件出去。松耦合的调用。

主要的实现方式有两种：
- AggregateRoot 方式
- @DomainEvents

### AggregateRoot 方式

使用 AggregateRoot 方式时，需要实现 AbstractAggregateRoot 接口，调用 RegisterEvent() 方法发布事件。

对于这种操作的核心在于，需要继承 AbstractAggregateRoot，然后发送一个指定的事件，后面在接收的时候，也是按照这个事件来接收。

**第一步：定义事件**

这里是自定义一个事件类型，发送与接收都是基于这个事件来。

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderFinishedEvent {
    private Long Id;
    private Long customerId;
    private String eventData;
}
```

**第二步：定义发送事件的方法**

这里是在实体类中继承了 AbstractAggregateRoot 类，然后发送一个指定的事件

```java
@Entity
@Data
@Table(name = "T_order")
public class Order extends AbstractAggregateRoot<Order> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String orderNo;
    private Long customerId;
    private int status;

    /**
     * 注册领域事件
     * @return
     */
    public Order confirmReceived(){
        //todo 业务逻辑
        //发布领域事件
        registerEvent(new OrderFinishedEvent(this.getId(), this.customerId, "订单完成啦！"));
        return this;
    }
}
```

**第三步：监听发布的事件**

这里是用于接收发送的事件消息
```java
@Component
@Slf4j
public class OrderEventReceiver {

    @Autowired
    private EmployeeService service;

//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void handleOrderFinished(OrderFinishedEvent event) {
        log.info("================Order finished event handler==================");
        if(Objects.isNull(event)) {
            return;
        }
        System.out.println(event.toString());
        service.save();    // 进行另外的事务操作
        throw new RuntimeException("测试事务是否回滚");    // 用于测试事务是否回滚
    }

    /* condition 中的格式是需要以 #开头，后面的名称就是参数列表的名称 */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, condition = "#event.getCustomerId().toString() == '125435' ")
    public void handleOrderCondition(OrderFinishedEvent event) {
        log.info("================订单完成，条件输入==================");
        if(Objects.isNull(event)) {
            return;
        }
        System.out.println(event);
        service.save();
        throw new RuntimeException("测试事务是否回滚");
    }
}
```

**第四步：测试**

访问的 controller

```java
@RestController
public class OrderController {
@Autowired
private OrderService orderService;

    @RequestMapping("/createOrder")
    public long createOrder(@RequestBody Order orderEntity){
        orderEntity = orderService.save(orderEntity);
        return orderEntity.getId();
    }
}
```

访问的 Service

```java
@Service
public class OrderService {
@Autowired
private OrderRepository orderRepository;

    @Transactional
    public Order save(Order orderEntity){
        System.out.println("***** CustomerId: " + orderEntity.getCustomerId());
        orderEntity.confirmReceived();    // 这里发送事件
        return orderRepository.saveAndFlush(orderEntity);
    }
}
```

访问请求

```http request
POST /createOrder HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Content-Length: 58

{"orderNo":"0002034","customerId":"125435","status":"1"}
```

#### @EventListener 与 @TransactionalEventListener

针对上面第三步的注解 @EventListener 与 @TransactionalEventListener 来说明。

这里使用 @EventListener 与 @TransactionalEventListener  都可以实现事件的监听，但是两者是有区别的。

##### @EventListener

我们先看下 @EventListener 注解，这个注解来自于Spring 事件，在这个包下面，

    org.springframework.context.event;

使用 @EventListener 做为监听的时候，会发现当前的操作是同步操作（虽然在程序上面解耦了），这里有两种情况：

- 如果是在 handleOrderFinished() 方法（事件的监听方法）里面报错了，那么所有的操作都回滚，如上面的操作
- 如果是发送事件的方法报错，那么事务也会回滚，下面的操作

```java
@Transactional
public Order save(Order orderEntity){
    System.out.println("***** CustomerId: " + orderEntity.getCustomerId());
    orderEntity.confirmReceived();
    orderEntity = orderRepository.saveAndFlush(orderEntity);
     int a = 1/0;
    return orderEntity;
}
```

##### @TransactionalEventListener

看下部分源码，会发现所在的包就不一样，并且 也使用了 @EventListener 注解作为标注，就类似于 @TransactionalEventListener 是 @EventListener 子类的感觉

```java
package org.springframework.transaction.event;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EventListener
public @interface TransactionalEventListener {
    // ... 省略
}
```

对于 `@TransactionalEventListener` 中的参数 `TransactionPhase`，有好几种情况：

- `TransactionPhase.BEFORE_COMMIT`: 事务提交前
- `TransactionPhase.AFTER_COMMIT`: 事务提交后
- `TransactionPhase.AFTER_ROLLBACK`: 事务回滚后
- `TransactionPhase.AFTER_COMPLETION`: 事务完成后

测试之后发现：
- 在使用 `TransactionPhase.AFTER_COMMIT` 的时候，会发现发送事件之前的事务操作是提交到了数据库中，当前的操作报错回滚了。
- 在使用 `TransactionPhase.BEFORE_COMMIT` 事务提交时，会发现所有的操作都没有被提交。

### @DomainEvents

官方地址

[Spring Data JPA - Reference Documentation](https://docs.spring.io/spring-data/jpa/docs/2.2.6.RELEASE/reference/html/#core.domain-events)


从上面的信息可以知道：

- 指定了 JPA 的 save 方法之后，就会回调 `@DomainEvents` 注解的方法。
- `@DomainEvents` 注解的方法返回的结果类型是 Collection 对象。
- `@AfterDomainEventsPublication` 标注的注解方法，一般是用来清空 `@DomainEvents` 设置的集合数据


#### 具体操作
使用 `@DataEvents` 时，就是在当前的类中增加对应的注解

```java
@Entity
@Data
@Table(name = "SaleOrder")
public class SaleOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String orderNo;
    private Long customerId;
    private int status;


    // @DomainEvents 可以返回单个事件实例或事件集合, @DomainEvents 用来发布时间，触发机制在保存的时候。
    // 批量保存对象时，每个对象都会触发一次事件
    @DomainEvents
    public List<Object> domainEvents(){
        return Stream.of(new SaleOrderEvent(this.getId(), this.customerId, "订单完成啦！")).collect(Collectors.toList());
    }

    // 事件发布后callback
    @AfterDomainEventPublication
    void callback() {
        System.err.println("ok");
        this.domainEvents().clear();    // 事件发布完成之后，清理掉对应的事件
    }
}
```


后面的事件监听和上面的是一样的

```java
@Component
@Slf4j
public class SaleOrderEventReceiver {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderFinished(SaleOrderEvent event) {
        log.info("================Order finished event handler==================");
        if(Objects.isNull(event)) {
            return;
        }
        System.out.println(event.toString());
        throw new RuntimeException("测试事务是否回滚");
    }

    /* condition 中的格式是需要以 #开头，后面的名称就是参数列表的名称 */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, condition = "#event.getCustomerId().toString() == '125435' ")
    public void handleOrderCondition(SaleOrderEvent event) {
        log.info("================订单完成，条件输入==================");
        if(Objects.isNull(event)) {
            return;
        }
        System.out.println(event);
    }
}
```