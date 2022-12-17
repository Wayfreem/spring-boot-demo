## 说明
在 JPA 中使用 CompletableFuture 提升处理效率。

由于使用 CompletableFuture 是使用异步线程，会出现事务不一致的情况，下面我们会一点点去的测试，使用 CompletableFuture 达到我们预想的效果。

## 项目搭建

### 第一步：引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!--  jpa 依赖     -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.29</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>

<dependency>
    <groupId>com.integralblue</groupId>
    <artifactId>log4jdbc-spring-boot-starter</artifactId>
    <version>1.0.2</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>2.0.14</version>
</dependency>
```

### 第二步：增加配置文件内容

在 mysql 的链接中，增加参数 `logger=Slf4JLogger&profileSQL=true` 可以看到事务日志信息

```properties
server.port=8080

# jpa config
## add config `logger=Slf4JLogger&profileSQL=true`  to  logger the log of transaction
## eg: spring.datasource.url=jdbc:mysql://localhost:3306/test?logger=Slf4JLogger&profileSQL=true
spring.datasource.url=jdbc:mysql://192.168.152.129:3306/study?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF8
spring.datasource.username=admin
spring.datasource.password=123456

# auto create table
spring.jpa.generate-ddl=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

logging.level.com.demo=debug
logging.level.jdbc.sqlonly=WARN
logging.level.jdbc.sqltiming=INFO
logging.level.jdbc.resultsettable=WARN
logging.level.jdbc.resultset=WARN
logging.level.jdbc.connection=WARN
logging.level.jdbc.audit=WARN


## open under logger lever to watch the open and close of transaction
#logging.level.org.springframework.orm.jpa=DEBUG
#logging.level.org.springframework.transaction=DEBUG
#logging.level.org.springframework.orm.jpa.JpaTransactionManager=trace
#logging.level.org.hibernate.engine.transaction.internal.TransactionImpl=DEBUG
```

### 第三步：增加模型与 Repository

**model 类**

```java
@Entity
@Data
@Table(name = "T_order")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String orderNo;
    private Long customerId;
    private int status;

    @Version
    private Integer version;
}
```

**Repository**

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
}
```


### 第四步: 增加 controller 

由于我们会使用到异步操作，对于异步操作最好是自己新建一个连接池，我这里偷懒，直接使用了 Spring 默认的线程池

```java
package com.demo.orm.jpa.completableFuture.controller;

import com.demo.orm.jpa.completableFuture.config.TransactionHelper;
import com.demo.orm.jpa.completableFuture.model.Order;
import com.demo.orm.jpa.completableFuture.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author wuq
 * @Time 2022-12-5 13:58
 * @Description
 */
@RestController
@Log4j2
public class OrderController {

    // 异步操作必须要建立线程池，这里是调用 SpringBoot 的异步线程池
    @Autowired
    private Executor executor;

    /**
     * 下面的会 <b>报错</b>，这里是用来做观察使用
     *
     * <p>测试链接</p>
     *      http://localhost:8080/update?id=7
     * <p>使用下面的执行逻辑，会发现</p>
     *   - 开启了四次事务 `Creating new transaction with name [org.springframework.data.jpa.repository.support.SimpleJpaRepository.save]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT`
     *   - 执行的时候，只会执行第一个 `First_` 操作 （前提是需要在对应的 模型类中使用 version 字段）
     *   - 前端返回值，永远都是 success
     */
    @RequestMapping("update")
    @Transactional  // 开启事务
    public String update(Long id) {
        CompletableFuture<Void> cf = CompletableFuture.runAsync(()-> {
            Order order = orderService.findById(id);

            //..... 此处模拟一些业务操作，第一次改变 order 里面的值；
            try {
                Thread.sleep(200L);// 加上复杂业务耗时200毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            order.setOrderNo("First_" + order.getOrderNo());
            orderService.save(order);


            //..... 此处模拟一些业务操作，第二次改变 order 里面的值；
            try {
                Thread.sleep(300L);// 加上复杂业务耗时300毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            order.setOrderNo("Second_" + order.getOrderNo());
            orderService.save(order);


        }, executor).exceptionally(e -> {
            log.error(e);//把异常信息打印出来
            e.printStackTrace();
            return null;
        });

        // 等待执行完成
        cf.isDone();
        return "Success";
    }
}
```

### 测试

首先，先在数据库中增加一条记录 
```sql
INSERT INTO `study`.`t_order` (`id`, `customer_id`, `order_no`, `status`, `version`)
 VALUES (7, 125435, '0001', 1, 12);
```

这个时候，我们先请求一次来看看效果

```http request
http://localhost:8080/update?id=7
```

这个时候会看到控制台中会报错。那是因为这里开启了 version 版本控制。如果将 version 版本控制去掉的话，这里就不会报错了，但是去掉 version 并不是我们所想要的效果。


这里我们去修改下配置文件，增加对应的 SQL 事务日志信息

```properties
spring.datasource.url=jdbc:mysql://192.168.152.129:3306/study?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF8&logger=Slf4JLogger&profileSQL=true

## open under logger lever to watch the open and close of transaction
logging.level.org.springframework.orm.jpa=DEBUG
logging.level.org.springframework.transaction=DEBUG
logging.level.org.springframework.orm.jpa.JpaTransactionManager=trace
logging.level.org.hibernate.engine.transaction.internal.TransactionImpl=DEBUG
```

再次请求调用

```http request
http://localhost:8080/update?id=7
```

通过控制台日志可以看到，是因为在 completableFuture 执行的过程中，开启了多个事务（多个 begin 和 commit）导致。知道了这个之后，我们开始做修改

