DROP TABLE IF EXISTS `project_info`;
CREATE TABLE `project_info` (
  `project_id`         BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_code`       VARCHAR(50) NOT NULL DEFAULT '' COMMENT '项目名称',
  `project_name`       VARCHAR(50) NOT NULL DEFAULT '' COMMENT '项目名称',
  `project_desc`       VARCHAR(255)         DEFAULT '' COMMENT '项目描述',
  `project_manager_id` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '项目负责人userid',
  `manager_telephone`  VARCHAR(50)          DEFAULT NULL COMMENT '负责人电话',
  `create_user_id`     VARCHAR(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time`        DATETIME    NOT NULL COMMENT '创建时间',
  `update_user_id`     VARCHAR(50)          DEFAULT NULL COMMENT '修改人',
  `update_time`        DATETIME             DEFAULT NULL COMMENT '修改时间',
  `caas_topic_id`      BIGINT(20)           DEFAULT NULL,
  `user_name`          VARCHAR(255)         DEFAULT NULL,
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `u_project_code` (`project_code`),
  UNIQUE KEY `u_project_name` (`project_name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

INSERT INTO project_info (`project_id`, `project_code`, `project_name`, `project_desc`, `project_manager_id`, `manager_telephone`, `create_user_id`, `create_time`, `update_user_id`, `update_time`, `caas_topic_id`, `user_name`)
VALUES (1, 'first_project_code', 'first_project_name', 'first project desc', '121', '15001482721', '1888', '2017-08-09 13:07:09', '212323', '2017-08-09 13:07:09', 1228, 'testing');

CREATE TABLE `table_authority_v2` (
  `table_authority_id` BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '权限关系ID',
  `project_id`         BIGINT(20)  NOT NULL COMMENT '项目ID',
  `table_info_id`      VARCHAR(50) NOT NULL COMMENT '表ID',
  `table_name`         VARCHAR(50) NOT NULL COMMENT '表名',
  `column_info_id`     VARCHAR(50) NOT NULL COMMENT '列ID',
  `column_name`        VARCHAR(50) NOT NULL COMMENT '列名',
  `user_id`            VARCHAR(50) NOT NULL DEFAULT '' COMMENT '用户ID',
  `end_date`           DATE                 DEFAULT NULL COMMENT '失效日期',
  PRIMARY KEY (`table_authority_id`),
  UNIQUE KEY `u_user_column` (`user_id`, `column_info_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
INSERT INTO table_authority_v2 (table_authority_id, project_id, table_info_id, table_name, column_info_id, column_name, user_id) VALUES
  (1, 1, 'alj0-fda-11', 'tableName1', '983jf-23f', 'columnName1', '1234');
INSERT INTO table_authority_v2 (table_authority_id, project_id, table_info_id, table_name, column_info_id, column_name, user_id) VALUES
  (2, 1, 'alj0-fda-11', 'tableName1', '983jf-13f', 'columnName2', '1234');
INSERT INTO table_authority_v2 (table_authority_id, project_id, table_info_id, table_name, column_info_id, column_name, user_id) VALUES
  (3, 1, 'alj0-faa-11', 'tableName2', '983jf-5a3f', 'columnName3', '1234');

