package com.demo.pattern.service;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用于获取指定的单据处理器
 * <p>
 * 实现 ApplicationListener 接口，在容器刷新时，获取所有的 OrderProcess 实现类，并存储到 List 中
 */
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
