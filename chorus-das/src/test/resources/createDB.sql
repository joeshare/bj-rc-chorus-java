

DROP TABLE IF EXISTS `apply_detail`;

CREATE TABLE `apply_detail` (
  `apply_detail_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `apply_form_id` bigint NOT NULL COMMENT '申请单ID',
  `table_info_id` bigint NOT NULL COMMENT '表ID',
  `column_info_id` bigint NOT NULL COMMENT '列ID',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  primary key (`apply_detail_id`)
);



DROP TABLE IF EXISTS `apply_form`;

CREATE TABLE `apply_form` (
  `apply_form_id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请单ID',
  `table_info_id` bigint NOT NULL COMMENT '表ID',
  `end_date` date NOT NULL COMMENT '失效日期',
  `apply_user_id` varchar(50) NOT NULL DEFAULT '' COMMENT '申请人',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `reason` varchar(100) NOT NULL DEFAULT '' COMMENT '申请原因',
  `deal_user_id` varchar(50) DEFAULT NULL COMMENT '处理人',
  `deal_time` datetime DEFAULT NULL COMMENT '处理时间',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  PRIMARY KEY (`apply_form_id`)
);



DROP TABLE IF EXISTS `column_info`;

CREATE TABLE `column_info` (
  `table_info_id` bigint NOT NULL COMMENT '表ID',
  `column_info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '列ID',
  `column_name` varchar(50) NOT NULL DEFAULT '' COMMENT '列名称',
  `column_desc` varchar(255) DEFAULT '' COMMENT '列描述',
  `column_type` varchar(50) NOT NULL DEFAULT '' COMMENT '列类型',
  `column_length` varchar(50) DEFAULT NULL COMMENT '列长度',
  `column_precision` varchar(50) DEFAULT NULL COMMENT '列精度',
  `security_level` varchar(50) DEFAULT '' COMMENT '安全级别',
  `is_key` tinyint(4) DEFAULT NULL COMMENT '是否主键',
  `is_ref_key` tinyint(4) DEFAULT NULL COMMENT '是否外键',
  `is_index` tinyint(4) DEFAULT NULL COMMENT '是否索引',
  `is_null` tinyint(4) DEFAULT NULL COMMENT '是否为空',
  `is_partition_key` tinyint(4) DEFAULT NULL comment '是否分区键',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  PRIMARY KEY (`column_info_id`)
) ;



DROP TABLE IF EXISTS `common_status`;

CREATE TABLE `common_status` (
  `status_id` bigint NOT NULL AUTO_INCREMENT COMMENT '状态ID',
  `status_code` varchar(50) NOT NULL COMMENT '状态编码',
  `status_name` varchar(50) NOT NULL COMMENT '状态名称',
  `status_type` varchar(50) NOT NULL COMMENT '状态类型',
  PRIMARY KEY (`status_id`)
);



DROP TABLE IF EXISTS `environment_info`;

CREATE TABLE `environment_info` (
  `environment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '环境ID',
  `environment_name` varchar(50) NOT NULL DEFAULT '' COMMENT '环境名称',
  `environment_version` varchar(100) DEFAULT NULL COMMENT '环境版本',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status_code` varchar(50) NOT NULL DEFAULT '0' COMMENT '状态编码',
  PRIMARY KEY (`environment_id`)
);



DROP TABLE IF EXISTS `instance_environment_mapping`;

CREATE TABLE `instance_environment_mapping` (
  `instance_environment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '实例环境映射ID',
  `instance_id` bigint NOT NULL COMMENT '实例ID',
  `environment_id` bigint NOT NULL COMMENT '环境ID',
  PRIMARY KEY (`instance_environment_id`)
);



DROP TABLE IF EXISTS `instance_info`;

CREATE TABLE `instance_info` (
  `instance_id` bigint NOT NULL AUTO_INCREMENT COMMENT '实例ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `resource_inner_id` bigint NOT NULL COMMENT '内部资源ID',
  `resource_template_id` bigint NOT NULL COMMENT '资源模板ID',
  `instance_size` int NOT NULL comment '实例数量',
  `group_name` varchar(255) NOT NULL comment '实例组名',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  `instance_desc` varchar(500) DEFAULT '' COMMENT '实例备注',
  PRIMARY KEY (`instance_id`)
);



DROP TABLE IF EXISTS `instance_operation`;

