package com.demo.orm.mybatisPlus.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")      // 表名映射
public class User {

    @TableId("id")     // 主键映射，如果指定名称为 user_id 则表示数据库中存在的列名未  user_id
    private Long id;

    @TableField("name")     // 一般的表名映射
    private String name;

    // 在数据库中不存在的字段，以下处理不会序列化到数据库中
    @TableField(exist = false)
    private Integer age;

    @TableField(select = true)  // 表示在查询语句中显示改列（投影操作），默认为true
    private String email;

    // 在数据库中不存在的字段，以下处理不会序列化到数据库中
    @TableField(exist = false)
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
