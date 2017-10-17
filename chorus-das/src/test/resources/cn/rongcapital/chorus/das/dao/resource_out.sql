CREATE TABLE `resource_out` (
  `resource_out_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `project_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `resource_name` varchar(50) DEFAULT NULL COMMENT '资源名称',
  `resource_source` varchar(50) DEFAULT NULL COMMENT '资源来源',
  `resource_usage` varchar(50) DEFAULT NULL COMMENT '资源用途',
  `resource_desc` varchar(100) DEFAULT NULL COMMENT '资源描述',
  `storage_type` varchar(50) DEFAULT NULL COMMENT '存储类型',
  `conn_url` varchar(220) DEFAULT NULL COMMENT '链接地址',
  `conn_port` varchar(50) DEFAULT NULL COMMENT '端口',
  `conn_user` varchar(50) DEFAULT NULL COMMENT '用户名',
  `conn_pass` varchar(50) DEFAULT NULL COMMENT '密码',
  `create_user_id` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `end_date` date DEFAULT NULL COMMENT '失效日期',
  `status_code` varchar(50) DEFAULT NULL COMMENT '状态编码',
  `database_name` varchar(255) DEFAULT NULL,
  `conn_host` varchar(255) DEFAULT NULL,
  `create_user_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`resource_out_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `resource_out`
  ADD COLUMN `guid` VARCHAR(50) NOT NULL DEFAULT '';
INSERT INTO `resource_out` VALUES
  ('33', '222667', 'chorus', NULL, NULL, 'chorus', '1', 'jdbc:mysql://10.200.48.79:3306/chorus', '3306', 'dps', 'Dps@10.200.48.MySQL', '10970', '2017-08-31 09:48:12', NULL, NULL, NULL, NULL, 'chorus', '10.200.48.79', 'guoyemeng', '');
INSERT INTO `resource_out` VALUES
  ('34', '222667', 'chorus', NULL, NULL, 'chorus', '1', 'jdbc:mysql://10.200.48.79:3306/chorus', '3306', 'dps', 'Dps@10.200.48.MySQL', '10970', '2017-08-31 09:48:12', NULL, NULL, NULL, NULL, 'chorus', '10.200.48.79', 'guoyemeng', 'FAJIOFAL');
