CREATE TABLE scm_task (
                          id BIGINT UNSIGNED auto_increment COMMENT 'primary key' PRIMARY KEY,
                          create_time datetime NOT NULL COMMENT 'create time',
                          modified_time datetime NOT NULL COMMENT 'modify time',
                          params text NULL COMMENT '任务参数',
                          param_hash VARCHAR ( 128 ) NULL COMMENT '参数哈希',
                          result text NULL COMMENT '任务结果',
                          type TINYINT NULL COMMENT '任务类别',
                          STATUS TINYINT NULL COMMENT '任务状态',
                          retries INT DEFAULT 0 NULL COMMENT '重试次数',
                          description VARCHAR ( 32 ) NULL COMMENT '任务描述'
) COMMENT '异步任务表' COLLATE = utf8mb4_general_ci row_format = DYNAMIC;