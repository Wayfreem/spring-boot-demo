package com.demo.orm.jpa.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author wuq
 * @Time 2022-7-1 14:30
 * @Description
 */
@Data
@Entity
@Table(name = "User")
public class User {

    @Id
    private String id;
    private String name;
    private String email;
    private String lastname;

    @Version
    private Long version;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
