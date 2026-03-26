-- MySQL dump 10.13  Distrib 9.6.0, for macos26.3 (arm64)
--
-- Host: 127.0.0.1    Database: evaluation
-- ------------------------------------------------------
-- Server version	9.6.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '6a12693a-28b0-11f1-b8c5-246db74e60f7:1-217';

--
-- Table structure for table `evaluation_apply`
--

DROP TABLE IF EXISTS `evaluation_apply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_apply` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申报ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `period_id` bigint NOT NULL COMMENT '综测周期ID',
  `status` varchar(32) NOT NULL COMMENT '状态（DRAFT/SUBMITTED/APPROVED/REJECTED）',
  `total_score` decimal(6,2) DEFAULT NULL COMMENT '申报总分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生综测申报表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_apply`
--

LOCK TABLES `evaluation_apply` WRITE;
/*!40000 ALTER TABLE `evaluation_apply` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_apply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_apply_item`
--

DROP TABLE IF EXISTS `evaluation_apply_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_apply_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申报项ID',
  `apply_id` bigint NOT NULL COMMENT '申报ID',
  `rule_item_id` bigint NOT NULL COMMENT '规则项ID',
  `score` decimal(6,2) DEFAULT NULL COMMENT '计算得分',
  `status` varchar(32) NOT NULL COMMENT '状态（PENDING/APPROVED/REJECTED）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='申报指标项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_apply_item`
--

LOCK TABLES `evaluation_apply_item` WRITE;
/*!40000 ALTER TABLE `evaluation_apply_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_apply_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_apply_material`
--

DROP TABLE IF EXISTS `evaluation_apply_material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_apply_material` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '材料ID',
  `apply_item_id` bigint NOT NULL COMMENT '申报项ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `file_url` varchar(500) NOT NULL COMMENT '文件访问地址（MinIO）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='申报材料表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_apply_material`
--

LOCK TABLES `evaluation_apply_material` WRITE;
/*!40000 ALTER TABLE `evaluation_apply_material` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_apply_material` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_audit_record`
--

DROP TABLE IF EXISTS `evaluation_audit_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_audit_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核记录ID',
  `apply_item_id` bigint NOT NULL COMMENT '申报项ID',
  `auditor_id` bigint NOT NULL COMMENT '审核人ID',
  `audit_result` varchar(32) NOT NULL COMMENT '审核结果（PASS/REJECT）',
  `remark` varchar(255) DEFAULT NULL COMMENT '审核意见',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审核记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_audit_record`
--

LOCK TABLES `evaluation_audit_record` WRITE;
/*!40000 ALTER TABLE `evaluation_audit_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_audit_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_calc_task`
--

DROP TABLE IF EXISTS `evaluation_calc_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_calc_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `period_id` bigint NOT NULL COMMENT '综测周期ID',
  `rule_id` bigint NOT NULL COMMENT '规则ID',
  `status` varchar(32) NOT NULL COMMENT '状态（INIT/RUNNING/SUCCESS/FAILED）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测计算任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_calc_task`
--

LOCK TABLES `evaluation_calc_task` WRITE;
/*!40000 ALTER TABLE `evaluation_calc_task` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_calc_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_period`
--

DROP TABLE IF EXISTS `evaluation_period`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_period` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '综测周期ID',
  `period_name` varchar(100) NOT NULL COMMENT '周期名称',
  `start_time` varchar(100) NOT NULL COMMENT '开始时间',
  `end_time` varchar(100) NOT NULL COMMENT '结束时间',
  `status` tinyint NOT NULL COMMENT '状态（0-FROZEN,1-ACTIVE）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测周期表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_period`
--

LOCK TABLES `evaluation_period` WRITE;
/*!40000 ALTER TABLE `evaluation_period` DISABLE KEYS */;
INSERT INTO `evaluation_period` VALUES (1,'2026春季综测','2026-03-01 00:00:00','2026-07-31 23:59:59',1,'2026-03-26 05:53:29','2026-03-26 05:53:29');
/*!40000 ALTER TABLE `evaluation_period` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_result`
--

DROP TABLE IF EXISTS `evaluation_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_result` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '结果ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `period_id` bigint NOT NULL COMMENT '综测周期ID',
  `total_score` decimal(6,2) NOT NULL COMMENT '综测总分',
  `rank_no` int DEFAULT NULL COMMENT '排名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测结果表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_result`
--

LOCK TABLES `evaluation_result` WRITE;
/*!40000 ALTER TABLE `evaluation_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_result_item`
--

DROP TABLE IF EXISTS `evaluation_result_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_result_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '结果项ID',
  `result_id` bigint NOT NULL COMMENT '结果ID',
  `rule_item_id` bigint NOT NULL COMMENT '规则项ID',
  `score` decimal(6,2) NOT NULL COMMENT '得分',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测结果明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_result_item`
--

LOCK TABLES `evaluation_result_item` WRITE;
/*!40000 ALTER TABLE `evaluation_result_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_result_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_rule`
--

DROP TABLE IF EXISTS `evaluation_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `period_id` bigint NOT NULL COMMENT '综测周期ID',
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `version_code` varchar(50) NOT NULL COMMENT '规则版本号',
  `status` tinyint NOT NULL COMMENT '状态（0-FROZEN,1-ACTIVE）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测规则表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_rule`
--

LOCK TABLES `evaluation_rule` WRITE;
/*!40000 ALTER TABLE `evaluation_rule` DISABLE KEYS */;
INSERT INTO `evaluation_rule` VALUES (1,1,'2026春季默认规则','v1',1,'2026-03-26 05:53:29','2026-03-26 05:53:29');
/*!40000 ALTER TABLE `evaluation_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_rule_formula`
--

DROP TABLE IF EXISTS `evaluation_rule_formula`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_rule_formula` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公式ID',
  `rule_id` bigint NOT NULL COMMENT '规则ID',
  `formula` varchar(500) NOT NULL COMMENT '计算公式（Spring EL）',
  `description` varchar(255) DEFAULT NULL COMMENT '公式说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='规则公式表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_rule_formula`
--

LOCK TABLES `evaluation_rule_formula` WRITE;
/*!40000 ALTER TABLE `evaluation_rule_formula` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_rule_formula` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_rule_item`
--

DROP TABLE IF EXISTS `evaluation_rule_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_rule_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则项ID',
  `rule_id` bigint NOT NULL COMMENT '规则ID',
  `item_name` varchar(200) NOT NULL COMMENT '指标项名称',
  `item_type` tinyint NOT NULL COMMENT '0-加分项，1-减分项',
  `item_category` tinyint NOT NULL COMMENT '规则项的分类',
  `level` varchar(200) NOT NULL COMMENT '规则项级别',
  `base_score` decimal(6,2) NOT NULL COMMENT '基础分值',
  `is_competition` tinyint NOT NULL DEFAULT '0' COMMENT '是否为竞赛项，0-非竞赛，1-竞赛项',
  `need_material` tinyint NOT NULL DEFAULT '1' COMMENT '是否需要材料，0-不需要，1-需要',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用，0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='规则指标项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_rule_item`
--

LOCK TABLES `evaluation_rule_item` WRITE;
/*!40000 ALTER TABLE `evaluation_rule_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_rule_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_rule_item_category`
--

DROP TABLE IF EXISTS `evaluation_rule_item_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_rule_item_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则项分类id',
  `rule_id` bigint NOT NULL COMMENT '规则ID',
  `category_name` varchar(200) NOT NULL COMMENT '分类名字',
  `parent_id` tinyint NOT NULL DEFAULT '0' COMMENT '父级分类id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='规则分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_rule_item_category`
--

LOCK TABLES `evaluation_rule_item_category` WRITE;
/*!40000 ALTER TABLE `evaluation_rule_item_category` DISABLE KEYS */;
INSERT INTO `evaluation_rule_item_category` VALUES (1,1,'test',0,'2026-03-17 11:35:16','2026-03-17 11:35:18');
/*!40000 ALTER TABLE `evaluation_rule_item_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_rule_item_limit`
--

DROP TABLE IF EXISTS `evaluation_rule_item_limit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_rule_item_limit` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '限制ID',
  `rule_item_id` bigint NOT NULL COMMENT '规则项ID',
  `max_score` decimal(6,2) NOT NULL COMMENT '封顶分值',
  `max_times` int DEFAULT NULL COMMENT '最大申报次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='规则项限制表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_rule_item_limit`
--

LOCK TABLES `evaluation_rule_item_limit` WRITE;
/*!40000 ALTER TABLE `evaluation_rule_item_limit` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_rule_item_limit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_class`
--

DROP TABLE IF EXISTS `sys_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_class` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `class_name` varchar(100) NOT NULL COMMENT '班级名称',
  `college_id` bigint NOT NULL COMMENT '学院ID（逻辑外键）',
  `grade_year` int NOT NULL COMMENT '年级（如2023）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='班级表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_class`
--

LOCK TABLES `sys_class` WRITE;
/*!40000 ALTER TABLE `sys_class` DISABLE KEYS */;
INSERT INTO `sys_class` VALUES (1,'计算机科学与技术1班',1,2022,'2026-03-18 21:57:04','2026-03-26 13:13:50'),(2,'计算机科学与技术2班',1,2023,'2026-03-18 21:57:04','2026-03-18 21:57:04'),(3,'电子信息工程1班',2,2023,'2026-03-18 21:57:04','2026-03-18 21:57:04'),(4,'电子信息工程2班',2,2023,'2026-03-18 21:57:04','2026-03-18 21:57:04'),(5,'工商管理1班',3,2023,'2026-03-18 21:57:04','2026-03-18 21:57:04'),(6,'汉语言文学1班',4,2023,'2026-03-18 21:57:04','2026-03-18 21:57:04'),(7,'英语1班',5,2023,'2026-03-18 21:57:04','2026-03-18 21:57:04');
/*!40000 ALTER TABLE `sys_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_college`
--

DROP TABLE IF EXISTS `sys_college`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_college` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学院ID',
  `college_name` varchar(100) NOT NULL COMMENT '学院名称',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（1启用 0停用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学院表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_college`
--

LOCK TABLES `sys_college` WRITE;
/*!40000 ALTER TABLE `sys_college` DISABLE KEYS */;
INSERT INTO `sys_college` VALUES (1,'计算机科学与技术学院',1,'2026-03-18 21:56:40','2026-03-18 21:56:40'),(2,'电子信息工程学院',1,'2026-03-18 21:56:40','2026-03-18 21:56:40'),(3,'经济管理学院',1,'2026-03-18 21:56:40','2026-03-18 21:56:40'),(4,'人文学院',1,'2026-03-18 21:56:40','2026-03-18 21:56:40'),(5,'外国语学院',1,'2026-03-18 21:56:40','2026-03-18 21:56:40');
/*!40000 ALTER TABLE `sys_college` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_operation_log`
--

DROP TABLE IF EXISTS `sys_operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NOT NULL COMMENT '操作人ID',
  `operation` varchar(100) NOT NULL COMMENT '操作类型',
  `content` varchar(500) NOT NULL COMMENT '操作内容',
  `ip_address` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_operation_log`
--

LOCK TABLES `sys_operation_log` WRITE;
/*!40000 ALTER TABLE `sys_operation_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_permission`
--

DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `perm_name` varchar(64) NOT NULL COMMENT '权限名称（如：用户管理-查询）',
  `perm_code` varchar(128) NOT NULL COMMENT '权限编码（如：sys:user:list）',
  `parent_id` bigint DEFAULT '0' COMMENT '父权限ID（0为顶级）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（1启用 0禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`perm_code`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES (1,'用户管理','sys:user:menu',100,1,'2026-03-16 16:20:34','2026-03-26 05:23:10'),(2,'角色管理','sys:role:menu',100,1,'2026-03-16 16:20:34','2026-03-26 05:23:10'),(3,'权限管理','sys:perm:menu',100,1,'2026-03-16 16:20:34','2026-03-26 05:23:10'),(4,'学生管理','sys:student:menu',100,1,'2026-03-16 16:20:34','2026-03-26 05:23:10'),(5,'规则分类管理','sys:category:menu',101,1,'2026-03-17 10:29:13','2026-03-26 05:23:10'),(6,'规则总览','sys:rule:menu',101,1,'2026-03-17 10:30:08','2026-03-26 05:23:10'),(7,'规则项管理','sys:item:menu',101,1,'2026-03-17 10:30:45','2026-03-26 05:23:10'),(16,'学院管理','sys:college:menu',101,1,'2026-03-21 13:18:05','2026-03-26 05:23:10'),(17,'班级管理','sys:class:menu',101,1,'2026-03-21 13:18:05','2026-03-26 05:23:10'),(100,'系统管理','sys:group:system',0,1,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(101,'教学评估','sys:group:evaluation',0,1,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(102,'用户分配角色','sys:user:assign:menu',100,1,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(103,'角色分配菜单','sys:role:assign:menu',100,1,'2026-03-26 05:23:10','2026-03-26 05:23:10');
/*!40000 ALTER TABLE `sys_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(64) NOT NULL COMMENT '角色名称（如：学生、管理员，教师）',
  `role_code` varchar(64) NOT NULL COMMENT '角色编码（如：STUDENT、ADMIN、TEACHER）',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（1启用 0禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'学生','STUDENT','普通学生角色',1,'2026-03-16 14:23:33','2026-03-16 14:23:33'),(2,'教师','TEACHER','普通教师角色',1,'2026-03-16 14:23:33','2026-03-16 14:23:33'),(3,'系统管理员','ADMIN','系统最高权限角色',1,'2026-03-16 14:23:33','2026-03-16 14:23:33');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_permission`
--

DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID（关联sys_role.id）',
  `perm_id` bigint NOT NULL COMMENT '权限ID（关联sys_permission.id）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`,`perm_id`) COMMENT '唯一索引：一个角色不能重复绑定同一个权限',
  KEY `idx_perm_id` (`perm_id`) COMMENT '普通索引：便于按权限查询角色'
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` VALUES (17,3,1,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(18,3,2,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(19,3,3,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(20,3,4,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(24,1,4,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(25,3,5,'2026-03-17 10:31:42','2026-03-17 10:31:43'),(26,3,6,'2026-03-17 10:31:49','2026-03-17 10:31:50'),(27,3,7,'2026-03-17 10:31:57','2026-03-17 10:31:59'),(32,3,16,'2026-03-21 13:18:05','2026-03-21 13:18:05'),(33,3,17,'2026-03-21 13:18:05','2026-03-21 13:18:05'),(34,3,100,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(35,3,101,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(36,3,102,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(37,3,103,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(38,2,4,'2026-03-26 13:31:14','2026-03-26 13:31:14'),(39,2,5,'2026-03-26 13:31:14','2026-03-26 13:31:14'),(40,2,6,'2026-03-26 13:31:14','2026-03-26 13:31:14'),(41,2,7,'2026-03-26 13:31:14','2026-03-26 13:31:14'),(42,2,16,'2026-03-26 13:31:14','2026-03-26 13:31:14'),(43,2,17,'2026-03-26 13:31:14','2026-03-26 13:31:14');
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `student_id` varchar(64) NOT NULL COMMENT '登录账号（学号/工号）',
  `password` varchar(255) DEFAULT NULL COMMENT '密码（加密存储）',
  `real_name` varchar(64) NOT NULL COMMENT '真实姓名',
  `college_id` bigint DEFAULT NULL COMMENT '学院ID（逻辑外键）',
  `class_id` bigint DEFAULT NULL COMMENT '班级ID（逻辑外键）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（1启用 0禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','{bcrypt}$2a$10$ijC.UqIhDVWnFOFI9e8fPusbeffVvabJ279LQ1Etqvl4IwZedW0ge','admin',0,0,1,'2026-03-11 16:34:10','2026-03-14 16:03:11'),(2,'2023001','{bcrypt}$2a$10$ijC.UqIhDVWnFOFI9e8fPusbeffVvabJ279LQ1Etqvl4IwZedW0ge','张三',1,101,1,'2026-03-16 14:23:33','2026-03-26 13:28:19'),(3,'2023002','{bcrypt}$2a$10$ijC.UqIhDVWnFOFI9e8fPusbeffVvabJ279LQ1Etqvl4IwZedW0ge','李四',1,101,1,'2026-03-16 14:23:33','2026-03-17 11:35:40'),(4,'2023003','{bcrypt}$2a$10$ijC.UqIhDVWnFOFI9e8fPusbeffVvabJ279LQ1Etqvl4IwZedW0ge','管理员',2,NULL,1,'2026-03-16 14:23:33','2026-03-17 11:35:40'),(5,'23110506122','{bcrypt}$2a$10$BoncPzPOjuy45nWoIRyyXOPvWHpyN3UX5rfCYAmJyXhiM8W/MrEOq','sjk',1,1,1,'2026-03-26 13:28:09','2026-03-26 13:28:30');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联sys_user.id）',
  `role_id` bigint NOT NULL COMMENT '角色ID（关联sys_role.id）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`) COMMENT '唯一索引：一个用户不能重复绑定同一个角色',
  KEY `idx_role_id` (`role_id`) COMMENT '普通索引：便于按角色查询用户'
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1,3,'2026-03-16 14:23:34','2026-03-16 14:34:42'),(2,2,1,'2026-03-16 14:23:34','2026-03-16 14:34:42'),(3,3,2,'2026-03-16 14:23:34','2026-03-16 14:34:42'),(4,4,3,'2026-03-16 14:34:49','2026-03-16 14:34:50'),(6,5,1,'2026-03-26 13:31:22','2026-03-26 13:31:22'),(7,5,2,'2026-03-26 13:31:22','2026-03-26 13:31:22');
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-26 14:40:07
