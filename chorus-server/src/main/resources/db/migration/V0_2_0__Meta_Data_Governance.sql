/* project_info 表增加 status_code 字段用于区别可用项目及注销项目*/

ALTER TABLE `chorus`.`project_info`
  ADD COLUMN `status_code` VARCHAR(50) NULL;

--
-- table  apply_detail_v2
--
CREATE TABLE `apply_detail_v2` (
  `apply_detail_id` BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
  `apply_form_id`   BIGINT(20)  NOT NULL COMMENT '申请单ID',
  `table_info_id`   VARCHAR(50) NOT NULL COMMENT '表ID',
  `column_info_id`  VARCHAR(50) NOT NULL COMMENT '列ID',
  `status_code`     VARCHAR(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  PRIMARY KEY (`apply_detail_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

--
-- table  apply_form_v2
--
CREATE TABLE `apply_form_v2` (
  `apply_form_id`    BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '申请单ID',
  `project_id`       BIGINT(20)   NOT NULL COMMENT '被申请表的项目ID',
  `table_info_id`    VARCHAR(50)  NOT NULL COMMENT '表ID',
  `table_name`       varchar(50) NOT NULL DEFAULT '' COMMENT '表名称',
  `end_date`         DATE         NOT NULL DEFAULT '2099-12-31' COMMENT '失效日期',
  `apply_user_id`    VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '申请人',
  `apply_time`       DATETIME     NOT NULL COMMENT '申请时间',
  `reason`           VARCHAR(255) NOT NULL DEFAULT '' COMMENT '申请原因',
  `deal_user_id`     VARCHAR(50)           DEFAULT NULL COMMENT '处理人',
  `deal_instruction` VARCHAR(255)          DEFAULT NULL,
  `deal_time`        DATETIME              DEFAULT NULL COMMENT '处理时间',
  `status_code`      VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '状态编码',
  `apply_user_name`  VARCHAR(100)          DEFAULT NULL,
  PRIMARY KEY (`apply_form_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

--
-- table  table_authority_v2
--
CREATE TABLE `table_authority_v2` (
  `table_authority_id` BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '权限关系ID',
  `project_id`      BIGINT(20) NOT NULL COMMENT '项目ID',
  `table_info_id`      VARCHAR(50) NOT NULL COMMENT '表ID',
  `table_name`      VARCHAR(50) NOT NULL COMMENT '表名',
  `column_info_id`     VARCHAR(50) NOT NULL COMMENT '列ID',
  `column_name`     VARCHAR(50) NOT NULL COMMENT '列名',
  `user_id`            VARCHAR(50) NOT NULL DEFAULT '' COMMENT '用户ID',
  `end_date`           DATE                 DEFAULT NULL COMMENT '失效日期',
  PRIMARY KEY (`table_authority_id`),
  UNIQUE KEY `u_user_column` (`user_id`, `column_info_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

--
-- table  table_monitor_v2
--
CREATE TABLE `table_monitor_v2` (
  `id`            BIGINT(20)  NOT NULL AUTO_INCREMENT,
  `project_id`    BIGINT(20)  NOT NULL,
  `table_info_id` VARCHAR(50) NOT NULL,
  `table_name`      VARCHAR(50) NOT NULL COMMENT '表名',
  `monitor_date`  DATE        NOT NULL,
  `rows`          BIGINT(20)           DEFAULT NULL,
  `storage_size`  BIGINT(20)           DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `table_unique` (`table_info_id`, `monitor_date`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
