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
