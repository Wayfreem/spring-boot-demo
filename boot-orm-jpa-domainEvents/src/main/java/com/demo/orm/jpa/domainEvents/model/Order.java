package com.demo.orm.jpa.domainEvents.model;

import lombok.Data;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author wuq
 * @Time 2022-10-24 14:02
 * @Description
 */
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
