CREATE TABLE `common_attribute` (
    `attribute_id` VARCHAR(4) NOT NULL COMMENT '属性标识',
    `name` VARCHAR(20) NOT NULL COMMENT '属性名称',
    `description` VARCHAR(100) NOT NULL COMMENT '属性描述',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`attribute_id`)
)
COMMENT='平台通用信息标识属性表'
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB;

INSERT INTO `common_attribute` (`attribute_id`, `name`, `description`) VALUES ('0001', '平台维护状态', '0：运行中；1：维护中');

CREATE TABLE `common_info` (
    `id` INT(10) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `user_id` VARCHAR(255) NOT NULL DEFAULT '0' COMMENT '用户标识（0表示全平台用户）',
    `attribute_id` VARCHAR(4) NOT NULL COMMENT '属性标识',
    `value` VARCHAR(20) NOT NULL COMMENT '属性值',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uni_user_id_attri_id` (`user_id`, `attribute_id`)
)
COMMENT='平台通用信息表'
COLLATE='utf8_unicode_ci'
ENGINE=InnoDB;

INSERT INTO `common_info` (`attribute_id`, `value`) VALUES ('0001', '0');

CREATE TABLE `job_unexecuted` (
    `id` INT(10) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `job_name` VARCHAR(50) NOT NULL COMMENT '任务名称',
    `sche_exec_time` DATETIME NOT NULL COMMENT '计划执行时间',
    `rerun_flag` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '重新执行标识（0：需要执行，1：不需执行）',
    `notice_flag` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '通知标识（0：需要通知，1：不需通知）',
    `status` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '状态（0：未执行，1：已恢复并执行）',
    `exec_time` DATETIME NULL DEFAULT NULL COMMENT '执行时间',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`)
)
COMMENT='未执行任务表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;