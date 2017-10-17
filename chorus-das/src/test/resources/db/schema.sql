
--
-- Table structure for table `apply_detail`
--

DROP TABLE IF EXISTS `apply_detail`;

CREATE TABLE `apply_detail` (
  `apply_detail_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `apply_form_id` bigint(20) NOT NULL COMMENT '申请单ID',
  `table_info_id` bigint(20) NOT NULL COMMENT '表ID',
  `column_info_id` bigint(20) NOT NULL COMMENT '列ID',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  PRIMARY KEY (`apply_detail_id`)
) ENGINE=InnoDB AUTO_INCREMENT=186 DEFAULT CHARSET=utf8;


--
-- Table structure for table `apply_form`
--

DROP TABLE IF EXISTS `apply_form`;

CREATE TABLE `apply_form` (
  `apply_form_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '申请单ID',
  `table_info_id` bigint(20) NOT NULL COMMENT '表ID',
  `end_date` date NOT NULL DEFAULT '2099-12-31' COMMENT '失效日期',
  `apply_user_id` varchar(50) NOT NULL DEFAULT '' COMMENT '申请人',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `reason` varchar(255) NOT NULL DEFAULT '' COMMENT '申请原因',
  `deal_user_id` varchar(50) DEFAULT NULL COMMENT '处理人',
  `deal_instruction` varchar(255) DEFAULT NULL,
  `deal_time` datetime DEFAULT NULL COMMENT '处理时间',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  `apply_user_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`apply_form_id`)
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8;


--
-- Table structure for table `authorization_detail`
--

DROP TABLE IF EXISTS `authorization_detail`;

CREATE TABLE `authorization_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `policy_id` varchar(11) NOT NULL COMMENT '事件 id',
  `policy_name` varchar(50) NOT NULL COMMENT '事件名称, 编码',
  `user_id` varchar(11) DEFAULT NULL,
  `project_id` bigint(11) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=237 DEFAULT CHARSET=utf8;


--
-- Table structure for table `column_info`
--

DROP TABLE IF EXISTS `column_info`;

CREATE TABLE `column_info` (
  `table_info_id` bigint(20) NOT NULL COMMENT '表ID',
  `column_info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '列ID',
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
  `is_partition_key` tinyint(4) DEFAULT NULL COMMENT '是否分区键',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  PRIMARY KEY (`column_info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=526507 DEFAULT CHARSET=utf8;


--
-- Table structure for table `common_status`
--

DROP TABLE IF EXISTS `common_status`;

CREATE TABLE `common_status` (
  `status_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '状态ID',
  `status_code` varchar(50) NOT NULL COMMENT '状态编码',
  `status_name` varchar(50) NOT NULL COMMENT '状态名称',
  `status_type` varchar(50) NOT NULL COMMENT '状态类型',
  PRIMARY KEY (`status_id`),
  UNIQUE KEY `status_code` (`status_code`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;


--
-- Table structure for table `data_property`
--

DROP TABLE IF EXISTS `data_property`;

CREATE TABLE `data_property` (
  `id` int(45) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `property_name` varchar(45) NOT NULL COMMENT '属性名称',
  `property_code` varchar(45) NOT NULL,
  `property_datatype` varchar(45) NOT NULL COMMENT '属性数据类型',
  `property_length` int(3) NOT NULL COMMENT '属性长度',
  `property_desc` varchar(45) NOT NULL COMMENT '属性描述',
  `data_id` int(45) NOT NULL COMMENT '关联数据ID',
  `create_user` varchar(45) NULL COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` int(3) NOT NULL DEFAULT '0' COMMENT '状态，0有效，1无效',
  `security_level` varchar(45) NULL COMMENT '安全等级',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1701 DEFAULT CHARSET=utf8 ;


--
-- Table structure for table `datalab_info`
--

DROP TABLE IF EXISTS `datalab_info`;

CREATE TABLE `datalab_info` (
  `lab_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `project_code` varchar(50) NOT NULL,
  `lab_code` varchar(50) NOT NULL COMMENT '实验室代号',
  `lab_desc` varchar(150) NOT NULL COMMENT '实验室描述',
  `lab_name` varchar(150) NOT NULL COMMENT '实验室名称',
  `cpu` int(11) DEFAULT '1',
  `memory` int(11) DEFAULT '2048',
  `create_user_id` varchar(50) DEFAULT '' COMMENT '创建人id',
  `create_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`lab_id`),
  UNIQUE KEY `u_project_lab` (`project_code`,`lab_code`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8;


--
-- Table structure for table `datatype_element`
--

DROP TABLE IF EXISTS `datatype_element`;

CREATE TABLE `datatype_element` (
  `id` int(45) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type_code` varchar(45) NOT NULL COMMENT '类型CODE',
  `type_name` varchar(45) NOT NULL COMMENT '类型名称',
  `status` int(3) NOT NULL DEFAULT '0' COMMENT '状态，0-有效，1-无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 ;


--
-- Table structure for table `environment_info`
--

DROP TABLE IF EXISTS `environment_info`;

CREATE TABLE `environment_info` (
  `environment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '环境ID',
  `environment_name` varchar(50) NOT NULL DEFAULT '' COMMENT '环境名称',
  `environment_version` varchar(100) DEFAULT NULL COMMENT '环境版本',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status_code` varchar(50) NOT NULL DEFAULT '0' COMMENT '状态编码',
  PRIMARY KEY (`environment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


--
-- Table structure for table `execute_history`
--

DROP TABLE IF EXISTS `execute_history`;

CREATE TABLE `execute_history` (
  `execute_history_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '执行历史ID',
  `execute_status` int(2) NOT NULL DEFAULT '0' COMMENT '执行状态(-1:执行失败,0:执行中,1:执行成功)',
  `execute_sql` varchar(2000) DEFAULT NULL COMMENT '执行SQL',
  `execute_time` bigint(20) DEFAULT NULL COMMENT '执行耗时',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '数据插入时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新者用户ID',
  `update_time` datetime DEFAULT NULL COMMENT '数据更新时',
  PRIMARY KEY (`execute_history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=994 DEFAULT CHARSET=utf8;


--
-- Table structure for table `graph_data_info`
--

DROP TABLE IF EXISTS `graph_data_info`;

CREATE TABLE `graph_data_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `data_name` varchar(45) NOT NULL COMMENT '数据名称',
  `data_code` varchar(45) NOT NULL COMMENT '数据Code',
  `data_version` varchar(45) DEFAULT NULL COMMENT '版本',
  `data_desc` varchar(128) NOT NULL COMMENT '数据描述',
  `data_type` int(3) NOT NULL COMMENT '数据类型，0-点，1-边',
  `related_vertex1Id` int(45) DEFAULT NULL COMMENT '关联点2ID',
  `related_vertex2Id` int(45) DEFAULT NULL COMMENT '关联点2ID',
  `status` int(3) NOT NULL DEFAULT '0' COMMENT '数据状态，0-有效，1-无效',
  `create_user` varchar(45) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `project_code` varchar(45) DEFAULT NULL COMMENT '项目Code',
  `security_level` varchar(45) DEFAULT NULL COMMENT '安全等级',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=290 DEFAULT CHARSET=utf8 ;


--
-- Table structure for table `host_env`
--

DROP TABLE IF EXISTS `host_env`;

CREATE TABLE `host_env` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `host_id` bigint(20) NOT NULL COMMENT 'host 主键',
  `environment_id` bigint(20) NOT NULL COMMENT 'environment 主键',
  PRIMARY KEY (`id`),
  UNIQUE KEY `u_host_env` (`host_id`,`environment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;


--
-- Table structure for table `host_info`
--

DROP TABLE IF EXISTS `host_info`;

CREATE TABLE `host_info` (
  `host_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `host_name` varchar(255) NOT NULL COMMENT 'host名称',
  `cpu` int(11) NOT NULL COMMENT 'cpu 核数',
  `memory` int(11) NOT NULL COMMENT '内存 单位G',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  PRIMARY KEY (`host_id`),
  UNIQUE KEY `u_host_info_name` (`host_name`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;


--
-- Table structure for table `instance_environment_mapping`
--

DROP TABLE IF EXISTS `instance_environment_mapping`;

CREATE TABLE `instance_environment_mapping` (
  `instance_environment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '实例环境映射ID',
  `instance_id` bigint(20) NOT NULL COMMENT '实例ID',
  `environment_id` bigint(20) NOT NULL COMMENT '环境ID',
  PRIMARY KEY (`instance_environment_id`),
  UNIQUE KEY `u_instance_env` (`instance_id`,`environment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=330 DEFAULT CHARSET=utf8;


--
-- Table structure for table `instance_host`
--

DROP TABLE IF EXISTS `instance_host`;

CREATE TABLE `instance_host` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `instance_id` bigint(20) NOT NULL COMMENT '实例 id',
  `host_id` bigint(20) NOT NULL COMMENT 'host id',
  `size` int(11) NOT NULL COMMENT '数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `u_instance_host` (`instance_id`,`host_id`)
) ENGINE=InnoDB AUTO_INCREMENT=257 DEFAULT CHARSET=utf8;


--
-- Table structure for table `instance_info`
--

DROP TABLE IF EXISTS `instance_info`;

CREATE TABLE `instance_info` (
  `instance_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '实例ID',
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `resource_inner_id` bigint(20) NOT NULL COMMENT '内部资源ID',
  `resource_template_id` bigint(20) NOT NULL COMMENT '资源模板ID',
  `instance_size` int(11) NOT NULL COMMENT '实例数量',
  `group_name` varchar(255) NOT NULL COMMENT '实例组名',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  `instance_desc` varchar(500) DEFAULT '' COMMENT '实例备注',
  PRIMARY KEY (`instance_id`),
  UNIQUE KEY `u_project_group` (`project_id`,`group_name`),
  KEY `u_instance_info_group_name` (`group_name`),
  KEY `u_instance_info_project` (`project_id`),
  KEY `i_instance_info_resource_inner` (`resource_inner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=149 DEFAULT CHARSET=utf8;


--
-- Table structure for table `instance_operation`
--

DROP TABLE IF EXISTS `instance_operation`;

CREATE TABLE `instance_operation` (
  `operation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '操作ID',
  `instance_id` bigint(20) NOT NULL COMMENT '实例ID',
  `user_id` varchar(255) NOT NULL DEFAULT '' COMMENT '操作人',
  `user_name` varchar(50) DEFAULT NULL COMMENT '操作人名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `status_code` varchar(50) NOT NULL COMMENT '状态编码',
  PRIMARY KEY (`operation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=751 DEFAULT CHARSET=utf8;


--
-- Table structure for table `job`
--

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
  `status` varchar(10) NOT NULL DEFAULT 'UNDEPLOY' COMMENT '状态(UNDEPLOY:未发布 DEPLOY:已发布 DELETE:删除)',
  `use_yn` char(1) DEFAULT 'Y' COMMENT '是否可用 (Y-可用 N-不可用）',
  `warning_config` varchar(1000) DEFAULT NULL COMMENT '告警规则',
  `create_user` varchar(50) DEFAULT NULL COMMENT '任务创建人',
  `create_user_name` varchar(50) DEFAULT NULL COMMENT '任务创建人名',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `deploy_user` varchar(50) DEFAULT NULL COMMENT '任务负责人ID',
  `deploy_user_name` varchar(50) DEFAULT NULL COMMENT '任务负责人名',
  `update_user` varchar(50) DEFAULT NULL COMMENT '任务更新人',
  `update_user_name` varchar(50) DEFAULT NULL COMMENT '任务更新人名',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=515 DEFAULT CHARSET=utf8 ;


--
-- Table structure for table `job_exec_statistic`
--

DROP TABLE IF EXISTS `job_exec_statistic`;

CREATE TABLE `job_exec_statistic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `job_name` varchar(50) NOT NULL COMMENT 'XD 任务运行ID(uuid)',
  `job_alias_name` varchar(255)  DEFAULT NULL COMMENT '任务名（显示）',
  `max_duration` int(8) NULL COMMENT '最大时长',
  `avg_duration` double NULL COMMENT '平均时长',
  `completed_num` int(8) NULL COMMENT '执行完成次数',
  `failed_num` int(8) NULL COMMENT '执行失败次数',
  `running_num` int(8) DEFAULT NULL COMMENT '正在执行次数（开始时间与结束时间不在同一天）',
  `current_running_num` int(8) NULL COMMENT '统计时刻正在执行的实例数量',
  `current_running_time` datetime NOT NULL COMMENT '统计正在执行实例数量时的时间',
  `date` date NOT NULL COMMENT '统计日期',
  `update_time` datetime NULL COMMENT '修改时间',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 PRIMARY KEY (`id`),
 UNIQUE KEY `un_pro_job_date` (`project_id`,`date`,`job_name`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8 ;


--
-- Table structure for table `job_fail_history`
--

DROP TABLE IF EXISTS `job_fail_history`;

CREATE TABLE `job_fail_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_name` varchar(50) NOT NULL COMMENT 'job的job_name，xd job_instance中的job_name',
  `job_execution_id` bigint(20) NOT NULL COMMENT 'xd表中的job执行id',
  `email_status` varchar(16) NOT NULL,
  `send_time` datetime NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=610 DEFAULT CHARSET=utf8;


--
-- Table structure for table `job_step`
--

DROP TABLE IF EXISTS `job_step`;

CREATE TABLE `job_step` (
  `step_id` int(64) NOT NULL AUTO_INCREMENT COMMENT '业务主键',
  `job_id` int(64) NOT NULL COMMENT '数据任务表ID',
  `module_name` varchar(100) NOT NULL COMMENT '模块名',
  `step_name` varchar(50) NOT NULL COMMENT 'XD 步骤运行ID(UUID)',
  `alias_name` varchar(255) DEFAULT NULL COMMENT '步骤别名',
  `instance_id` bigint(20) DEFAULT NULL COMMENT '实例ID',
  `definition` varchar(10000) DEFAULT NULL COMMENT 'step definition',
  `data_input` varchar(100) DEFAULT NULL COMMENT '任务输入',
  `data_output` varchar(100) DEFAULT NULL COMMENT '任务输出',
  `config` varchar(10000) DEFAULT NULL COMMENT 'Step配置参数信息',
  `remark` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_user` varchar(50) DEFAULT NULL COMMENT '任务创建人',
  `create_time` datetime DEFAULT NULL COMMENT '数据插入时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新者用户ID',
  `update_time` datetime DEFAULT NULL COMMENT '数据更新时',
  `instance_size` int(10) DEFAULT NULL,
  PRIMARY KEY (`step_id`)
) ENGINE=InnoDB AUTO_INCREMENT=518 DEFAULT CHARSET=utf8 ;


--
-- Table structure for table `project_info`
--

DROP TABLE IF EXISTS `project_info`;

CREATE TABLE `project_info` (
  `project_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_code` varchar(50) NOT NULL DEFAULT '' COMMENT '项目名称',
  `project_name` varchar(50) NOT NULL DEFAULT '' COMMENT '项目名称',
  `project_desc` varchar(255) DEFAULT '' COMMENT '项目描述',
  `project_manager_id` varchar(50) NOT NULL DEFAULT '' COMMENT '项目负责人userid',
  `manager_telephone` varchar(50) DEFAULT NULL COMMENT '负责人电话',
  `create_user_id` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `caas_topic_id` bigint(20) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `status_code` VARCHAR(50) NULL,
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `u_project_code` (`project_code`),
  UNIQUE KEY `u_project_name` (`project_name`)
) ENGINE=InnoDB AUTO_INCREMENT=222640 DEFAULT CHARSET=utf8;


--
-- Table structure for table `project_member_mapping`
--

DROP TABLE IF EXISTS `project_member_mapping`;

CREATE TABLE `project_member_mapping` (
  `project_member_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目成员关系ID',
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `role_id` varchar(50) DEFAULT NULL,
  `user_id` varchar(50) NOT NULL DEFAULT '' COMMENT '成员ID',
  `is_filtered` varchar(5) DEFAULT 'Y' COMMENT '项目是否被过滤\n可选值 Y (该项目被选中过滤)',
  `update_time` TIMESTAMP NULL COMMENT '最近更新时间',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '入库时间',
  PRIMARY KEY (`project_member_id`),
  UNIQUE KEY `project_member_uk`(`project_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=314 DEFAULT CHARSET=utf8;


--
-- Table structure for table `project_resource_mapping`
--

DROP TABLE IF EXISTS `project_resource_mapping`;

CREATE TABLE `project_resource_mapping` (
  `project_resource_out_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目外部资源映射ID',
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `resource_out_id` bigint(20) NOT NULL COMMENT '外部资源ID',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  PRIMARY KEY (`project_resource_out_id`),
  UNIQUE KEY `u_project_resource` (`project_id`,`resource_out_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `resource_inner`
--

DROP TABLE IF EXISTS `resource_inner`;

CREATE TABLE `resource_inner` (
  `resource_inner_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '内部资源ID',
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `create_user_id` varchar(50) NOT NULL DEFAULT '' COMMENT '用户ID',
  `resource_cpu` int(10) NOT NULL COMMENT 'CPU',
  `resource_memory` int(10) NOT NULL COMMENT '内存',
  `resource_storage` int(10) NOT NULL COMMENT '存储',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status_code` varchar(50) NOT NULL DEFAULT '' COMMENT '状态编码',
  PRIMARY KEY (`resource_inner_id`),
  UNIQUE KEY `u_resource_inner_project_id` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8;


--
-- Table structure for table `resource_operation`
--

DROP TABLE IF EXISTS `resource_operation`;

CREATE TABLE `resource_operation` (
  `operation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '操作ID',
  `resource_id` bigint(20) DEFAULT NULL COMMENT '资源ID',
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `cpu` int(11) NOT NULL COMMENT 'cpu',
  `memory` int(11) NOT NULL COMMENT '内存 G',
  `storage` int(11) NOT NULL COMMENT '存储 G',
  `create_user_id` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` varchar(50) DEFAULT NULL COMMENT '修改人',
  `update_user_name` varchar(50) DEFAULT NULL COMMENT '修改人名称',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `reason` varchar(255) NOT NULL DEFAULT '' COMMENT '原因',
  `status_code` varchar(50) NOT NULL COMMENT '状态编码',
  `notes` varchar(255) DEFAULT NULL COMMENT '处理备注',
  PRIMARY KEY (`operation_id`),
  KEY `i_project` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=167 DEFAULT CHARSET=utf8;


--
-- Table structure for table `resource_out`
--

DROP TABLE IF EXISTS `resource_out`;

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
) ENGINE=InnoDB AUTO_INCREMENT=123 DEFAULT CHARSET=utf8;


--
-- Table structure for table `resource_template`
--

DROP TABLE IF EXISTS `resource_template`;

CREATE TABLE `resource_template` (
  `resource_template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '资源模板ID',
  `resource_name` varchar(200) NOT NULL DEFAULT '' COMMENT '资源模板名称,不允许中文',
  `resource_cpu` int(10) NOT NULL COMMENT 'CPU',
  `resource_memory` int(10) NOT NULL DEFAULT '1' COMMENT '内存',
  `resource_storage` int(10) NOT NULL DEFAULT '1' COMMENT '存储',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `status_code` varchar(50) NOT NULL DEFAULT '0' COMMENT '状态编码',
  PRIMARY KEY (`resource_template_id`),
  UNIQUE KEY `idx_uniq_resource_name` (`resource_name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;


--
-- Table structure for table `resource_type`
--

DROP TABLE IF EXISTS `resource_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_type` (
  `id` int(100) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type_code` varchar(20) NOT NULL DEFAULT '' COMMENT '资源类型编码',
  `type_name` varchar(25) NOT NULL COMMENT '资源名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


--
-- Table structure for table `resource_usage`
--

DROP TABLE IF EXISTS `resource_usage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_usage` (
  `id` int(100) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `usage_code` varchar(20) NOT NULL DEFAULT '' COMMENT '资源使用编码',
  `usage_name` varchar(25) NOT NULL COMMENT '资源使用名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


--
-- Table structure for table `resource_used_stats`
--

DROP TABLE IF EXISTS `resource_used_stats`;

CREATE TABLE `resource_used_stats` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `policy_id` varchar(11) NOT NULL COMMENT '事件 id',
  `resource_name` varchar(128) NOT NULL COMMENT '资源名称，表名',
  `service_name` varchar(50) NOT NULL COMMENT '服务名称,如：ChorusCluster_hive',
  `resource_type` varchar(20) NOT NULL COMMENT '资源类型，column, table,database, FUNCTION etc',
  `used_count` bigint(20) NOT NULL COMMENT '使用次数统计',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `rs_sv_uq` (`resource_name`,`service_name`,`resource_type`,`policy_id`)
) ENGINE=InnoDB AUTO_INCREMENT=45176 DEFAULT CHARSET=utf8;


--
-- Table structure for table `schedule`
--

DROP TABLE IF EXISTS `schedule`;

CREATE TABLE `schedule` (
  `schedule_id` int(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `job_id` int(64) NOT NULL COMMENT '数据任务表ID',
  `schedule_name` varchar(100) DEFAULT NULL COMMENT '计划名称',
  `schedule_stat` int(5) DEFAULT '0' COMMENT '定时任务状态(0:禁用;1:启用)',
  `schedule_type` int(5) DEFAULT '2' COMMENT '任务类型(1:一次性;2:周期)',
  `schedule_cycle` int(5) DEFAULT NULL COMMENT '调度周期(1:日;2:周3;月)',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `repeat_count` int(10) DEFAULT '-1' COMMENT '重复次数(执行次数=1(开始时间执行的一次)+重复次数,0:不重复执行;-1:不限制次数;大于0:具体次数)',
  `repeat_interval` int(10) DEFAULT '1' COMMENT '重复周期(单位:小时/分钟)',
  `second` varchar(50) DEFAULT NULL COMMENT '执行时间-秒(0~59)',
  `minute` varchar(50) DEFAULT NULL COMMENT '执行时间-分(0~59)',
  `hour` varchar(50) DEFAULT NULL COMMENT '执行时间-时(0~23)',
  `day` varchar(50) DEFAULT NULL COMMENT '执行时间-日(1~31)',
  `week` varchar(50) DEFAULT NULL COMMENT '执行时间-周(1~7,周日是1)',
  `month` varchar(50) DEFAULT NULL COMMENT '执行时间-月(1~12)',
  `cron_expression` varchar(50) DEFAULT NULL COMMENT 'CRON表达式',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`schedule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=456 DEFAULT CHARSET=utf8 ;


--
-- Table structure for table `stream_exec_statistic`
--

DROP TABLE IF EXISTS `stream_exec_statistic`;

CREATE TABLE `stream_exec_statistic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `no_exec_num` int(8) DEFAULT NULL COMMENT '未执行任务数量',
  `failed_num` int(8) DEFAULT NULL COMMENT '执行任务失败数量',
  `running_num` int(8) DEFAULT NULL COMMENT '正在执行任务数量',
  `date` date NOT NULL COMMENT '统计日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pro_job_date_1` (`project_id`,`date`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8 ;


--
-- Table structure for table `table_authority`
--

DROP TABLE IF EXISTS `table_authority`;

CREATE TABLE `table_authority` (
  `table_authority_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '权限关系ID',
  `table_info_id` bigint(20) NOT NULL COMMENT '表ID',
  `column_info_id` bigint(20) NOT NULL COMMENT '列ID',
  `user_id` varchar(50) NOT NULL DEFAULT '' COMMENT '用户ID',
  `end_date` date DEFAULT NULL COMMENT '失效日期',
  PRIMARY KEY (`table_authority_id`),
  UNIQUE KEY `uk_user_column` (`user_id`,`column_info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=123 DEFAULT CHARSET=utf8;


--
-- Table structure for table `table_info`
--

DROP TABLE IF EXISTS `table_info`;

CREATE TABLE `table_info` (
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `table_info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
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
) ENGINE=InnoDB AUTO_INCREMENT=123457188 DEFAULT CHARSET=utf8;


--
-- Table structure for table `table_monitor`
--

DROP TABLE IF EXISTS `table_monitor`;

CREATE TABLE `table_monitor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `table_info_id` bigint(20) NOT NULL,
  `monitor_date` date NOT NULL,
  `rows` bigint(20) DEFAULT NULL,
  `storage_size` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `table_unique` (`table_info_id`,`monitor_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1402 DEFAULT CHARSET=utf8 ;


--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;

CREATE TABLE `task` (
  `task_id` int(64) NOT NULL AUTO_INCREMENT COMMENT '业务主键',
  `job_id` int(64) NOT NULL COMMENT '数据任务表ID',
  `module_type` int(10) NOT NULL COMMENT '模块类型 1:默认Job 2:用户自定义Job 3:默认Stream source 4:用户自定义Stream source 5:默认Stream processor 6:用自定义Stream processor 7:默认Stream sink 8:用户自定义Stream sink 9:默认Stream other 10:用户自定义Stream other 11:job definition',
  `module_name` varchar(100) NOT NULL COMMENT '模块名',
  `task_name` varchar(50) NOT NULL COMMENT 'XD 步骤运行ID(UUID)',
  `alias_name` varchar(255) DEFAULT NULL COMMENT '步骤别名',
  `instance_id` bigint(20) DEFAULT NULL COMMENT '实例ID',
  `task_dsl` varchar(10000) DEFAULT NULL COMMENT 'task definition',
  `data_input` varchar(100) DEFAULT NULL COMMENT '任务输入',
  `data_output` varchar(100) DEFAULT NULL COMMENT '任务输出',
  `config` varchar(10000) DEFAULT NULL COMMENT 'Step配置参数信息',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `variable` varchar(500) DEFAULT NULL COMMENT '变量Json',
  `create_user` varchar(50) DEFAULT NULL COMMENT '任务创建人',
  `create_user_name` varchar(50) DEFAULT NULL COMMENT '任务创建人名',
  `create_time` datetime DEFAULT NULL COMMENT '数据插入时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新者用户ID',
  `update_user_name` varchar(50) DEFAULT NULL COMMENT '更新者用户名',
  `update_time` datetime DEFAULT NULL COMMENT '数据更新时',
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=770 DEFAULT CHARSET=utf8 ;


--
-- Table structure for table `task_execution_timeout`
--

DROP TABLE IF EXISTS `task_execution_timeout`;

CREATE TABLE `task_execution_timeout` (
  `task_id` bigint(20) NOT NULL,
  `task_execution_id` bigint(20) NOT NULL,
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expect_end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`task_execution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;


--
-- Table structure for table `xd_module`
--

DROP TABLE IF EXISTS `xd_module`;

CREATE TABLE `xd_module` (
  `module_id` int(64) NOT NULL AUTO_INCREMENT COMMENT '业务主键',
  `module_type` int(10) NOT NULL DEFAULT '1' COMMENT '模块类型 1:默认Job 2:用户自定义Job 3:默认Stream source 4:用户自定义Stream source 5:默认Stream processor 6:用自定义Stream processor 7:默认Stream sink 8:用户自定义Stream sink 9:默认Stream other 10:用户自定义Stream other 11:job definition',
  `module_name` varchar(100) NOT NULL COMMENT 'Spring XD 定义Module',
  `module_alias_name` varchar(100) DEFAULT NULL COMMENT 'Module画面显示名',
  `remark` varchar(255) DEFAULT NULL COMMENT '描述',
  `sort_num` int(10) DEFAULT '0' COMMENT '显示顺序',
  `use_yn` char(1) DEFAULT 'Y' COMMENT '是否可用 (Y-可用 N-不可用）',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `module_level` tinyint(2) NOT NULL DEFAULT '0' COMMENT '组件级别，0：平台级别，1：项目级别',
  `project_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '项目Id ，组件所属项目ID,如果属于平台的话，该值为0',
  `module_category` tinyint(2) NOT NULL DEFAULT '0' COMMENT '组件类别，0：批量，1：流式',
  `file_name` varchar(64) NOT NULL DEFAULT '' COMMENT '上传文件名称',
  `module_view_name` varchar(120) NOT NULL DEFAULT '' COMMENT '组件在前端显示的名称',
  `create_user_name` varchar(100) NOT NULL DEFAULT '' COMMENT '组件创建用户名',
  `update_user_name` varchar(100) DEFAULT '' COMMENT '组件更新用户名',
  PRIMARY KEY (`module_id`)
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8 ;


--
-- Table structure for table `xd_prop_element`
--

DROP TABLE IF EXISTS `xd_prop_element`;
CREATE TABLE `xd_prop_element` (
  `id` int(64) NOT NULL AUTO_INCREMENT COMMENT '业务主键',
  `xd_module_id` int(64) NOT NULL COMMENT 'Module主键',
  `xd_module_type` int(2) NOT NULL DEFAULT '2' COMMENT '模块类型 1:Job 2:Stream',
  `name` varchar(20) NOT NULL COMMENT '元素id',
  `type` varchar(20) NOT NULL COMMENT '元素类型',
  `label` varchar(30) DEFAULT NULL COMMENT '元素显示名',
  `placeholder` varchar(100) DEFAULT NULL COMMENT '可描述输入字段预期值的提示信息',
  `validate_config` varchar(2000) DEFAULT NULL COMMENT '属性验证配置信息',
  `remark` varchar(255) DEFAULT NULL COMMENT '描述',
  `sort_num` int(10) DEFAULT '0' COMMENT '显示顺序',
  `use_yn` char(1) DEFAULT 'Y' COMMENT '是否可用 (Y-可用 N-不可用）',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8 ;


--
-- Table structure for table `xd_prop_element`
--
DROP TABLE IF EXISTS `platform_resource_snapshot`;

CREATE TABLE `platform_resource_snapshot` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `snapshot_date` date NOT NULL COMMENT '快照日期',
  `applied_cpu` int(10) NOT NULL DEFAULT '0' COMMENT '平台当日已申请CPU总量',
  `platform_cpu` int(10) NOT NULL DEFAULT '0' COMMENT '平台CPU总量',
  `applied_memory` int(10) NOT NULL DEFAULT '0' COMMENT '平台当日已申请总量',
  `platform_memory` int(10) NOT NULL DEFAULT '0' COMMENT '平台内存总量',
  `applied_storage` int(10) NOT NULL DEFAULT '0' COMMENT '平台当日已申请存储总量',
  `platform_storage` int(10) NOT NULL DEFAULT '0' COMMENT '平台存储总量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `u_snapshot_date` (`snapshot_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

DROP TABLE IF EXISTS `project_resource_kpi_snapshot`;

CREATE TABLE `project_resource_kpi_snapshot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `project_name` varchar(50) NOT NULL DEFAULT '' COMMENT '项目名称',
  `kpi_date` date NOT NULL COMMENT '指标日期',
  `cpu_total` int(10) NOT NULL DEFAULT 0 COMMENT '总CPU数 单位(核)',
  `cpu_used` int(10) NOT NULL DEFAULT 0 COMMENT '已使用CPU 单位(核)',
  `cpu_usage` double NOT NULL DEFAULT 0 COMMENT 'CPU使用率',
  `memory_total` int(10) NOT NULL DEFAULT 0 COMMENT '总内存数 单位(G)',
  `memory_used` int(10) NOT NULL DEFAULT 0 COMMENT '已使用内存 单位(G)',
  `memory_usage` double NOT NULL DEFAULT 0 COMMENT '内存使用率',
  `storage_total` bigint(20) NOT NULL DEFAULT 0 COMMENT '存储总量 单位(byte)',
  `storage_used` bigint(20) NOT NULL DEFAULT 0 COMMENT '数据总量 单位(byte)',
  `storage_usage` double NOT NULL DEFAULT 0 COMMENT '存储使用率',
  `data_daily_incr` bigint(20) NOT NULL DEFAULT 0 COMMENT '数据日增量 单位byte',
  `task_total` int(10) NOT NULL DEFAULT 0 COMMENT '当天启动的任务总量',
  `task_success` int(10) NOT NULL DEFAULT 0 COMMENT '当天启动的任务成功量',
  `task_success_rate` double DEFAULT 0 COMMENT '任务成功率',
  `score` tinyint(4) DEFAULT NULL COMMENT '资源利用率综合得分，得分范围为(0-5)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_uniq_kpi_date` (`project_id`,`kpi_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

--
-- table  apply_detail_v2
--
DROP TABLE IF EXISTS `apply_detail_v2`;
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
DROP TABLE IF EXISTS `apply_form_v2`;
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
DROP TABLE IF EXISTS `table_authority_v2`;
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
DROP TABLE IF EXISTS `table_monitor_v2`;
CREATE TABLE `table_monitor_v2` (
  `id`            BIGINT(20)  NOT NULL AUTO_INCREMENT,
  `project_id`    BIGINT(20)  NOT NULL,
  `table_info_id` VARCHAR(50) NOT NULL,
  `table_name`      VARCHAR(50) NOT NULL COMMENT '表名',
  `monitor_date`  DATE        NOT NULL,
  `rows`          BIGINT(20)           DEFAULT NULL,
  `storage_size`  BIGINT(20)           DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_table_info_date` (`table_info_id`, `monitor_date`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `ROLE_ID` varchar(255) NOT NULL,
  `ROLE_CODE` varchar(255) DEFAULT NULL,
  `ROLE_NAME` varchar(255) DEFAULT NULL,
  `ROLE_TYPE` varchar(255) DEFAULT NULL,
  `REMARK` varchar(255) DEFAULT NULL,
  `create_user` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `permission` varchar(255) DEFAULT NULL COMMENT '可用权限',
  PRIMARY KEY (`ROLE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;