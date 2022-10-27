package com.demo.orm.jpa.domainEvents.model;

import com.demo.orm.jpa.domainEvents.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

/**
 * @author wuq
 * @Time 2022-10-24 14:17
 * @Description
 */
@Component
@Slf4j
public class OrderEventReceiver {

    @Autowired
    private EmployeeService service;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderFinished(OrderFinishedEvent event) {
        log.info("================Order finished event handler==================");
        if(Objects.isNull(event)) {
            return;
        }
        System.out.println(event.toString());
        service.save();
        throw new RuntimeException("测试事务是否回滚");
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
