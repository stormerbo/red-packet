CREATE TABLE `user`
(
    `user_id`   int(20)        NOT NULL AUTO_INCREMENT COMMENT '用户id，主键，自增',
    `user_name` varchar(255)   NOT NULL COMMENT '用户名',
    `balance`   decimal(32, 2) NOT NULL COMMENT '账户余额',
    PRIMARY KEY (`user_id`)
);

CREATE TABLE `red_packet`
(
    `red_packet_id` int(20)        NOT NULL AUTO_INCREMENT COMMENT '红包id，主键，自增',
    `user_id`       int(20)        NOT NULL COMMENT '发红包的用户',
    `total_money`   decimal(32, 2) NOT NULL COMMENT '红包的总金额',
    `num`           int(3)         NOT NULL COMMENT '红包的个数',
    `remain_money`  decimal(32, 2) NOT NULL COMMENT '红包的总金额',
    `status`        tinyint(2)     not null comment '红包的状态，0-未抢完，1-已经抢完',
    `create_time`   datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    PRIMARY KEY (`red_packet_id`),
    INDEX USER_ID (`user_id`) using BTREE
) comment '发红包的记录表';

CREATE TABLE `red_packet_record`
(
    `red_packet_record_id` int(20)        NOT NULL AUTO_INCREMENT COMMENT '红包记录id，主键，自增',
    `red_packet_id`        int(20)        NOT NULL COMMENT '红包id，主键，自增',
    `user_id`              int(20)        NOT NULL COMMENT '抢红包的用户',
    `serial_no`            int(20)        NOT NULL COMMENT '流水号, 唯一性校验',
    `money`                decimal(32, 2) NOT NULL COMMENT '抢到的金额',
    `create_time`          datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    PRIMARY KEY (`red_packet_record_id`),
    INDEX RED_PACKET_ID (`red_packet_id`) using BTREE
) comment '抢红包的记录表';