### 修改：controller

增加对应的方法，将 completableFuture 中调用的逻辑移入到 service 层中，通过 service 层再开启事务
```java

@Autowired
OrderService orderService;

/**
 * <b>正确实现</b>
 *
 * 由于异步方法里面的事务是独立的，那么直接把异步的代码块用独立的事务包装起来即可
 *
 * <p>测试链接</p>
 *      http://localhost:8080/updateAsync?id=7
 */
@RequestMapping("updateAsync")
public String updateAsync(Long id){
    CompletableFuture<Void> cf = CompletableFuture.runAsync(()->{
        orderService.bizOrderMethod(id);
    }, executor).exceptionally( e -> {
        log.error(e);//把异常信息打印出来
        e.printStackTrace();
        return null;
    });

    // 等待执行完成
    cf.isDone();
    return "Success";
}
```

### 修改: 增加 service

```java
package com.demo.orm.jpa.completableFuture.service;

import com.demo.orm.jpa.completableFuture.model.Order;
import com.demo.orm.jpa.completableFuture.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public Order findById(Long id){
        return orderRepository.findById(id).get();
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }


    @Transactional  // 事务从这里开始传播
    public void bizOrderMethod(Long id){
        Order order = findById(id);

        //..... 此处模拟一些业务操作，第一次改变 order 里面的值；
        try {
            Thread.sleep(200L);// 加上复杂业务耗时200毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        order.setOrderNo("First_" + order.getOrderNo());
        save(order);


        //..... 此处模拟一些业务操作，第二次改变 order 里面的值；
        try {
            Thread.sleep(300L);// 加上复杂业务耗时300毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        order.setOrderNo("Second_" + order.getOrderNo());
        save(order);
    }
}
```

### 再次测试

这里，我们再次测试的时候发现，这里是可以正常执行过去的

```java
 http://localhost:8080/updateAsync?id=7
```


### 增加事务开启的入口

按照上面的改法，说明可以通过 service 层来开启事务，这里我们改变一种写法，通过 lambda 表示来实现

**TransactionHelper**

```java
@Component
public class TransactionHelper {

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class}) //可以根据实际业务情况，指定明确的回滚异常
    public void execute(Consumer consumer, Object o) {
        consumer.accept(o);
    }
}
```

在 controller 层中增加一个新的调用方法

```java
@Autowired
TransactionHelper transactionHelper;

/**
 * <b>正确实现</b>
 *
 * 由于异步方法里面的事务是独立的，那么直接把异步的代码块用独立的事务包装起来即可
 *
 * <p>测试链接</p>
 *      http://localhost:8080/updateConsumeAsync?id=7
 */
@RequestMapping("updateConsumeAsync")
public String updateConsumeAsync(Long id){
    CompletableFuture<Void> cf = CompletableFuture.runAsync(()->{

    // 使用 transactionHelper 将整个事务包装起来，就可以避免出现事务不一致的问题
    transactionHelper.execute( param -> {

    Order order = orderService.findById(id);

    //..... 此处模拟一些业务操作，第一次改变 order 里面的值；
    try {
        Thread.sleep(200L);// 加上复杂业务耗时200毫秒
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    order.setOrderNo("First_" + order.getOrderNo());
    orderService.save(order);


    //..... 此处模拟一些业务操作，第二次改变 order 里面的值；
    try {
     Thread.sleep(300L);// 加上复杂业务耗时300毫秒
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    order.setOrderNo("Second_" + order.getOrderNo());
    orderService.save(order);

    }, id);

    }, executor).exceptionally(e -> {
    log.error(e);//把异常信息打印出来
        e.printStackTrace();
        return null;
    });

    // 等待执行完成
    cf.isDone();
    return "Success";
}
```


### 最后，来测试下通过 CompletableFuture 获取数据之后，返回

```java
package com.demo.orm.jpa.completableFuture.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.demo.orm.jpa.completableFuture.model.Order;
import com.demo.orm.jpa.completableFuture.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * 这里是用于测试 completableFuture 在生产中实践的方式
 *
 * @author wuq
 * @Time 2022-12-6 16:40
 * @Description
 */
@RestController
@Log4j2
public class TestController {

    @Autowired
    OrderService orderService;

    @Autowired
    private Executor executor;  // 这里是使用Spring 框架默认的异步执行器

    @RequestMapping("test/aync")
    @Transactional
    public void test() throws ExecutionException, InterruptedException {
        long beginTime = System.nanoTime(); /**单位：微秒**/

        CompletableFuture<Map<String, Object>> firstOrderFuture = CompletableFuture.supplyAsync(() -> {
            Order order = orderService.findById(7l);
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(order));

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }, executor);

        CompletableFuture<Map<String, Object>> secondOrderFuture = CompletableFuture.supplyAsync(() -> {
            Order order = orderService.findById(8l);
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(order));

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }, executor);


        CompletableFuture<List> resultFuture = firstOrderFuture.thenCombine(secondOrderFuture, (firstOrder, secondOrder) -> {
            List list = Arrays.asList(firstOrder, secondOrder);
            return list;
        });

        List list = (List) resultFuture.get();  // 这里会阻塞异步执行的操作，拿到结果
        log.info(list);

        long endTime = System.nanoTime();
        long total = endTime - beginTime;
        long ms = total / 1000 / 1000; /**毫秒**/
        total -= ms * 1000 * 1000;
        long us = total / 1000; /**微秒**/
        total -= us * 1000;
        long ns = total; /**纳秒**/
        log.info(String.format("%s耗时：%s ms, %s us, %s ns", "执行耗时", ms, us, ns));
    }

}
```