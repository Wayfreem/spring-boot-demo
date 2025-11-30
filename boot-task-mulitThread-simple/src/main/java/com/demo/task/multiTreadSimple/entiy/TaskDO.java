package com.demo.task.multiTreadSimple.entiy;

import com.baomidou.mybatisplus.annotation.*;
import com.demo.task.multiTreadSimple.enums.TaskStatusEnum;
import com.demo.task.multiTreadSimple.enums.TaskTypeEnum;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

@TableName("infra_task")
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 参数
     */
    private String params;
    /**
     * 执行结果
     */
    private String result;
    /**
     * <p>
     * {@link TaskStatusEnum}
     * </p>
     * 状态
     */
    private Integer status;
    /**
     * 业务单号
     */
    private String bizNo;

    /**
     * 业务单据Id(orderId + type 唯一)
     */
    private String orderId;


    /**
     * <p>
     * {@link TaskTypeEnum}
     * </p>
     * 类型
     */
    private String type;
    /**
     * 重试次数
     */
    private Integer retries;
    /**
     * 描述
     */
    private String description;
    /**
     * 错误文件下载URL
     */
    private String fileUrl;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 创建者，目前使用 SysUser 的 id 编号
     * <p>
     * 使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    @TableField(fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private String creator;
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     * <p>
     * 使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.VARCHAR)
    private String updater;
    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
}
