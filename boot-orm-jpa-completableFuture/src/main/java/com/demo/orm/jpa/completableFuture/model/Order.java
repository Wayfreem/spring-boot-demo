package com.demo.orm.jpa.completableFuture.model;

import lombok.Data;

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
