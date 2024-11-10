package com.demo.task.mulitThread.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Task implements Serializable {

    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime modifiedTime;

    /**
     * 任务参数
     */
    private String params;

    /**
     * 任务结果
     */
    private String result;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 任务参数hash
     */
    private String paramHash;

    /**
     * 任务类型
     */
    private Integer type;

    /**
     * 重试次数
     */
    private Integer retries;

    /**
     * 任务描述
     */
    private String description;

}
