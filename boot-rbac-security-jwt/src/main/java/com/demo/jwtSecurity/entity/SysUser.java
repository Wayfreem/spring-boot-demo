package com.demo.jwtSecurity.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wuq
 * @Time 2023-9-8 11:38
 * @Description
 */
@TableName(value = "sys_user")
@Data
@ToString(callSuper = true)
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "user_name")
    private String userName;

    @TableField(value = "nick_name")
    private String nickName;

    @TableField(value = "user_type")
    private String userType;

    @TableField(value = "password")
    private String password;

    @TableField(value = "del_flag")
    private String delFlag;

    @TableField(value = "create_by")
    private String createBy;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_by")
    private String updateBy;

    @TableField(value = "update_time")
    private Date updateTime;
}
