-- MySQL dump 10.13  Distrib 5.7.12, for osx10.9 (x86_64)
--
-- Host: 10.200.32.83    Database: chorus
-- ------------------------------------------------------
-- Server version	5.7.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `common_status`
--

LOCK TABLES `common_status` WRITE;
/*!40000 ALTER TABLE `common_status` DISABLE KEYS */;
INSERT INTO `common_status` VALUES (1,'1000','资源申请待审批','10'),(2,'1501','待审批','15'),(3,'1502','全通过','15'),(4,'1503','全拒绝','15'),(5,'2101','已创建','21'),(6,'2102','已启动','21'),(7,'2103','已停止','21'),(8,'2104','已销毁','21'),(9,'1001','资源申请已通过','10'),(10,'1002','资源申请已拒绝','10'),(11,'1504','部分通过','15'),(12,'1512','通过','15'),(13,'1513','拒绝','15'),(14,'1511','待审批','15');
/*!40000 ALTER TABLE `common_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `datatype_element`
--

LOCK TABLES `datatype_element` WRITE;
/*!40000 ALTER TABLE `datatype_element` DISABLE KEYS */;
INSERT INTO `datatype_element` VALUES (1,'String','String',0),(2,'Character','Character',0),(3,'Boolean','Boolean',0),(4,'Short','Short',0),(5,'Integer','Integer',0),(6,'Long','Long',0),(7,'Float','Float',0),(8,'Double','Double',0),(9,'Decimal','Decimal',1),(10,'Precision','Precision',1),(11,'Date','Date',0),(12,'Geoshape','Geoshape',1),(13,'UUID','UUID',1);
/*!40000 ALTER TABLE `datatype_element` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `environment_info`
--

LOCK TABLES `environment_info` WRITE;
/*!40000 ALTER TABLE `environment_info` DISABLE KEYS */;
INSERT INTO `environment_info` VALUES (1,'java','1.8','2016-12-02 21:38:09','2016-12-02 21:38:09','0');
/*!40000 ALTER TABLE `environment_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `host_env`
--

LOCK TABLES `host_env` WRITE;
/*!40000 ALTER TABLE `host_env` DISABLE KEYS */;
INSERT INTO `host_env` VALUES (1,1,1),(2,1,2),(5,3,1),(6,3,2),(9,5,1),(10,5,2),(12,6,1),(11,6,2),(14,7,1),(13,7,2),(15,8,1),(16,8,2),(17,9,1),(18,9,2),(19,10,1),(20,10,2),(21,12,1),(22,12,2),(23,13,1),(24,13,2),(25,14,1),(26,14,2),(27,15,1),(28,15,2),(29,16,1),(30,16,2),(31,17,1),(32,17,2);
/*!40000 ALTER TABLE `host_env` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `host_info`
--

LOCK TABLES `host_info` WRITE;
/*!40000 ALTER TABLE `host_info` DISABLE KEYS */;
INSERT INTO `host_info` VALUES (1,'bj-rc-dptd-ambari-dd-1-v-test-1.host.dataengine.com',3,8,'2016-10-10 00:00:00',NULL,''),(2,'bj-rc-dptd-ambari-dd-3-v-test-1.host.dataengine.com',3,8,'2016-10-10 00:00:00',NULL,''),(3,'bj-rc-dptd-ambari-dd-2-v-test-1.host.dataengine.com',3,8,'2016-10-10 00:00:00',NULL,''),(5,'bj-rc-dptd-ambari-dd-4-v-test-1.host.dataengine.com',3,8,'2016-10-10 00:00:00',NULL,''),(6,'bj-rc-dptd-ambari-dd-5-v-test-1.host.dataengine.com',3,8,'2016-10-10 00:00:00',NULL,''),(7,'bj-rc-dptd-ambari-dd-6-v-test-1.host.dataengine.com',3,8,'2016-10-10 00:00:00',NULL,''),(8,'bj-rc-dptd-sc-v-test-4.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(9,'bj-rc-dptd-sc-v-test-5.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(10,'bj-rc-dptd-sc-v-test-6.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(12,'bj-rc-dptd-sc-v-test-7.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(13,'bj-rc-dptd-sc-v-test-8.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(14,'bj-rc-dptd-sc-v-test-9.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(15,'bj-rc-dptd-sc-v-test-10.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,''),(16,'bj-rc-dptd-sc-v-test-11.host.dataengine.com',6,12,'2016-10-10 00:00:00',NULL,'');
/*!40000 ALTER TABLE `host_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `resource_template`
--

LOCK TABLES `resource_template` WRITE;
/*!40000 ALTER TABLE `resource_template` DISABLE KEYS */;
INSERT INTO `resource_template` VALUES (5,'container-locality-template',1,2,1,'2016-12-02 17:36:25','2016-12-02 17:36:28','0'),(6,'container-nolocality-template-proxy',1,1,1,'2016-12-02 17:36:25','2016-12-02 17:36:25','0'),(9,'container-locality-template-2c4g',2,4,1,'2017-07-05 12:22:35','2017-07-05 12:22:35','0');
/*!40000 ALTER TABLE `resource_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `resource_type`
--

LOCK TABLES `resource_type` WRITE;
/*!40000 ALTER TABLE `resource_type` DISABLE KEYS */;
INSERT INTO `resource_type` VALUES (1,'MYSQL','MySQL');
/*!40000 ALTER TABLE `resource_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `resource_usage`
--

LOCK TABLES `resource_usage` WRITE;
/*!40000 ALTER TABLE `resource_usage` DISABLE KEYS */;
INSERT INTO `resource_usage` VALUES (1,'TEST','测试'),(2,'DEV','开发');
/*!40000 ALTER TABLE `resource_usage` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-07-13 11:24:06
