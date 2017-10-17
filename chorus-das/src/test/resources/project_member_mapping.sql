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


DROP TABLE IF EXISTS `project_member_mapping`;
CREATE TABLE `project_member_mapping` (
  `project_member_id` BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '项目成员关系ID',
  `project_id`        BIGINT(20)  NOT NULL COMMENT '项目ID',
  `role_id`           VARCHAR(50)          DEFAULT NULL,
  `user_id`           VARCHAR(50) NOT NULL DEFAULT '' COMMENT '成员ID',
  `is_filtered`       VARCHAR(5)           DEFAULT 'Y' COMMENT '项目是否被过滤\n可选值 Y (该项目被选中过滤)',
  PRIMARY KEY (`project_member_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;



CREATE TABLE `t_role` (
  `ROLE_ID`     VARCHAR(255) NOT NULL,
  `ROLE_CODE`   VARCHAR(255) DEFAULT NULL,
  `ROLE_NAME`   VARCHAR(255) DEFAULT NULL,
  `ROLE_TYPE`   VARCHAR(255) DEFAULT NULL,
  `REMARK`      VARCHAR(255) DEFAULT NULL,
  `create_user` VARCHAR(255) DEFAULT NULL,
  `create_time` DATETIME     DEFAULT NULL,
  `update_time` DATETIME     DEFAULT NULL,
  `permission`  VARCHAR(255) DEFAULT NULL COMMENT '可用权限',
  PRIMARY KEY (`ROLE_ID`)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;

INSERT INTO `t_role` (`ROLE_ID`, `ROLE_CODE`, `ROLE_NAME`, `ROLE_TYPE`, `REMARK`, `create_user`, `create_time`, `update_time`, `permission`)
VALUES ('2', '913', '数据分析人员', 'CHA', '1', '0', '2016-11-25 16:04:12', '2016-11-25 16:04:15', 'select');
INSERT INTO `t_role` (`ROLE_ID`, `ROLE_CODE`, `ROLE_NAME`, `ROLE_TYPE`, `REMARK`, `create_user`, `create_time`, `update_time`, `permission`)
VALUES ('1', '911', '数据开发人员', 'KAI', '1', '0', '2016-11-25 16:04:12', '2016-11-25 16:04:15', 'select,Create');
INSERT INTO `t_role` (`ROLE_ID`, `ROLE_CODE`, `ROLE_NAME`, `ROLE_TYPE`, `REMARK`, `create_user`, `create_time`, `update_time`, `permission`)
VALUES ('5', '915', '项目管理员', 'ADM', '1', '0', '2016-11-25 16:04:12', '2016-11-25 16:04:15', 'select,update,Create,Drop,Alter,Index,Lock,All');
INSERT INTO `t_role` (`ROLE_ID`, `ROLE_CODE`, `ROLE_NAME`, `ROLE_TYPE`, `REMARK`, `create_user`, `create_time`, `update_time`, `permission`)
VALUES ('4', '914', '项目Owner', 'OWN', '1', '0', '2016-11-25 16:04:12', '2016-11-25 16:04:15', 'select,update,Create,Drop,Alter,Index,Lock,All');
