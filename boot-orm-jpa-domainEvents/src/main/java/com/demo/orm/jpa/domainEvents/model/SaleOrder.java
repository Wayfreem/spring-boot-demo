package com.demo.orm.jpa.domainEvents.model;

import lombok.Data;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wuq
 * @Time 2022-10-24 17:12
 * @Description
 */
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
