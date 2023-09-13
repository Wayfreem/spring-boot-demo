DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `user_id`     bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `user_name`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '用户账号',
    `nick_name`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '用户昵称',
    `user_type`   varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
    `password`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码',
    `del_flag`    char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime                                                      NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime                                                      NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户信息表'
  ROW_FORMAT = DYNAMIC;

/* 插入的密码为 123456 */
INSERT INTO `sys_user`
VALUES (6, 'admin', 'admin', '00', '$2a$10$ZglYem2Zs8E4ETbLwaiA4OjXaTZX9w8wJ7x8LZdpGisdtI9VlIfvO', '0', '', NULL, '',
        NULL);

INSERT INTO `sys_user`
VALUES (7, 'user', 'user', '00', '$2a$10$ZglYem2Zs8E4ETbLwaiA4OjXaTZX9w8wJ7x8LZdpGisdtI9VlIfvO', '0', '', NULL, '',
        NULL);
