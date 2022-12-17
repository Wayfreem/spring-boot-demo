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

    @Autowired
    OrderService orderService;

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