CREATE TABLE `instance_operation` (
  `operation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '操作ID',
  `instance_id` bigint(20) NOT NULL COMMENT '实例ID',
  `user_id` bigint(50) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `status_code` varchar(50) NOT NULL COMMENT '状态编码',
  PRIMARY KEY (`operation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `project_info`;

CREATE TABLE `project_info` (
  `project_id` bigint NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` varchar(50) NOT NULL DEFAULT '' COMMENT '项目名称',
  `project_desc` varchar(50) DEFAULT '' COMMENT '项目描述',
  `project_manager_id` varchar(50) NOT NULL DEFAULT '' COMMENT '项目负责人userid',
  `manager_telephone` varchar(11) DEFAULT NULL COMMENT '负责人电话',
  `create_user_id` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`project_id`)
) ;



DROP TABLE IF EXISTS `project_member_mapping`;

CREATE TABLE `project_member_mapping` (
  `project_member_id` bigint NOT NULL AUTO_INCREMENT COMMENT '项目成员关系ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `user_id` varchar(50) NOT NULL DEFAULT '' COMMENT '成员ID',
  `role_id` varchar(50) NOT NULL DEFAULT '' COMMENT '角色ID',
  PRIMARY KEY (`project_member_id`)
) ;



DROP TABLE IF EXISTS `project_resource_mapping`;

CREATE TABLE `project_resource_mapping` (
  `project_resource_out_id` bigint NOT NULL AUTO_INCREMENT COMMENT '项目外部资源映射ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `resource_out_id` bigint NOT NULL COMMENT '外部资源ID',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  PRIMARY KEY (`project_resource_out_id`)
) ;



DROP TABLE IF EXISTS `resource_inner`;

CREATE TABLE `resource_inner` (
  `resource_inner_id` bigint NOT NULL AUTO_INCREMENT COMMENT '内部资源ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `create_user_id` varchar(50) NOT NULL DEFAULT '' COMMENT '用户ID',
  `resource_cpu` int(10) NOT NULL COMMENT 'CPU',
  `resource_memory` int(10) NOT NULL COMMENT '内存',
  `resource_storage` int(10) NOT NULL COMMENT '存储',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  PRIMARY KEY (`resource_inner_id`),
  UNIQUE KEY `u_resource_inner_project_id` (`project_id`)
) ;




DROP TABLE IF EXISTS `resource_operation`;

CREATE TABLE `resource_operation` (
  `operation_id` bigint NOT NULL AUTO_INCREMENT COMMENT '操作ID',
  `resource_id` bigint COMMENT '资源ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `cpu` int(11) not null comment 'cpu',
  `memory` int(11) not null comment '内存 G',
  `storage` int(11) not null comment '存储 G',
  `create_user_id` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `reason` varchar(255) NOT NULL DEFAULT '' COMMENT '原因',
  `status_code` varchar(50) NOT NULL COMMENT '状态编码',
  `notes` varchar(255) DEFAULT NULL COMMENT '处理备注',
  PRIMARY KEY (`operation_id`)
) ;



DROP TABLE IF EXISTS `resource_out`;

CREATE TABLE `resource_out` (
  `resource_out_id` bigint NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `project_id` bigint DEFAULT NULL COMMENT '项目ID',
  `resource_name` varchar(50) DEFAULT NULL COMMENT '资源名称',
  `resource_source` varchar(50) DEFAULT NULL COMMENT '资源来源',
  `resource_usage` varchar(50) DEFAULT NULL COMMENT '资源用途',
  `resource_desc` varchar(100) DEFAULT NULL COMMENT '资源描述',
  `storage_type` varchar(50) DEFAULT NULL COMMENT '存储类型',
  `conn_url` varchar(50) DEFAULT NULL COMMENT '链接地址',
  `conn_port` varchar(50) DEFAULT NULL COMMENT '端口',
  `conn_user` varchar(50) DEFAULT NULL COMMENT '用户名',
  `conn_pass` varchar(50) DEFAULT NULL COMMENT '密码',
  `create_user_id` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `end_date` date DEFAULT NULL COMMENT '失效日期',
  `status_code` varchar(50) DEFAULT NULL COMMENT '状态编码',
  `database_name` varchar(255) DEFAULT NULL COMMENT '数据库名称'
  PRIMARY KEY (`resource_out_id`)
) ;



DROP TABLE IF EXISTS `resource_template`;

CREATE TABLE `resource_template` (
  `resource_template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '资源模板ID',
  `resouce_name` varchar(200) NOT NULL COMMENT '资源模板名称,不允许中文',
  `resource_cpu` int(10) NOT NULL COMMENT 'CPU',
  `resource_memory` int(10) NOT NULL DEFAULT '1' COMMENT '内存',
  `resource_storage` int(10) NOT NULL DEFAULT '1' COMMENT '存储',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status_code` varchar(50) NOT NULL DEFAULT '0' COMMENT '状态编码',
  PRIMARY KEY (`resource_template_id`),
  UNIQUE KEY `idx_uniq_resource_name` (`resouce_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `table_authority`;

CREATE TABLE `table_authority` (
  `table_authority_id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限关系ID',
  `table_info_id` bigint NOT NULL COMMENT '表ID',
  `column_info_id` bigint NOT NULL COMMENT '列ID',
  `user_id` varchar(50) NOT NULL DEFAULT '' COMMENT '用户ID',
  `end_date` date NOT NULL COMMENT '失效日期',
  PRIMARY KEY (`table_authority_id`)
);




DROP TABLE IF EXISTS `table_info`;

CREATE TABLE `table_info` (
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `table_info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `table_code` varchar(50) DEFAULT '' COMMENT '表编码',
  `table_name` varchar(50) NOT NULL DEFAULT '' COMMENT '表名称',
  `data_field` varchar(50) DEFAULT NULL COMMENT '数据域',
  `table_type` varchar(50) DEFAULT NULL COMMENT '表类型',
  `is_snapshot` varchar(50) DEFAULT NULL COMMENT '是否支持快照',
  `update_frequence` varchar(50) DEFAULT NULL COMMENT '时效性',
  `sla` varchar(50) DEFAULT NULL COMMENT 'SLA',
  `security_level` varchar(50) DEFAULT NULL COMMENT '安全等级',
  `is_open` tinyint(50) NOT NULL COMMENT '是否对外',
  `table_des` varchar(255) DEFAULT '' COMMENT '表描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  PRIMARY KEY (`table_info_id`)
);

DROP TABLE IF EXISTS `total_resource`;

CREATE TABLE `total_resource`(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `cpu` int(11) NOT NULL COMMENT 'cpu',
    `memory` int(11) NOT NULL COMMENT '内存 单位G',
    `storage` int(11) NOT NULL COMMENT '存储 单位G',
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `job`;

CREATE TABLE `job` (
  `job_id` int(64) NOT NULL AUTO_INCREMENT COMMENT '业务主键',
  `job_type` int(5) NOT NULL COMMENT '任务类型',
  `project_id` int(64) NOT NULL COMMENT '项目ID',
  `job_name` varchar(50) NOT NULL COMMENT 'XD 任务运行ID(uuid)',
  `job_alias_name` varchar(255) DEFAULT NULL COMMENT '任务名(显示)',
  `instance_id` bigint(20) DEFAULT NULL COMMENT '实例ID',
  `work_flow_dsl` varchar(10000) DEFAULT NULL COMMENT 'Spring XD DSL',
  `job_parameters` varchar(1000) DEFAULT NULL COMMENT 'Job执行参数',
  `data_input` varchar(100) DEFAULT NULL COMMENT '任务输入',
  `data_output` varchar(100) DEFAULT NULL COMMENT '任务输出',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` varchar(10) NOT NULL DEFAULT 'UNDEPLOY' COMMENT '状态(UNDEPLOY:未发布 DEPLOY:已发布 DELETE:删除)',
  `warning_config` varchar(1000) DEFAULT NULL COMMENT '告警规则(json格式)',
  `use_yn` char(1) DEFAULT 'Y' COMMENT '是否可用 (Y-可用 N-不可用）',
  `deploy_user` varchar(50) DEFAULT NULL COMMENT '任务负责人ID',
  `deploy_user_name` varchar(50) DEFAULT NULL COMMENT '任务负责人名',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人ID',
  `create_user_name` varchar(50) DEFAULT NULL COMMENT '任务创建人名',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '修改人ID',
  `update_user_name` varchar(50) DEFAULT NULL COMMENT '修改人名',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=237 DEFAULT CHARSET=utf8 COMMENT='数据任务信息';

/*Table structure for table `task` */

DROP TABLE IF EXISTS `task`;

CREATE TABLE `task` (
  `task_id` int(64) NOT NULL AUTO_INCREMENT COMMENT '业务主键',
  `job_id` int(64) NOT NULL COMMENT '数据任务表ID',
  `module_name` varchar(100) NOT NULL COMMENT '模块名',
  `task_name` varchar(50) NOT NULL COMMENT 'XD 步骤运行ID(UUID)',
  `alias_name` varchar(255) DEFAULT NULL COMMENT '步骤别名',
  `instance_id` bigint(20) DEFAULT NULL COMMENT '实例ID',
  `task_dsl` varchar(10000) DEFAULT NULL COMMENT 'task definition',
  `data_input` varchar(100) DEFAULT NULL COMMENT '任务输入',
  `data_output` varchar(100) DEFAULT NULL COMMENT '任务输出',
  `config` varchar(10000) DEFAULT NULL COMMENT 'Step配置参数信息',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_user` varchar(50) DEFAULT NULL COMMENT '任务创建人',
  `create_user_name` varchar(50) DEFAULT NULL COMMENT '任务创建人名',
  `create_time` datetime DEFAULT NULL COMMENT '数据插入时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新者用户ID',
  `update_user_name` varchar(50) DEFAULT NULL COMMENT '更新者用户名',
  `update_time` datetime DEFAULT NULL COMMENT '数据更新时',
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=431 DEFAULT CHARSET=utf8 COMMENT='执行步骤信息';



