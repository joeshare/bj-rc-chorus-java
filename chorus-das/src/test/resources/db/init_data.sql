

--
-- Dumping data for table `common_status`
--


INSERT INTO `common_status` VALUES (1,'1000','资源申请待审批','10'),(2,'1501','待审批','15'),(3,'1502','全通过','15'),(4,'1503','全拒绝','15'),(5,'2101','已创建','21'),(6,'2102','已启动','21'),(7,'2103','已停止','21'),(8,'2104','已销毁','21'),(9,'1001','资源申请已通过','10'),(10,'1002','资源申请已拒绝','10'),(11,'1504','部分通过','15'),(12,'1512','通过','15'),(13,'1513','拒绝','15'),(14,'1511','待审批','15');


--
-- Dumping data for table `datatype_element`
--


INSERT INTO `datatype_element` VALUES (1,'String','String',0),(2,'Character','Character',0),(3,'Boolean','Boolean',0),(4,'Short','Short',0),(5,'Integer','Integer',0),(6,'Long','Long',0),(7,'Float','Float',0),(8,'Double','Double',0),(9,'Decimal','Decimal',1),(10,'Precision','Precision',1),(11,'Date','Date',0),(12,'Geoshape','Geoshape',1),(13,'UUID','UUID',1);


--
-- Dumping data for table `environment_info`
--


INSERT INTO `environment_info` VALUES (1,'java','1.8','2016-12-02 21:38:09','2016-12-02 21:38:09','0');


--
-- Dumping data for table `host_env`
--

INSERT INTO `host_env` VALUES (1,1,1),(2,1,2),(5,3,1),(6,3,2),(9,5,1),(10,5,2),(12,6,1),(11,6,2),(14,7,1),(13,7,2),(15,8,1),(16,8,2),(17,9,1),(18,9,2),(19,10,1),(20,10,2),(21,12,1),(22,12,2),(23,13,1),(24,13,2),(25,14,1),(26,14,2),(27,15,1),(28,15,2),(29,16,1),(30,16,2),(31,17,1),(32,17,2);

--
-- Dumping data for table `host_info`
--

INSERT INTO `host_info` VALUES (1,'bj-rc-dptd-ambari-dd-1-v-test-1.host.dataengine.com',3,8,'2016-10-10 00:00:00',NULL,''),(2,'bj-rc-dptd-ambari-dd-3-v-test-1.host.dataengine.com',3,8,'2016-10-10 00:00:00',NULL,''),(3,'bj-rc-dptd-ambari-dd-2-v-test-1.host.dataengine.com',3,8,'2016-10-10 00:00:00',NULL,''),(5,'bj-rc-dptd-ambari-dd-4-v-test-1.host.dataengine.com',3,8,'2016-10-10 00:00:00',NULL,''),(6,'bj-rc-dptd-ambari-dd-5-v-test-1.host.dataengine.com',3,8,'2016-10-10 00:00:00',NULL,''),(7,'bj-rc-dptd-ambari-dd-6-v-test-1.host.dataengine.com',3,8,'2016-10-10 00:00:00',NULL,''),(8,'bj-rc-dptd-sc-v-test-4.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(9,'bj-rc-dptd-sc-v-test-5.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(10,'bj-rc-dptd-sc-v-test-6.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(12,'bj-rc-dptd-sc-v-test-7.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(13,'bj-rc-dptd-sc-v-test-8.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(14,'bj-rc-dptd-sc-v-test-9.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(15,'bj-rc-dptd-sc-v-test-10.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(16,'bj-rc-dptd-sc-v-test-11.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,'');


--
-- Dumping data for table `resource_template`
--


INSERT INTO `resource_template` VALUES (5,'container-locality-template',1,2,1,'2016-12-02 17:36:25','2016-12-02 17:36:28','0'),(6,'container-nolocality-template-proxy',1,1,1,'2016-12-02 17:36:25','2016-12-02 17:36:25','0'),(9,'container-locality-template-2c4g',2,4,1,'2017-07-05 12:22:35','2017-07-05 12:22:35','0');


--
-- Dumping data for table `resource_type`
--

INSERT INTO `resource_type` VALUES (1,'MYSQL','MySQL');


--
-- Dumping data for table `resource_usage`
--

INSERT INTO `resource_usage` VALUES (1,'TEST','测试'),(2,'DEV','开发');

INSERT INTO t_role (ROLE_ID,ROLE_CODE,ROLE_NAME,ROLE_TYPE,REMARK,create_user,create_time,update_time,permission) VALUES
('2','913','数据分析人员','CHA','1','0','2016-11-25 16:04:12','2016-11-25 16:04:15','select')
,('1','911','数据开发人员','KAI','1','0','2016-11-25 16:04:12','2016-11-25 16:04:15','select,Create')
,('5','915','项目管理员','ADM','1','0','2016-11-25 16:04:12','2016-11-25 16:04:15','select,update,Create,Drop,Alter,Index,Lock,All')
,('4','914','项目Owner','OWN','1','0','2016-11-25 16:04:12','2016-11-25 16:04:15','select,update,Create,Drop,Alter,Index,Lock,All')
;

INSERT INTO `project_info`(project_id, project_code, project_name, project_desc, project_manager_id, create_user_id,create_time,user_name, status_code,caas_topic_id)
VALUES(1,'p01','p01','',1,1,'2017-08-08 18:28:00','test',1205,1);
INSERT INTO `project_info` (`project_id`, `project_code`, `project_name`, `project_desc`, `project_manager_id`, `manager_telephone`, `create_user_id`, `create_time`, `update_user_id`, `update_time`, `caas_topic_id`, `user_name`)
VALUES (2, 'first_project_code', 'first_project_name', 'first project desc', '121', '15001482721', '1888', '2017-08-09 13:07:09', '212323', '2017-08-09 13:07:09', 1228, 'testing');


INSERT INTO `project_member_mapping`(project_member_id,project_id,role_id,user_id,create_time) VALUES (1,1,4,1,'2017-08-09 11:57:22');
INSERT INTO project_member_mapping (`project_member_id`, `project_id`, `role_id`, `user_id`, `create_time`) VALUES (2, 2, 1, 1234,'2017-08-09 11:57:22');
