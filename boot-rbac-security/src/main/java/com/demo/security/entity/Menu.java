package com.demo.security.entity;

import lombok.Data;

@Data
public class Menu {
    private Long id;
    private String name;
    private String url;
    private Long parentId;
    private String permission;
}
