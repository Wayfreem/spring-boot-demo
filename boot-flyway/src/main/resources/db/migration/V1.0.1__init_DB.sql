DROP TABLE IF EXISTS `tb_user`;

CREATE TABLE `tb_user`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT,
    `user_name`    varchar(45) NOT NULL,
    `password`     varchar(45) NOT NULL,
    `email`        varchar(45)  DEFAULT NULL,
    `phone_number` int(11) DEFAULT NULL,
    `description`  varchar(255) DEFAULT NULL,
    `create_time`  datetime     DEFAULT NULL,
    `update_time`  datetime     DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
