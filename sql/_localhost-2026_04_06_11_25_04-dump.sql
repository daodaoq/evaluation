-- MySQL dump 10.13  Distrib 8.0.45, for macos26.3 (arm64)
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

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '6a12693a-28b0-11f1-b8c5-246db74e60f7:1-832';

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
  PRIMARY KEY (`id`),
  KEY `idx_apply_student_period` (`student_id`,`period_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生综测申报表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_apply`
--

LOCK TABLES `evaluation_apply` WRITE;
/*!40000 ALTER TABLE `evaluation_apply` DISABLE KEYS */;
INSERT INTO `evaluation_apply` VALUES (1,5,2,'APPROVED',0.00,'2026-04-01 11:22:50','2026-04-01 11:23:15'),(2,5,2,'APPROVED',0.00,'2026-04-01 22:21:07','2026-04-01 22:21:26'),(3,5,2,'APPROVED',0.00,'2026-04-03 22:04:44','2026-04-03 22:08:25'),(4,5,2,'APPROVED',0.00,'2026-04-03 22:09:27','2026-04-03 22:09:42'),(5,5,2,'APPROVED',0.00,'2026-04-03 22:10:12','2026-04-03 22:10:25'),(6,5,3,'APPROVED',0.00,'2026-04-05 18:54:55','2026-04-05 18:55:18'),(7,5,3,'APPROVED',0.00,'2026-04-06 10:27:59','2026-04-06 10:28:50'),(8,5,3,'APPROVED',0.00,'2026-04-06 10:29:28','2026-04-06 10:29:38');
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
  `rule_item_id` bigint DEFAULT NULL COMMENT '规则项ID（非细则项可为空）',
  `score` decimal(6,2) DEFAULT NULL COMMENT '计算得分',
  `status` varchar(32) NOT NULL COMMENT '状态（PENDING/APPROVED/REJECTED）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `evidence_no` varchar(128) DEFAULT NULL COMMENT '证书/文件编号',
  `award_level` varchar(64) DEFAULT NULL COMMENT '奖项级别',
  `award_grade` varchar(64) DEFAULT NULL COMMENT '奖项等级',
  `occurred_time` datetime DEFAULT NULL COMMENT '事件发生时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '申报备注',
  `source_type` varchar(16) NOT NULL DEFAULT 'RULE' COMMENT '来源类型：RULE/CUSTOM',
  `custom_name` varchar(200) DEFAULT NULL COMMENT '非细则项名称',
  PRIMARY KEY (`id`),
  KEY `idx_apply_item_apply_status` (`apply_id`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='申报指标项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_apply_item`
--

LOCK TABLES `evaluation_apply_item` WRITE;
/*!40000 ALTER TABLE `evaluation_apply_item` DISABLE KEYS */;
INSERT INTO `evaluation_apply_item` VALUES (1,1,27,0.00,'APPROVED','2026-04-01 11:22:50',NULL,NULL,NULL,NULL,NULL,'RULE',NULL),(2,2,30,0.80,'APPROVED','2026-04-01 22:21:07',NULL,NULL,NULL,NULL,NULL,'RULE',NULL),(3,3,110,0.10,'APPROVED','2026-04-03 22:04:44',NULL,NULL,NULL,NULL,'213123','RULE',NULL),(4,3,153,0.50,'APPROVED','2026-04-03 22:04:44',NULL,NULL,NULL,NULL,'8778','RULE',NULL),(5,4,159,0.00,'APPROVED','2026-04-03 22:09:27',NULL,NULL,NULL,NULL,NULL,'RULE',NULL),(6,5,31,0.50,'APPROVED','2026-04-03 22:10:12',NULL,NULL,NULL,NULL,NULL,'RULE',NULL),(7,6,198,1.00,'APPROVED','2026-04-05 18:54:55',NULL,NULL,NULL,NULL,'213213','RULE',NULL),(8,6,201,0.70,'APPROVED','2026-04-05 18:54:55',NULL,NULL,NULL,NULL,'213','RULE',NULL),(9,7,247,0.10,'APPROVED','2026-04-06 10:27:59',NULL,NULL,NULL,NULL,NULL,'RULE',NULL),(10,7,247,0.10,'APPROVED','2026-04-06 10:27:59',NULL,NULL,NULL,NULL,NULL,'RULE',NULL),(11,7,247,0.10,'APPROVED','2026-04-06 10:27:59',NULL,NULL,NULL,NULL,NULL,'RULE',NULL),(12,7,247,0.10,'APPROVED','2026-04-06 10:27:59',NULL,NULL,NULL,NULL,NULL,'RULE',NULL),(13,8,232,1.00,'APPROVED','2026-04-06 10:29:28',NULL,NULL,NULL,NULL,NULL,'RULE',NULL);
/*!40000 ALTER TABLE `evaluation_apply_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_apply_item_appeal`
--

DROP TABLE IF EXISTS `evaluation_apply_item_appeal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_apply_item_appeal` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申诉ID',
  `apply_item_id` bigint NOT NULL COMMENT '申报项ID',
  `student_id` bigint NOT NULL COMMENT '学生用户ID（sys_user.id，冗余）',
  `reason` varchar(1000) NOT NULL COMMENT '申诉理由',
  `status` varchar(32) NOT NULL COMMENT 'PENDING/ACCEPTED/REJECTED',
  `handler_id` bigint DEFAULT NULL COMMENT '处理人用户ID',
  `handler_remark` varchar(500) DEFAULT NULL COMMENT '处理说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_appeal_item` (`apply_item_id`),
  KEY `idx_appeal_status` (`status`),
  KEY `idx_appeal_student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='申报项申诉';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_apply_item_appeal`
--

LOCK TABLES `evaluation_apply_item_appeal` WRITE;
/*!40000 ALTER TABLE `evaluation_apply_item_appeal` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_apply_item_appeal` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='申报材料表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_apply_material`
--

LOCK TABLES `evaluation_apply_material` WRITE;
/*!40000 ALTER TABLE `evaluation_apply_material` DISABLE KEYS */;
INSERT INTO `evaluation_apply_material` VALUES (1,1,'证明材料','123213123','2026-04-01 11:22:50'),(2,2,'123123','123','2026-04-01 22:21:07'),(3,3,'计算机科学与技术学院本科学生综合素质评价实施细则.pdf','apply-materials/5/3db9b5b2-4327-4c70-b949-5991079677f8_计算机科学与技术学院本科学生综合素质评价实施细则.pdf','2026-04-03 22:04:44'),(4,7,'附件2：2026年春季心理筛查学生操作说明.pdf','apply-materials/5/443bedd9-ddef-4fcf-8f58-007dcaf7da4e_附件2_2026年春季心理筛查学生操作说明.pdf','2026-04-05 18:54:55'),(5,8,'计算机科学与技术学院本科学生综合素质评价实施细则 (1) (2).pdf','apply-materials/5/449111ca-9e14-4cb9-a577-c75ab525f6f6_计算机科学与技术学院本科学生综合素质评价实施细则__1___2_.pdf','2026-04-05 18:54:55'),(6,9,'作业2 黑盒测试技术-等价类划分-02.xlsx','apply-materials/5/1566c0c7-894d-4791-b9bf-a89f559addcc_作业2_黑盒测试技术-等价类划分-02.xlsx','2026-04-06 10:27:59'),(7,10,'作业2 黑盒测试技术-等价类划分-02.xlsx','apply-materials/5/1566c0c7-894d-4791-b9bf-a89f559addcc_作业2_黑盒测试技术-等价类划分-02.xlsx','2026-04-06 10:27:59'),(8,11,'作业2 黑盒测试技术-等价类划分-02.xlsx','apply-materials/5/1566c0c7-894d-4791-b9bf-a89f559addcc_作业2_黑盒测试技术-等价类划分-02.xlsx','2026-04-06 10:27:59'),(9,12,'作业2 黑盒测试技术-等价类划分-02.xlsx','apply-materials/5/1566c0c7-894d-4791-b9bf-a89f559addcc_作业2_黑盒测试技术-等价类划分-02.xlsx','2026-04-06 10:27:59'),(10,13,'作业3 黑盒测试技术-边界值分析-组名 (1).xlsx','apply-materials/5/4da9d37a-67a5-40a5-8314-6d45af1adcb9_作业3_黑盒测试技术-边界值分析-组名__1_.xlsx','2026-04-06 10:29:28');
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
  PRIMARY KEY (`id`),
  KEY `idx_audit_apply_item` (`apply_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审核记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_audit_record`
--

LOCK TABLES `evaluation_audit_record` WRITE;
/*!40000 ALTER TABLE `evaluation_audit_record` DISABLE KEYS */;
INSERT INTO `evaluation_audit_record` VALUES (1,1,1,'PASS',NULL,'2026-04-01 11:23:15'),(2,2,1,'PASS',NULL,'2026-04-01 22:21:26'),(3,4,1,'PASS',NULL,'2026-04-03 22:08:23'),(4,3,1,'PASS',NULL,'2026-04-03 22:08:25'),(5,5,1,'PASS',NULL,'2026-04-03 22:09:42'),(6,6,1,'PASS',NULL,'2026-04-03 22:10:25'),(7,8,1,'PASS',NULL,'2026-04-05 18:55:16'),(8,7,1,'PASS',NULL,'2026-04-05 18:55:18'),(9,12,1,'PASS',NULL,'2026-04-06 10:28:34'),(10,11,1,'PASS',NULL,'2026-04-06 10:28:36'),(11,10,1,'PASS',NULL,'2026-04-06 10:28:39'),(12,9,1,'PASS',NULL,'2026-04-06 10:28:50'),(13,13,1,'PASS',NULL,'2026-04-06 10:29:38');
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
-- Table structure for table `evaluation_objection`
--

DROP TABLE IF EXISTS `evaluation_objection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_objection` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '异议ID',
  `publicity_id` bigint NOT NULL COMMENT '公示ID',
  `student_id` bigint NOT NULL COMMENT '提出异议学生ID',
  `target_student_id` bigint NOT NULL COMMENT '异议目标学生ID',
  `reason` varchar(500) NOT NULL COMMENT '异议理由',
  `status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态 PENDING/ACCEPTED/REJECTED',
  `reply` varchar(500) DEFAULT NULL COMMENT '处理回复',
  `reviewer_id` bigint DEFAULT NULL COMMENT '处理人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `period_id` bigint DEFAULT NULL COMMENT '综测周期ID',
  `student_user_id` bigint DEFAULT NULL COMMENT '学生用户ID',
  `class_id` int DEFAULT NULL COMMENT '班级ID快照',
  `content` varchar(2000) DEFAULT NULL COMMENT '异议内容',
  `handler_user_id` bigint DEFAULT NULL COMMENT '处理人用户ID',
  `handler_remark` varchar(500) DEFAULT NULL COMMENT '处理说明',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测异议与复核表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_objection`
--

LOCK TABLES `evaluation_objection` WRITE;
/*!40000 ALTER TABLE `evaluation_objection` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_objection` ENABLE KEYS */;
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
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint NOT NULL COMMENT '状态（0-FROZEN,1-ACTIVE）',
  `archived` tinyint NOT NULL DEFAULT '0' COMMENT '1=归档锁定（不可改分/申报/审批等）',
  `application_start_time` datetime DEFAULT NULL COMMENT '申报开放起（空则用 start_time）',
  `application_end_time` datetime DEFAULT NULL COMMENT '申报截止（空则用 end_time）',
  `review_end_time` datetime DEFAULT NULL COMMENT '教师审核截止（空则不限制）',
  `public_notice_start` datetime DEFAULT NULL COMMENT '公示开始',
  `public_notice_end` datetime DEFAULT NULL COMMENT '公示结束',
  `objection_end_time` datetime DEFAULT NULL COMMENT '异议截止（空则同公示结束）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测周期表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_period`
--

LOCK TABLES `evaluation_period` WRITE;
/*!40000 ALTER TABLE `evaluation_period` DISABLE KEYS */;
INSERT INTO `evaluation_period` VALUES (1,'2026春季综测','2026-03-01 00:00:00','2026-07-31 23:59:59',1,0,NULL,NULL,NULL,NULL,NULL,NULL,'2026-03-26 05:53:29','2026-03-26 05:53:29'),(2,'2025-2026学年第一学期','2026-04-01 00:00:00','2026-05-01 23:59:59',1,0,'2026-04-01 00:00:00','2026-04-10 00:00:00','2026-04-15 00:00:00','2026-04-16 00:00:00','2026-04-20 00:00:00','2026-04-28 00:00:00','2026-03-27 19:27:01','2026-04-01 11:21:50'),(3,'2025-2026学年第二学期（综测-上限拆分类）','2026-02-01 00:00:00','2026-08-31 23:59:59',1,0,NULL,NULL,NULL,NULL,NULL,NULL,'2026-04-05 18:33:49','2026-04-05 18:33:49');
/*!40000 ALTER TABLE `evaluation_period` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_period_event_log`
--

DROP TABLE IF EXISTS `evaluation_period_event_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_period_event_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `period_id` bigint NOT NULL,
  `operator_user_id` int NOT NULL COMMENT '操作人用户ID',
  `event_code` varchar(64) NOT NULL COMMENT '事件编码',
  `detail` varchar(1000) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_pev_period` (`period_id`),
  KEY `idx_pev_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测周期关键操作留痕';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_period_event_log`
--

LOCK TABLES `evaluation_period_event_log` WRITE;
/*!40000 ALTER TABLE `evaluation_period_event_log` DISABLE KEYS */;
INSERT INTO `evaluation_period_event_log` VALUES (1,2,1,'PERIOD_UPDATE','更新周期配置/阶段时间','2026-04-01 11:21:50');
/*!40000 ALTER TABLE `evaluation_period_event_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_publicity`
--

DROP TABLE IF EXISTS `evaluation_publicity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_publicity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公示ID',
  `period_id` bigint NOT NULL COMMENT '综测周期ID',
  `class_id` bigint DEFAULT NULL COMMENT '班级ID（可空表示全院）',
  `start_time` datetime NOT NULL COMMENT '公示开始时间',
  `end_time` datetime NOT NULL COMMENT '公示结束时间',
  `status` varchar(32) NOT NULL DEFAULT 'OPEN' COMMENT '状态 OPEN/CLOSED',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测结果公示表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_publicity`
--

LOCK TABLES `evaluation_publicity` WRITE;
/*!40000 ALTER TABLE `evaluation_publicity` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_publicity` ENABLE KEYS */;
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
  `moral_score` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '德育分',
  `academic_score` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '学业分',
  `quality_score` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '素质能力分',
  PRIMARY KEY (`id`),
  KEY `idx_result_student_period` (`student_id`,`period_id`)
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测规则表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_rule`
--

LOCK TABLES `evaluation_rule` WRITE;
/*!40000 ALTER TABLE `evaluation_rule` DISABLE KEYS */;
INSERT INTO `evaluation_rule` VALUES (1,1,'2026春季默认规则','v1',1,'2026-03-26 05:53:29','2026-03-26 05:53:29'),(2,2,'计算机学院本科综测细则(2025)-全量','v2025.2',1,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(3,3,'计算机学院本科综测细则(2025)-按上限拆分类','v2025-cap-cat-1',1,'2026-04-05 18:33:50','2026-04-05 18:33:50');
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
  `score_mode` varchar(32) NOT NULL DEFAULT 'ADD' COMMENT '计分方式：ADD/SUB/MAX_ONLY',
  `dedupe_group` varchar(64) DEFAULT NULL COMMENT '同类去重组（同组取最高）',
  `coeff` decimal(6,3) NOT NULL DEFAULT '1.000' COMMENT '系数（如第二职务*0.5）',
  `module_code` varchar(32) DEFAULT NULL COMMENT '模块编码：MORAL/ACADEMIC/QUALITY',
  `submodule_code` varchar(32) DEFAULT NULL COMMENT '子模块编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=431 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='规则指标项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_rule_item`
--

LOCK TABLES `evaluation_rule_item` WRITE;
/*!40000 ALTER TABLE `evaluation_rule_item` DISABLE KEYS */;
INSERT INTO `evaluation_rule_item` VALUES (1,2,'德育-通报批评',1,2,'处分',0.80,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(2,2,'德育-警告',1,2,'处分',1.50,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(3,2,'德育-严重警告',1,2,'处分',2.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(4,2,'德育-记过',1,2,'处分',4.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(5,2,'德育-留校察看',1,2,'处分',6.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(6,2,'德育-课堂迟到（每次）',1,2,'考勤',0.10,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_ATTENDANCE',1.000,'MORAL','ATTENDANCE'),(7,2,'德育-课堂早退（每次）',1,2,'考勤',0.10,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_ATTENDANCE',1.000,'MORAL','ATTENDANCE'),(8,2,'德育-课堂旷课（每节）',1,2,'考勤',1.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_ATTENDANCE',1.000,'MORAL','ATTENDANCE'),(9,2,'德育-集体活动迟到（每次）',1,2,'活动',0.10,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_ACTIVITY',1.000,'MORAL','ACTIVITY'),(10,2,'德育-集体活动早退（每次）',1,2,'活动',0.10,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_ACTIVITY',1.000,'MORAL','ACTIVITY'),(11,2,'德育-集体活动缺勤（每次）',1,2,'活动',0.50,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_ACTIVITY',1.000,'MORAL','ACTIVITY'),(12,2,'德育-校园教学楼内吸烟（每次）',1,2,'行为',1.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_BEHAVIOR',1.000,'MORAL','BEHAVIOR'),(13,2,'德育-诚信问题（学术失信/恶意欠费等，每次）',1,2,'诚信',2.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_INTEGRITY',1.000,'MORAL','INTEGRITY'),(14,2,'德育-严重违规行为（基础分清零）',1,2,'一票否决',10.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','MAX_ONLY','MORAL_ZERO',1.000,'MORAL','ZERO'),(15,2,'德育荣誉-好人好事-国家级',0,2,'国家级',3.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_GOOD',1.000,'MORAL','HONOR'),(16,2,'德育荣誉-好人好事-省级',0,2,'省级',2.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_GOOD',1.000,'MORAL','HONOR'),(17,2,'德育荣誉-好人好事-市/校级',0,2,'市校级',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_GOOD',1.000,'MORAL','HONOR'),(18,2,'德育荣誉-好人好事-院级',0,2,'院级',1.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_GOOD',1.000,'MORAL','HONOR'),(19,2,'德育荣誉-通报表扬-国家级',0,2,'国家级',2.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_COMMEND',1.000,'MORAL','HONOR'),(20,2,'德育荣誉-通报表扬-省级',0,2,'省级',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_COMMEND',1.000,'MORAL','HONOR'),(21,2,'德育荣誉-通报表扬-市/校级',0,2,'市校级',1.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_COMMEND',1.000,'MORAL','HONOR'),(22,2,'德育荣誉-通报表扬-院级',0,2,'院级',0.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_COMMEND',1.000,'MORAL','HONOR'),(23,2,'学生干部-执行主席/辅导员助理',0,2,'学院',2.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(24,2,'学生干部-轮值主席/大社联主席/会长',0,2,'学院',2.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(25,2,'学生干部-部门负责人/党支部副书记/社联部门负责人',0,2,'学院',1.80,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(26,2,'学生干部-班长/团支书',0,2,'班级',2.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(27,2,'学生干部-班委/团支部委员/党支部委员/社联副部长',0,2,'班级',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(28,2,'学生干部-宿舍长',0,2,'班级',1.20,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(29,2,'学生干部-干事（考核合格）',0,2,'班级',1.20,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(30,2,'学生干部-社团干事',0,2,'班级',0.80,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(31,2,'学生干部-次高职务折算',0,2,'折算',1.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',0.500,'MORAL','CADRE'),(32,2,'学生干部-第三职务折算',0,2,'折算',1.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',0.300,'MORAL','CADRE'),(33,2,'学生干部-满意度不足50%折算',0,2,'折算',1.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',0.500,'MORAL','CADRE'),(34,2,'学业水平-平均学分绩点折算总分',0,3,'系统计算',70.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','ACADEMIC_GPA',1.000,'ACADEMIC','GPA'),(35,2,'学业水平-课程免修折算70分（规则说明项）',0,3,'规则',0.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','ACADEMIC_RULE',1.000,'ACADEMIC','RULE'),(36,2,'学业水平-等级制折算（优95/良84/中73/及62）说明项',0,3,'规则',0.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','ACADEMIC_RULE',1.000,'ACADEMIC','RULE'),(37,2,'学业水平-考试作弊/旷考/禁考/缺考按0分说明项',1,3,'规则',0.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','ACADEMIC_RULE',1.000,'ACADEMIC','RULE'),(38,2,'身心素养-国家级-一等奖',0,5,'国家级一等奖',1.00,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(39,2,'身心素养-国家级-二等奖',0,5,'国家级二等奖',0.90,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(40,2,'身心素养-国家级-三等奖',0,5,'国家级三等奖',0.80,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(41,2,'身心素养-国家级-优秀奖',0,5,'国家级优秀奖',0.70,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(42,2,'身心素养-省级-一等奖',0,5,'省级一等奖',0.80,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(43,2,'身心素养-省级-二等奖',0,5,'省级二等奖',0.70,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(44,2,'身心素养-省级-三等奖',0,5,'省级三等奖',0.50,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(45,2,'身心素养-省级-优秀奖',0,5,'省级优秀奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(46,2,'身心素养-市校级-一等奖',0,5,'市校级一等奖',0.60,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(47,2,'身心素养-市校级-二等奖',0,5,'市校级二等奖',0.50,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(48,2,'身心素养-市校级-三等奖',0,5,'市校级三等奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(49,2,'身心素养-市校级-优秀奖',0,5,'市校级优秀奖',0.20,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(50,2,'身心素养-院级-一等奖',0,5,'院级一等奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(51,2,'身心素养-院级-二等奖',0,5,'院级二等奖',0.30,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(52,2,'身心素养-院级-三等奖',0,5,'院级三等奖',0.20,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(53,2,'身心素养-院级-优秀奖',0,5,'院级优秀奖',0.10,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(69,2,'身心素养-基础减分（每次）',1,5,'基础扣分',1.50,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','QUALITY_BODYMIND_DEDUCT',1.000,'QUALITY','BODYMIND'),(70,2,'身心素养-代表学院参加大型文体活动（每项）',0,5,'活动',0.40,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_ACTIVITY',1.000,'QUALITY','BODYMIND'),(71,2,'审美人文-文化活动-国家级-一等奖',0,6,'国家级一等奖',1.00,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(72,2,'审美人文-文化活动-国家级-二等奖',0,6,'国家级二等奖',0.90,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(73,2,'审美人文-文化活动-国家级-三等奖',0,6,'国家级三等奖',0.80,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(74,2,'审美人文-文化活动-国家级-优秀奖',0,6,'国家级优秀奖',0.70,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(75,2,'审美人文-文化活动-省级-一等奖',0,6,'省级一等奖',0.80,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(76,2,'审美人文-文化活动-省级-二等奖',0,6,'省级二等奖',0.70,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(77,2,'审美人文-文化活动-省级-三等奖',0,6,'省级三等奖',0.50,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(78,2,'审美人文-文化活动-省级-优秀奖',0,6,'省级优秀奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(79,2,'审美人文-文化活动-市校级-一等奖',0,6,'市校级一等奖',0.60,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(80,2,'审美人文-文化活动-市校级-二等奖',0,6,'市校级二等奖',0.50,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(81,2,'审美人文-文化活动-市校级-三等奖',0,6,'市校级三等奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(82,2,'审美人文-文化活动-市校级-优秀奖',0,6,'市校级优秀奖',0.20,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(83,2,'审美人文-文化活动-院级-一等奖',0,6,'院级一等奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(84,2,'审美人文-文化活动-院级-二等奖',0,6,'院级二等奖',0.30,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(85,2,'审美人文-文化活动-院级-三等奖',0,6,'院级三等奖',0.20,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(86,2,'审美人文-文化活动-院级-优秀奖',0,6,'院级优秀奖',0.10,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(102,2,'审美人文-第二课堂项目负责人（每项）',0,6,'第二课堂',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_SECOND_CLASS',1.000,'QUALITY','ART'),(103,2,'审美人文-第二课堂普通成员（每项）',0,6,'第二课堂',1.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_SECOND_CLASS',1.000,'QUALITY','ART'),(104,2,'审美人文-国家/省级官网发表新闻（每篇）',0,6,'宣传报道',0.60,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(105,2,'审美人文-校外地市官方媒体/报纸/校报（每篇）',0,6,'宣传报道',0.40,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(106,2,'审美人文-校电台/电视台/校报/理工视窗等（每篇）',0,6,'宣传报道',0.20,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(107,2,'审美人文-学院网站/学院公众号发表新闻（每篇）',0,6,'宣传报道',0.10,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(108,2,'审美人文-图片/条幅制作并采用（每人）',0,6,'宣传报道',0.10,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(109,2,'审美人文-网站/视频/PPT制作并采用（每人）',0,6,'宣传报道',0.10,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(110,2,'审美人文-公寓黑板报通报表扬（每次/人）',0,6,'宣传报道',0.10,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(111,2,'审美人文-晚会主持（每次）',0,6,'文艺活动',0.30,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_ACTIVITY',1.000,'QUALITY','ACTIVITY'),(112,2,'审美人文-活动礼仪（每次）',0,6,'文艺活动',0.20,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_ACTIVITY',1.000,'QUALITY','ACTIVITY'),(113,2,'审美人文-节目出演（每次）',0,6,'文艺活动',0.30,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_ACTIVITY',1.000,'QUALITY','ACTIVITY'),(114,2,'审美人文-代表学院参加大型文体活动（每项）',0,6,'文艺活动',0.40,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_ACTIVITY',1.000,'QUALITY','ACTIVITY'),(115,2,'劳动素养-实践志愿-国家级-一等奖',0,7,'国家级一等奖',1.00,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(116,2,'劳动素养-实践志愿-国家级-二等奖',0,7,'国家级二等奖',0.90,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(117,2,'劳动素养-实践志愿-国家级-三等奖',0,7,'国家级三等奖',0.80,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(118,2,'劳动素养-实践志愿-国家级-优秀奖',0,7,'国家级优秀奖',0.70,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(119,2,'劳动素养-实践志愿-省级-一等奖',0,7,'省级一等奖',0.80,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(120,2,'劳动素养-实践志愿-省级-二等奖',0,7,'省级二等奖',0.70,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(121,2,'劳动素养-实践志愿-省级-三等奖',0,7,'省级三等奖',0.50,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(122,2,'劳动素养-实践志愿-省级-优秀奖',0,7,'省级优秀奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(123,2,'劳动素养-实践志愿-市校级-一等奖',0,7,'市校级一等奖',0.60,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(124,2,'劳动素养-实践志愿-市校级-二等奖',0,7,'市校级二等奖',0.50,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(125,2,'劳动素养-实践志愿-市校级-三等奖',0,7,'市校级三等奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(126,2,'劳动素养-实践志愿-市校级-优秀奖',0,7,'市校级优秀奖',0.20,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(127,2,'劳动素养-实践志愿-院级-一等奖',0,7,'院级一等奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(128,2,'劳动素养-实践志愿-院级-二等奖',0,7,'院级二等奖',0.30,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(129,2,'劳动素养-实践志愿-院级-三等奖',0,7,'院级三等奖',0.20,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(130,2,'劳动素养-实践志愿-院级-优秀奖',0,7,'院级优秀奖',0.10,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(146,2,'劳动素养-虐待动物（每次）',1,7,'违纪',1.50,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(147,2,'劳动素养-擅自夜不归宿/瞒报（每次）',1,7,'违纪',1.50,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(148,2,'劳动素养-私自破坏逃生窗或公物（每次）',1,7,'违纪',2.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(149,2,'劳动素养-宿舍影响集体休息（每次）',1,7,'违纪',0.50,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(150,2,'劳动素养-宿舍不按时熄灯（每次）',1,7,'违纪',0.20,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(151,2,'劳动素养-英语四级',0,7,'过级',0.40,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LANGUAGE',1.000,'QUALITY','LANGUAGE'),(152,2,'劳动素养-英语六级',0,7,'过级',0.60,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LANGUAGE',1.000,'QUALITY','LANGUAGE'),(153,2,'劳动素养-集体项目队员折算（按获奖分值0.5）',0,7,'折算',1.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',0.500,'QUALITY','LABOR'),(154,2,'创新素养-基础性评价（竞赛/科研/专利/论文/双创）',0,8,'基础',6.00,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_INNOVATION_BASE',1.000,'QUALITY','INNOVATION'),(155,2,'创新素养-发展性评价（标志性科研成果/A+竞赛）',0,8,'发展',2.00,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(156,2,'创新素养-论文发表第一作者（篇）',0,8,'论文',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_INNOVATION_PAPER',1.000,'QUALITY','PAPER'),(157,2,'创新素养-论文发表第二作者（按1/2）',0,8,'论文',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_INNOVATION_PAPER',0.500,'QUALITY','PAPER'),(158,2,'创新素养-论文发表第三作者（按1/4）',0,8,'论文',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_INNOVATION_PAPER',0.250,'QUALITY','PAPER'),(159,2,'创新素养-论文发表第四作者及以后不加分（说明项）',0,8,'论文规则',0.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_INNOVATION_PAPER',0.000,'QUALITY','PAPER'),(160,3,'德育-通报批评',1,20,'处分',0.80,0,0,1,'2026-04-05 18:33:50','2026-04-05 19:36:03','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(161,3,'德育-警告',1,20,'处分',1.50,0,0,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(162,3,'德育-严重警告',1,20,'处分',2.00,0,0,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(163,3,'德育-记过',1,20,'处分',4.00,0,0,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(164,3,'德育-留校察看',1,20,'处分',6.00,0,0,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(165,3,'德育-课堂迟到（每次）',1,20,'考勤',0.20,0,0,1,'2026-04-05 18:33:50','2026-04-05 19:34:50','SUB','MORAL_ATTENDANCE',1.000,'MORAL','ATTENDANCE'),(166,3,'德育-课堂早退（每次）',1,20,'考勤',0.20,0,0,1,'2026-04-05 18:33:50','2026-04-05 19:34:57','SUB','MORAL_ATTENDANCE',1.000,'MORAL','ATTENDANCE'),(167,3,'德育-课堂旷课（每节）',1,20,'考勤',0.50,0,0,1,'2026-04-05 18:33:50','2026-04-05 19:35:06','SUB','MORAL_ATTENDANCE',1.000,'MORAL','ATTENDANCE'),(168,3,'德育-集体活动迟到（每次）',1,20,'活动',0.10,0,0,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','SUB','MORAL_ACTIVITY',1.000,'MORAL','ACTIVITY'),(169,3,'德育-集体活动早退（每次）',1,20,'活动',0.10,0,0,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','SUB','MORAL_ACTIVITY',1.000,'MORAL','ACTIVITY'),(170,3,'德育-集体活动缺勤（每次）',1,20,'活动',0.30,0,0,1,'2026-04-05 18:33:50','2026-04-05 19:35:22','SUB','MORAL_ACTIVITY',1.000,'MORAL','ACTIVITY'),(171,3,'德育-校园教学楼内吸烟（每次）',1,20,'行为',1.00,0,0,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','SUB','MORAL_BEHAVIOR',1.000,'MORAL','BEHAVIOR'),(172,3,'德育-诚信问题（学术失信/恶意欠费等，每次）',1,20,'诚信',2.00,0,0,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','SUB','MORAL_INTEGRITY',1.000,'MORAL','INTEGRITY'),(173,3,'德育-严重违规行为（基础分清零）',1,20,'一票否决',10.00,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','MAX_ONLY','MORAL_ZERO',1.000,'MORAL','ZERO'),(174,3,'德育荣誉-好人好事-国家级',0,21,'国家级',3.00,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','MORAL_HONOR_GOOD',1.000,'MORAL','HONOR'),(175,3,'德育荣誉-好人好事-省级',0,21,'省级',2.00,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','MORAL_HONOR_GOOD',1.000,'MORAL','HONOR'),(176,3,'德育荣誉-好人好事-市/校级',0,21,'市校级',1.50,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','MORAL_HONOR_GOOD',1.000,'MORAL','HONOR'),(177,3,'德育荣誉-好人好事-院级',0,21,'院级',1.00,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','MORAL_HONOR_GOOD',1.000,'MORAL','HONOR'),(178,3,'德育荣誉-通报表扬-国家级',0,21,'国家级',2.00,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','MORAL_HONOR_COMMEND',1.000,'MORAL','HONOR'),(179,3,'德育荣誉-通报表扬-省级',0,21,'省级',1.50,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','MORAL_HONOR_COMMEND',1.000,'MORAL','HONOR'),(180,3,'德育荣誉-通报表扬-市/校级',0,21,'市校级',1.00,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','MORAL_HONOR_COMMEND',1.000,'MORAL','HONOR'),(181,3,'德育荣誉-通报表扬-院级',0,21,'院级',0.50,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','MORAL_HONOR_COMMEND',1.000,'MORAL','HONOR'),(197,3,'身心素养-基础扣分',1,24,'基础扣分',1.50,0,0,1,'2026-04-05 18:33:50','2026-04-05 19:36:53','SUB','QUALITY_BODYMIND_DEDUCT',1.000,'QUALITY','BODYMIND'),(198,3,'身心素养-国家级-一等奖',0,25,'国家级一等奖',1.00,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(199,3,'身心素养-国家级-二等奖',0,25,'国家级二等奖',0.90,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(200,3,'身心素养-国家级-三等奖',0,25,'国家级三等奖',0.80,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(201,3,'身心素养-国家级-优秀奖',0,25,'国家级优秀奖',0.70,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(202,3,'身心素养-省级-一等奖',0,25,'省级一等奖',0.80,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(203,3,'身心素养-省级-二等奖',0,25,'省级二等奖',0.70,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(204,3,'身心素养-省级-三等奖',0,25,'省级三等奖',0.50,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(205,3,'身心素养-省级-优秀奖',0,25,'省级优秀奖',0.40,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(206,3,'身心素养-市校级-一等奖',0,25,'市校级一等奖',0.60,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(207,3,'身心素养-市校级-二等奖',0,25,'市校级二等奖',0.50,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(208,3,'身心素养-市校级-三等奖',0,25,'市校级三等奖',0.40,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(209,3,'身心素养-市校级-优秀奖',0,25,'市校级优秀奖',0.20,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(210,3,'身心素养-院级-一等奖',0,25,'院级一等奖',0.40,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(211,3,'身心素养-院级-二等奖',0,25,'院级二等奖',0.30,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(212,3,'身心素养-院级-三等奖',0,25,'院级三等奖',0.20,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(213,3,'身心素养-院级-优秀奖',0,25,'院级优秀奖',0.10,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(229,3,'身心素养-校运会仪仗队',0,25,'校级',0.40,0,1,1,'2026-04-05 18:33:50','2026-04-05 19:44:55','ADD','QUALITY_BODYMIND_ACTIVITY',1.000,'QUALITY','BODYMIND'),(230,3,'审美人文-第二课堂项目负责人（每项）',0,26,'第二课堂',1.50,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_SECOND_CLASS',1.000,'QUALITY','ART'),(231,3,'审美人文-第二课堂普通成员（每项）',0,26,'第二课堂',1.00,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_SECOND_CLASS',1.000,'QUALITY','ART'),(232,3,'审美人文-文化活动-国家级-一等奖',0,27,'国家级一等奖',1.00,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(233,3,'审美人文-文化活动-国家级-二等奖',0,27,'国家级二等奖',0.90,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(234,3,'审美人文-文化活动-国家级-三等奖',0,27,'国家级三等奖',0.80,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(235,3,'审美人文-文化活动-国家级-优秀奖',0,27,'国家级优秀奖',0.70,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(236,3,'审美人文-文化活动-省级-一等奖',0,27,'省级一等奖',0.80,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(237,3,'审美人文-文化活动-省级-二等奖',0,27,'省级二等奖',0.70,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(238,3,'审美人文-文化活动-省级-三等奖',0,27,'省级三等奖',0.50,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(239,3,'审美人文-文化活动-省级-优秀奖',0,27,'省级优秀奖',0.40,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(240,3,'审美人文-文化活动-市校级-一等奖',0,27,'市校级一等奖',0.60,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(241,3,'审美人文-文化活动-市校级-二等奖',0,27,'市校级二等奖',0.50,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(242,3,'审美人文-文化活动-市校级-三等奖',0,27,'市校级三等奖',0.40,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(243,3,'审美人文-文化活动-市校级-优秀奖',0,27,'市校级优秀奖',0.20,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(244,3,'审美人文-文化活动-院级-一等奖',0,27,'院级一等奖',0.40,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(245,3,'审美人文-文化活动-院级-二等奖',0,27,'院级二等奖',0.30,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(246,3,'审美人文-文化活动-院级-三等奖',0,27,'院级三等奖',0.20,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(247,3,'审美人文-文化活动-院级-优秀奖',0,27,'院级优秀奖',0.10,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(263,3,'审美人文-国家/省级官网发表新闻（每篇）',0,27,'宣传报道',0.60,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(264,3,'审美人文-校外地市官方媒体/报纸/校报（每篇）',0,27,'宣传报道',0.40,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(265,3,'审美人文-校电台/电视台/校报/理工视窗等（每篇）',0,27,'宣传报道',0.20,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(266,3,'审美人文-学院网站/学院公众号发表新闻（每篇）',0,27,'宣传报道',0.10,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(267,3,'审美人文-图片/条幅制作并采用（每人）',0,27,'宣传报道',0.10,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(268,3,'审美人文-网站/视频/PPT制作并采用（每人）',0,27,'宣传报道',0.10,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(269,3,'审美人文-公寓黑板报通报表扬（每次/人）',0,27,'宣传报道',0.10,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(270,3,'审美人文-晚会主持（每次）',0,27,'文艺活动',0.30,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_ACTIVITY',1.000,'QUALITY','ACTIVITY'),(271,3,'审美人文-活动礼仪（每次）',0,27,'文艺活动',0.20,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_ACTIVITY',1.000,'QUALITY','ACTIVITY'),(272,3,'审美人文-节目出演（每次）',0,27,'文艺活动',0.30,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_ACTIVITY',1.000,'QUALITY','ACTIVITY'),(273,3,'审美人文-代表学院参加大型文体活动（每项）',0,27,'文艺活动',0.40,0,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_ART_ACTIVITY',1.000,'QUALITY','ACTIVITY'),(274,3,'劳动素养-基础扣分',1,28,'违纪',1.50,0,0,1,'2026-04-05 18:33:50','2026-04-05 19:52:04','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(275,3,'劳动素养-擅自夜不归宿/瞒报（每次）',1,28,'违纪',1.50,0,0,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(276,3,'劳动素养-私自破坏逃生窗或公物（每次）',1,28,'违纪',2.00,0,0,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(277,3,'劳动素养-宿舍影响集体休息（每次）',1,28,'违纪',0.50,0,0,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(278,3,'劳动素养-宿舍不按时熄灯（每次）',1,28,'违纪',0.20,0,0,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(279,3,'劳动素养-实践志愿-国家级-一等奖',0,29,'国家级一等奖',1.00,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(280,3,'劳动素养-实践志愿-国家级-二等奖',0,29,'国家级二等奖',0.90,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(281,3,'劳动素养-实践志愿-国家级-三等奖',0,29,'国家级三等奖',0.80,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(282,3,'劳动素养-实践志愿-国家级-优秀奖',0,29,'国家级优秀奖',0.70,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(283,3,'劳动素养-实践志愿-省级-一等奖',0,29,'省级一等奖',0.80,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(284,3,'劳动素养-实践志愿-省级-二等奖',0,29,'省级二等奖',0.70,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(285,3,'劳动素养-实践志愿-省级-三等奖',0,29,'省级三等奖',0.50,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(286,3,'劳动素养-实践志愿-省级-优秀奖',0,29,'省级优秀奖',0.40,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(287,3,'劳动素养-实践志愿-市校级-一等奖',0,29,'市校级一等奖',0.60,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(288,3,'劳动素养-实践志愿-市校级-二等奖',0,29,'市校级二等奖',0.50,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(289,3,'劳动素养-实践志愿-市校级-三等奖',0,29,'市校级三等奖',0.40,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(290,3,'劳动素养-实践志愿-市校级-优秀奖',0,29,'市校级优秀奖',0.20,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(291,3,'劳动素养-实践志愿-院级-一等奖',0,29,'院级一等奖',0.40,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(292,3,'劳动素养-实践志愿-院级-二等奖',0,29,'院级二等奖',0.30,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(293,3,'劳动素养-实践志愿-院级-三等奖',0,29,'院级三等奖',0.20,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(294,3,'劳动素养-实践志愿-院级-优秀奖',0,29,'院级优秀奖',0.10,1,1,1,'2026-04-05 18:33:50','2026-04-05 18:33:50','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(310,3,'竞赛加分-四级过级',0,33,'过级',0.40,0,1,1,'2026-04-05 18:33:50','2026-04-05 20:57:41','ADD','QUALITY_LANGUAGE',1.000,'QUALITY','LANGUAGE'),(311,3,'竞赛加分-六级过级',0,33,'过级',0.60,0,1,1,'2026-04-05 18:33:50','2026-04-05 20:57:56','ADD','QUALITY_LANGUAGE',1.000,'QUALITY','LANGUAGE'),(319,3,'身心素养-校运会健美操',0,25,'校级',0.40,0,1,1,'2026-04-05 19:39:31','2026-04-05 19:39:31','ADD',NULL,1.000,'QUALITY',NULL),(320,3,'身心素养-运动会观众',0,25,'校级',0.10,0,1,1,'2026-04-05 19:42:48','2026-04-05 19:42:48','ADD',NULL,1.000,'QUALITY',NULL),(321,3,'身心素养-积极参与学院运动训练',0,25,'校级',0.40,0,1,1,'2026-04-05 19:43:18','2026-04-05 19:43:18','ADD',NULL,1.000,'QUALITY',NULL),(322,3,'身心素养-参加校运会、12.9万米接力、校篮球、足球、 排球等学校体育赛事',0,25,'校级',0.40,0,1,1,'2026-04-05 19:44:26','2026-04-05 19:44:26','ADD',NULL,1.000,'QUALITY',NULL),(323,3,'身心素养-关注同学者（根据具体加分多次选择）',0,25,'校级',0.10,0,1,1,'2026-04-05 19:45:46','2026-04-05 19:45:46','ADD',NULL,1.000,'QUALITY',NULL),(324,3,'劳动素养-宿舍卫生成绩低于90分',1,28,'校级',0.20,0,1,1,'2026-04-05 19:52:52','2026-04-05 19:52:52','SUB',NULL,1.000,'QUALITY',NULL),(327,3,'其他加分',0,20,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:14:07','ADD',NULL,1.000,'MORAL','MISC'),(328,3,'其他加分',0,21,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','ADD',NULL,1.000,'MORAL','MISC'),(331,3,'其他加分',0,24,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','ADD',NULL,1.000,'QUALITY','BODYMIND'),(332,3,'其他加分',0,25,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','ADD',NULL,1.000,'QUALITY','BODYMIND'),(333,3,'其他加分',0,26,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','ADD',NULL,1.000,'QUALITY','ART'),(334,3,'其他加分',0,27,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','ADD',NULL,1.000,'QUALITY','ART'),(335,3,'其他加分',0,28,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','ADD',NULL,1.000,'QUALITY','LABOR'),(336,3,'其他加分',0,29,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','ADD',NULL,1.000,'QUALITY','LABOR'),(338,3,'其他加分',0,31,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','ADD',NULL,1.000,'QUALITY','INNOVATION'),(342,3,'其他减分',1,20,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','SUB',NULL,1.000,'MORAL','MISC'),(343,3,'其他减分',1,21,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','SUB',NULL,1.000,'MORAL','MISC'),(346,3,'其他减分',1,24,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','SUB',NULL,1.000,'QUALITY','BODYMIND'),(347,3,'其他减分',1,25,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','SUB',NULL,1.000,'QUALITY','BODYMIND'),(348,3,'其他减分',1,26,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','SUB',NULL,1.000,'QUALITY','ART'),(349,3,'其他减分',1,27,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','SUB',NULL,1.000,'QUALITY','ART'),(350,3,'其他减分',1,28,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','SUB',NULL,1.000,'QUALITY','LABOR'),(351,3,'其他减分',1,29,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','SUB',NULL,1.000,'QUALITY','LABOR'),(353,3,'其他减分',1,31,'其他',0.10,0,0,1,'2026-04-05 20:13:01','2026-04-05 20:15:17','SUB',NULL,1.000,'QUALITY','INNOVATION'),(355,3,'劳动素养-宿舍加分',0,29,'校级',0.10,0,1,1,'2026-04-05 20:39:54','2026-04-05 20:39:54','ADD',NULL,1.000,'QUALITY',NULL),(356,3,'劳动素养-志愿服务加分',0,29,'校级',0.10,0,1,1,'2026-04-05 20:40:15','2026-04-05 20:40:15','ADD',NULL,1.000,'QUALITY',NULL),(357,3,'创新实践-积极参加对外国际交流活动',0,32,'校级',4.00,0,1,1,'2026-04-05 20:44:58','2026-04-05 20:46:50','ADD',NULL,1.000,'QUALITY',NULL),(358,3,'创新实践-参加学校认定的省级及以上创新创业竞赛活动',0,32,'校级',3.00,0,1,1,'2026-04-05 20:45:37','2026-04-05 20:46:35','ADD',NULL,1.000,'QUALITY',NULL),(359,3,'创新实践-积极申报省级及以上创新创业项目',0,32,'校级',4.00,0,1,1,'2026-04-05 20:46:13','2026-04-05 20:46:41','ADD',NULL,1.000,'QUALITY',NULL),(360,3,'创新实践-积极参加校级创新创业竞赛',0,32,'校级',2.50,0,1,1,'2026-04-05 20:47:36','2026-04-05 20:47:36','ADD',NULL,1.000,'QUALITY',NULL),(361,3,'创新实践-积极参加各类技能培训认证和国内对外交流活动',0,32,'校级',1.50,0,1,1,'2026-04-05 20:48:15','2026-04-05 20:48:15','ADD',NULL,1.000,'QUALITY',NULL),(362,3,'创新实践-积极参加大学生四、六级考试、研究生考试',0,32,'校级',1.50,0,1,1,'2026-04-05 20:48:54','2026-04-05 20:48:54','ADD',NULL,1.000,'QUALITY',NULL),(363,3,'竞赛加分-国家级-一等奖',0,33,'国家级',2.00,1,1,1,'2026-04-05 20:51:28','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(364,3,'竞赛加分-国家级-二等奖',0,33,'国家级',1.80,1,1,1,'2026-04-05 20:51:54','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(365,3,'竞赛加分-国家级-三等奖',0,33,'校级',1.50,1,1,1,'2026-04-05 20:52:11','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(366,3,'竞赛加分-国家级-优秀奖',0,33,'国家级',1.20,1,1,1,'2026-04-05 20:52:33','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(367,3,'竞赛加分-省级-一等奖',0,33,'省级',1.50,1,1,1,'2026-04-05 20:52:54','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(368,3,'竞赛加分-省级-二等奖',0,33,'省级',1.20,1,1,1,'2026-04-05 20:53:13','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(369,3,'竞赛加分-省级-三等奖',0,33,'省级',1.00,1,1,1,'2026-04-05 20:53:33','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(370,3,'竞赛加分-省级-优秀奖',0,33,'省级',0.80,1,1,1,'2026-04-05 20:53:51','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(371,3,'竞赛加分-市校级-一等奖',0,33,'校级',1.00,1,1,1,'2026-04-05 20:54:13','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(372,3,'竞赛加分-市校级-二等奖',0,33,'校级',0.80,1,1,1,'2026-04-05 20:54:31','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(373,3,'竞赛加分-市校级-三等奖',0,33,'校级',0.60,1,1,1,'2026-04-05 20:54:49','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(374,3,'竞赛加分-市校级-优秀奖',0,33,'校级',0.40,1,1,1,'2026-04-05 20:55:09','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(375,3,'竞赛加分-院级-一等奖',0,33,'院级',0.60,1,1,1,'2026-04-05 20:55:31','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(376,3,'竞赛加分-院级-二等奖',0,33,'院级',0.40,1,1,1,'2026-04-05 20:55:56','2026-04-05 21:19:43','ADD',NULL,1.000,'QUALITY',NULL),(377,3,'竞赛加分-院级-三等奖',0,33,'院级',0.30,1,1,1,'2026-04-05 20:56:15','2026-04-05 21:19:02','ADD',NULL,1.000,'QUALITY',NULL),(378,3,'竞赛加分-院级-优秀奖',0,33,'院级',0.20,1,1,1,'2026-04-05 20:56:37','2026-04-05 21:18:59','ADD',NULL,1.000,'QUALITY',NULL),(379,3,'A+竞赛-国家级-一等奖',0,31,'国家级一等奖',2.00,1,1,1,'2026-04-05 21:02:40','2026-04-05 21:05:45','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(380,3,'A+竞赛-国家级-二等奖',0,31,'国家级二等奖',1.80,1,1,1,'2026-04-05 21:02:40','2026-04-05 21:05:54','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(381,3,'A+竞赛-国家级-三等奖',0,31,'国家级三等奖',1.50,1,1,1,'2026-04-05 21:02:40','2026-04-05 21:06:00','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(382,3,'A+竞赛-国家级-优秀奖',0,31,'国家级优秀奖',1.20,1,1,1,'2026-04-05 21:02:40','2026-04-05 21:06:06','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(383,3,'A+竞赛-省级-一等奖',0,31,'省级一等奖',1.50,1,1,1,'2026-04-05 21:02:40','2026-04-05 21:06:16','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(384,3,'A+竞赛-省级-二等奖',0,31,'省级二等奖',1.20,1,1,1,'2026-04-05 21:02:40','2026-04-05 21:06:22','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(385,3,'A+竞赛-省级-三等奖',0,31,'省级三等奖',1.00,1,1,1,'2026-04-05 21:02:40','2026-04-05 21:06:31','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(386,3,'A+竞赛-省级-优秀奖',0,31,'省级优秀奖',0.80,1,1,1,'2026-04-05 21:02:40','2026-04-05 21:06:38','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(387,3,'A+竞赛-市校级-一等奖',0,31,'市校级一等奖',1.00,1,1,1,'2026-04-05 21:02:40','2026-04-05 21:06:49','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(388,3,'A+竞赛-市校级-二等奖',0,31,'市校级二等奖',0.80,1,1,1,'2026-04-05 21:02:40','2026-04-05 21:06:53','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(389,3,'A+竞赛-市校级-三等奖',0,31,'市校级三等奖',0.60,1,1,1,'2026-04-05 21:02:40','2026-04-05 21:06:57','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(390,3,'A+竞赛-市校级-优秀奖',0,31,'市校级优秀奖',0.40,1,1,1,'2026-04-05 21:02:40','2026-04-05 21:07:05','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(410,3,'论文专利-北大核心期刊',0,31,'论文',1.50,0,1,1,'2026-04-05 21:12:15','2026-04-05 21:14:46','ADD','QUALITY_INNOVATION_PAPER',1.000,'QUALITY','PAPER'),(413,3,'论文专利-SCI收录期刊论文',0,31,'论文',1.50,0,1,1,'2026-04-05 21:12:15','2026-04-05 21:14:41','ADD','QUALITY_INNOVATION_PAPER',1.000,'QUALITY','PAPER'),(416,3,'论文专利-EI期刊论文（与专业密切相关）',0,31,'论文',1.50,0,1,1,'2026-04-05 21:12:15','2026-04-05 21:14:35','ADD','QUALITY_INNOVATION_PAPER',1.000,'QUALITY','PAPER'),(419,3,'论文专利-CCF推荐A/B/C类会议论文',0,31,'论文',1.50,0,1,1,'2026-04-05 21:12:15','2026-04-05 21:14:27','ADD','QUALITY_INNOVATION_PAPER',1.000,'QUALITY','PAPER'),(422,3,'论文专利-EI国际会议论文（作报告）',0,31,'论文',1.50,0,1,1,'2026-04-05 21:12:15','2026-04-05 21:14:21','ADD','QUALITY_INNOVATION_PAPER',1.000,'QUALITY','PAPER'),(426,3,'竞赛论文-知网CNKI检索期刊论文',0,31,'校级',1.00,0,1,1,'2026-04-05 21:15:41','2026-04-05 21:15:41','ADD',NULL,1.000,'QUALITY',NULL),(427,3,'竞赛论文-出版专著',0,31,'校级',1.50,0,1,1,'2026-04-05 21:16:08','2026-04-05 21:16:08','ADD',NULL,1.000,'QUALITY',NULL),(428,3,'论文专利-软件著作权',0,31,'校级',0.80,0,1,1,'2026-04-05 21:16:38','2026-04-05 21:16:38','ADD',NULL,1.000,'QUALITY',NULL),(429,3,'论文专利-外观设计专利、实用新型专利',0,31,'校级',1.00,0,1,1,'2026-04-05 21:17:16','2026-04-05 21:17:16','ADD',NULL,1.000,'QUALITY',NULL),(430,3,'论文专利-发明专利',0,31,'校级',1.50,0,1,1,'2026-04-05 21:17:32','2026-04-05 21:17:32','ADD',NULL,1.000,'QUALITY',NULL);
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
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级分类id',
  `score_cap` decimal(10,2) DEFAULT NULL COMMENT '该分类子树内加分合计上限，NULL 不限制',
  `student_visible` tinyint NOT NULL DEFAULT '1' COMMENT '1=学生端展示；0=隐藏（子树一并隐藏）',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '同级排序，越小越靠前',
  `category_base_score` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '分类基础分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='规则分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_rule_item_category`
--

LOCK TABLES `evaluation_rule_item_category` WRITE;
/*!40000 ALTER TABLE `evaluation_rule_item_category` DISABLE KEYS */;
INSERT INTO `evaluation_rule_item_category` VALUES (2,2,'德育评价',0,NULL,1,0,0.00,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(3,2,'学业水平评价',0,NULL,1,0,0.00,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(4,2,'素质能力评价',0,NULL,1,0,0.00,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(5,2,'身心素养',4,NULL,1,0,0.00,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(6,2,'审美和人文素养',4,NULL,1,0,0.00,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(7,2,'劳动素养',4,NULL,1,0,0.00,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(8,2,'创新素养',4,NULL,1,0,0.00,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(9,1,'德育评价',0,NULL,1,0,0.00,'2026-04-03 21:38:51','2026-04-03 21:38:51'),(10,1,'学业水平评价',0,NULL,1,0,0.00,'2026-04-03 21:38:51','2026-04-03 21:38:51'),(11,1,'素质能力评价',0,NULL,1,0,0.00,'2026-04-03 21:38:51','2026-04-03 21:38:51'),(12,1,'身心素养',11,NULL,1,0,0.00,'2026-04-03 21:38:51','2026-04-03 21:38:51'),(13,1,'审美和人文素养',11,NULL,1,0,0.00,'2026-04-03 21:38:51','2026-04-03 21:38:51'),(14,1,'劳动素养',11,NULL,1,0,0.00,'2026-04-03 21:38:51','2026-04-03 21:38:51'),(15,1,'创新素养',11,NULL,1,0,0.00,'2026-04-03 21:38:51','2026-04-03 21:38:51'),(16,2,'任职',2,NULL,1,0,0.00,'2026-04-05 17:31:43','2026-04-05 17:35:17'),(17,1,'任职',9,NULL,1,0,0.00,'2026-04-05 17:33:09','2026-04-05 17:35:11'),(18,3,'任职',19,NULL,1,0,0.00,'2026-04-05 18:33:50','2026-04-05 18:40:53'),(19,3,'德育评价',0,10.00,1,0,0.00,'2026-04-05 18:33:50','2026-04-05 18:45:23'),(20,3,'德育-基础分',19,NULL,1,0,7.00,'2026-04-05 18:33:50','2026-04-05 18:52:22'),(21,3,'德育-奖励分',19,3.00,1,10,0.00,'2026-04-05 18:33:50','2026-04-05 18:51:50'),(22,3,'学业水平评价',0,70.00,1,10,0.00,'2026-04-05 18:33:50','2026-04-05 18:33:50'),(23,3,'素质能力评价',0,20.00,1,20,0.00,'2026-04-05 18:33:50','2026-04-05 18:46:31'),(24,3,'身心素养-基础',23,3.00,1,0,3.00,'2026-04-05 18:33:50','2026-04-05 18:47:36'),(25,3,'身心素养-奖励分',23,1.00,1,10,0.00,'2026-04-05 18:33:50','2026-04-05 18:47:44'),(26,3,'审美和人文素养-基础分',23,3.00,1,20,3.00,'2026-04-05 18:33:50','2026-04-05 18:52:32'),(27,3,'审美和人文素养-奖励分',23,1.00,1,30,0.00,'2026-04-05 18:33:50','2026-04-05 18:48:30'),(28,3,'劳动素养-基础分',23,3.00,1,40,3.00,'2026-04-05 18:33:50','2026-04-05 18:52:38'),(29,3,'劳动素养-奖励分',23,1.00,1,50,0.00,'2026-04-05 18:33:50','2026-04-05 18:49:17'),(30,3,'创新素养-基础性评价',23,6.00,1,60,0.00,'2026-04-05 18:33:50','2026-04-05 18:50:04'),(31,3,'创新素养-发展性评价',23,2.00,1,70,0.00,'2026-04-05 18:33:50','2026-04-05 18:50:18'),(32,3,'创新素养-创始实践能力加分',30,4.00,1,0,0.00,'2026-04-05 20:43:03','2026-04-05 20:49:23'),(33,3,'创新素养-基础竞赛加分',30,2.00,1,5,0.00,'2026-04-05 20:43:24','2026-04-05 20:49:34');
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
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='规则项限制表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_rule_item_limit`
--

LOCK TABLES `evaluation_rule_item_limit` WRITE;
/*!40000 ALTER TABLE `evaluation_rule_item_limit` DISABLE KEYS */;
INSERT INTO `evaluation_rule_item_limit` VALUES (1,15,3.00,NULL,'2026-03-27 19:27:01'),(2,16,3.00,NULL,'2026-03-27 19:27:01'),(3,17,3.00,NULL,'2026-03-27 19:27:01'),(4,18,3.00,NULL,'2026-03-27 19:27:01'),(5,19,3.00,NULL,'2026-03-27 19:27:01'),(6,20,3.00,NULL,'2026-03-27 19:27:01'),(7,21,3.00,NULL,'2026-03-27 19:27:01'),(8,22,3.00,NULL,'2026-03-27 19:27:01'),(16,38,1.00,NULL,'2026-03-27 19:27:01'),(17,39,1.00,NULL,'2026-03-27 19:27:01'),(18,40,1.00,NULL,'2026-03-27 19:27:01'),(19,41,1.00,NULL,'2026-03-27 19:27:01'),(20,42,1.00,NULL,'2026-03-27 19:27:01'),(21,43,1.00,NULL,'2026-03-27 19:27:01'),(22,44,1.00,NULL,'2026-03-27 19:27:01'),(23,45,1.00,NULL,'2026-03-27 19:27:01'),(24,46,1.00,NULL,'2026-03-27 19:27:01'),(25,47,1.00,NULL,'2026-03-27 19:27:01'),(26,48,1.00,NULL,'2026-03-27 19:27:01'),(27,49,1.00,NULL,'2026-03-27 19:27:01'),(28,50,1.00,NULL,'2026-03-27 19:27:01'),(29,51,1.00,NULL,'2026-03-27 19:27:01'),(30,52,1.00,NULL,'2026-03-27 19:27:01'),(31,53,1.00,NULL,'2026-03-27 19:27:01'),(32,71,1.00,NULL,'2026-03-27 19:27:01'),(33,72,1.00,NULL,'2026-03-27 19:27:01'),(34,73,1.00,NULL,'2026-03-27 19:27:01'),(35,74,1.00,NULL,'2026-03-27 19:27:01'),(36,75,1.00,NULL,'2026-03-27 19:27:01'),(37,76,1.00,NULL,'2026-03-27 19:27:01'),(38,77,1.00,NULL,'2026-03-27 19:27:01'),(39,78,1.00,NULL,'2026-03-27 19:27:01'),(40,79,1.00,NULL,'2026-03-27 19:27:01'),(41,80,1.00,NULL,'2026-03-27 19:27:01'),(42,81,1.00,NULL,'2026-03-27 19:27:01'),(43,82,1.00,NULL,'2026-03-27 19:27:01'),(44,83,1.00,NULL,'2026-03-27 19:27:01'),(45,84,1.00,NULL,'2026-03-27 19:27:01'),(46,85,1.00,NULL,'2026-03-27 19:27:01'),(47,86,1.00,NULL,'2026-03-27 19:27:01'),(48,115,1.00,NULL,'2026-03-27 19:27:01'),(49,116,1.00,NULL,'2026-03-27 19:27:01'),(50,117,1.00,NULL,'2026-03-27 19:27:01'),(51,118,1.00,NULL,'2026-03-27 19:27:01'),(52,119,1.00,NULL,'2026-03-27 19:27:01'),(53,120,1.00,NULL,'2026-03-27 19:27:01'),(54,121,1.00,NULL,'2026-03-27 19:27:01'),(55,122,1.00,NULL,'2026-03-27 19:27:01'),(56,123,1.00,NULL,'2026-03-27 19:27:01'),(57,124,1.00,NULL,'2026-03-27 19:27:01'),(58,125,1.00,NULL,'2026-03-27 19:27:01'),(59,126,1.00,NULL,'2026-03-27 19:27:01'),(60,127,1.00,NULL,'2026-03-27 19:27:01'),(61,128,1.00,NULL,'2026-03-27 19:27:01'),(62,129,1.00,NULL,'2026-03-27 19:27:01'),(63,130,1.00,NULL,'2026-03-27 19:27:01'),(79,155,2.00,NULL,'2026-03-27 19:27:01');
/*!40000 ALTER TABLE `evaluation_rule_item_limit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_student_academic_score`
--

DROP TABLE IF EXISTS `evaluation_student_academic_score`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_student_academic_score` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `period_id` bigint NOT NULL COMMENT '综测周期ID',
  `student_no` varchar(64) NOT NULL COMMENT '学号',
  `class_name` varchar(100) NOT NULL COMMENT '班级',
  `student_name` varchar(64) NOT NULL COMMENT '姓名',
  `intellectual_score` decimal(18,8) NOT NULL COMMENT '智育分（高精度小数）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_period_student_no` (`period_id`,`student_no`),
  KEY `idx_period_id` (`period_id`),
  KEY `idx_student_no` (`student_no`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生学业水平智育成绩表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_student_academic_score`
--

LOCK TABLES `evaluation_student_academic_score` WRITE;
/*!40000 ALTER TABLE `evaluation_student_academic_score` DISABLE KEYS */;
INSERT INTO `evaluation_student_academic_score` VALUES (1,2,'23110506122','计算机科学与技术1班','sjk',90.00000000,'2026-04-01 22:25:56','2026-04-01 22:25:56'),(2,3,'23110506122','计算机科学与技术1班','sjk',90.00000000,'2026-04-05 19:25:20','2026-04-05 19:25:20');
/*!40000 ALTER TABLE `evaluation_student_academic_score` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_student_period_confirm`
--

DROP TABLE IF EXISTS `evaluation_student_period_confirm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_student_period_confirm` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_user_id` bigint NOT NULL,
  `period_id` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_period` (`student_user_id`,`period_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生确认无异议（锁定不可再申报）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_student_period_confirm`
--

LOCK TABLES `evaluation_student_period_confirm` WRITE;
/*!40000 ALTER TABLE `evaluation_student_period_confirm` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_student_period_confirm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_student_submit_tip`
--

DROP TABLE IF EXISTS `evaluation_student_submit_tip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_student_submit_tip` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `period_id` bigint NOT NULL COMMENT '综测周期ID',
  `section_code` varchar(64) NOT NULL COMMENT 'moral/academic/quality_bodymind/quality_art/quality_labor/quality_innovation',
  `title` varchar(200) NOT NULL COMMENT '提示标题',
  `content` varchar(4000) NOT NULL COMMENT '提示正文',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '同分区内排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '1=启用 0=停用',
  `operator_user_id` int DEFAULT NULL COMMENT '最后操作人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tip_period_section` (`period_id`,`section_code`),
  KEY `idx_tip_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生综测申报动态提示';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_student_submit_tip`
--

LOCK TABLES `evaluation_student_submit_tip` WRITE;
/*!40000 ALTER TABLE `evaluation_student_submit_tip` DISABLE KEYS */;
INSERT INTO `evaluation_student_submit_tip` VALUES (1,1,'moral','德育总公式','德育成绩=基础分10-减分项+奖励分，奖励分上限3分；同一事项适用多标准时按最高分，不重复加分。',10,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(2,1,'moral','德育减分重点','处分、旷课/迟到早退、集体活动缺勤、教学楼吸烟、学术失信和恶意欠费等均会扣分；严重违纪情形可能导致基础分为0。',20,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(3,1,'moral','德育奖励重点','好人好事、通报表扬、学生干部任职可加分；多职务按“最高+次高×0.5+第三×0.3”，第四及以后不加分。',30,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(4,1,'academic','学业成绩口径','学业水平评价按课程平均学分绩点口径计算，重修/通识选修/辅修课程一般不计入该口径。',10,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(5,1,'academic','特殊成绩折算','免修按70分；等级制折算：优95、良84、中73、及格62、不及格0；两级制折算：合格70、不合格0。',20,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(6,1,'academic','学术诚信提醒','考试作弊、旷考、禁考、缺考按0分计入。请以教务系统与学院最终认定为准。',30,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(7,1,'quality_bodymind','身心素养公式','身心素养=基础分3-减分项+奖励分（奖励项满分1）。无故缺席心理普查、阳光体育、军训任务等会扣分。',10,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(8,1,'quality_bodymind','文体竞赛加分','文体竞赛按国家/省/市校/院级与奖项等级加分；名次折算按细则执行。建议上传证书、发文、名单公示等材料。',20,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(9,1,'quality_bodymind','不重复加分规则','同一类别、关联性成果一般按最高分认定，不重复累计。',30,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(10,1,'quality_art','审美人文基础说明','该模块以文化活动、宣传报道、文艺活动等为主，基础性评价可参考第二课堂记录。',10,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(11,1,'quality_art','宣传报道与作品加分','宣传稿件、图片/条幅、视频/PPT、公寓黑板报、主持礼仪和节目出演均可按细则加分。请写清平台、时间、角色。',20,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(12,1,'quality_labor','劳动素养减分重点','夜不归宿、损坏公物、影响休息、不按时熄灯等会扣分；按细则和学校管理规定执行。',10,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(13,1,'quality_labor','实践志愿与过级加分','社会实践/志愿服务按级别和奖项加分；英语四级0.4、六级0.6（按证书获得学期计分）。',20,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(14,1,'quality_labor','集体项目折算','集体项目获奖时，队员按对应奖项分值的0.5折算；同一作品同一比赛按最高奖项，不重复。',30,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(15,1,'quality_innovation','创新素养结构','创新素养分基础性评价（6分）与发展性评价（2分），重点看竞赛、科研、专利、论文、双创训练等。',10,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(16,1,'quality_innovation','论文加分规则','论文加分：第一作者1.5分/篇，第二作者按1/2，第三作者按1/4，第四及以后不加分；须有正式刊号且与专业相关。',20,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(17,1,'quality_innovation','材料提交建议','建议上传：证书、论文首页与检索页、专利授权页、立项/结题证明、官方公示链接。',30,1,NULL,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(18,3,'18','任职｜综测提示','请按实际任职情况填写申报分值，并上传任职证明、考核或公示材料，最终以学院审核认定为准。',1001,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(19,3,'19','德育评价｜综测提示','本项为目录：请在子类「德育基础与违纪减分」「德育奖励分（细则合计上限3分）」中选择对应细则分别申报。',1002,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(20,3,'20','德育-基础分｜综测提示','请根据《综测细则》在本分类选择对应细则申报，材料须真实清晰，最终以学院审核认定为准。（德育-基础分）',1003,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(21,3,'24','身心素养-基础｜综测提示','请根据《综测细则》在本分类选择对应细则申报，材料须真实清晰，最终以学院审核认定为准。（身心素养-基础）',1004,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(22,3,'32','创新素养-创始实践能力加分｜综测提示','请根据《综测细则》在本分类选择对应细则申报，材料须真实清晰，最终以学院审核认定为准。（创新素养-创始实践能力加分）',1005,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(23,3,'33','创新素养-基础竞赛加分｜综测提示','请根据《综测细则》在本分类选择对应细则申报，材料须真实清晰，最终以学院审核认定为准。（创新素养-基础竞赛加分）',1006,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(24,3,'21','德育-奖励分｜综测提示','请根据《综测细则》在本分类选择对应细则申报，材料须真实清晰，最终以学院审核认定为准。（德育-奖励分）',1007,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(25,3,'22','学业水平评价｜综测提示','按平均学分绩点折算总分（本类上限70分）；免修与等级制折算按细则执行，考试作弊、旷考、禁考、缺考按0分口径，以教务与学院认定为准。',1008,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(26,3,'25','身心素养-奖励分｜综测提示','请根据《综测细则》在本分类选择对应细则申报，材料须真实清晰，最终以学院审核认定为准。（身心素养-奖励分）',1009,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(27,3,'23','素质能力评价｜综测提示','本项为目录：请在身心、审美人文、劳动、创新等子类中分别选择细则完成申报。',1010,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(28,3,'26','审美和人文素养-基础分｜综测提示','请根据《综测细则》在本分类选择对应细则申报，材料须真实清晰，最终以学院审核认定为准。（审美和人文素养-基础分）',1011,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(29,3,'27','审美和人文素养-奖励分｜综测提示','请根据《综测细则》在本分类选择对应细则申报，材料须真实清晰，最终以学院审核认定为准。（审美和人文素养-奖励分）',1012,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(30,3,'28','劳动素养-基础分｜综测提示','请根据《综测细则》在本分类选择对应细则申报，材料须真实清晰，最终以学院审核认定为准。（劳动素养-基础分）',1013,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(31,3,'29','劳动素养-奖励分｜综测提示','请根据《综测细则》在本分类选择对应细则申报，材料须真实清晰，最终以学院审核认定为准。（劳动素养-奖励分）',1014,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(32,3,'30','创新素养-基础性评价｜综测提示','请根据《综测细则》在本分类选择对应细则申报，材料须真实清晰，最终以学院审核认定为准。（创新素养-基础性评价）',1015,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53'),(33,3,'31','创新素养-发展性评价｜综测提示','请根据《综测细则》在本分类选择对应细则申报，材料须真实清晰，最终以学院审核认定为准。（创新素养-发展性评价）',1016,1,NULL,'2026-04-06 11:19:53','2026-04-06 11:19:53');
/*!40000 ALTER TABLE `evaluation_student_submit_tip` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='班级表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_class`
--

LOCK TABLES `sys_class` WRITE;
/*!40000 ALTER TABLE `sys_class` DISABLE KEYS */;
INSERT INTO `sys_class` VALUES (1,'计算机科学与技术1班',1,2022,'2026-03-18 21:57:04','2026-03-26 13:13:50'),(2,'计算机科学与技术2班',1,2023,'2026-03-18 21:57:04','2026-03-18 21:57:04'),(3,'电子信息工程1班',2,2023,'2026-03-18 21:57:04','2026-03-18 21:57:04'),(4,'电子信息工程2班',2,2023,'2026-03-18 21:57:04','2026-03-18 21:57:04'),(5,'工商管理1班',3,2023,'2026-03-18 21:57:04','2026-03-18 21:57:04'),(6,'汉语言文学1班',4,2023,'2026-03-18 21:57:04','2026-03-18 21:57:04'),(7,'英语1班',5,2023,'2026-03-18 21:57:04','2026-03-18 21:57:04'),(8,'软件2304',1,2023,'2026-03-26 20:38:40','2026-03-26 20:38:40'),(9,'机械一班',10,2026,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(10,'绘画二班',17,2026,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(11,'建筑设计一班',11,2026,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(12,'汉语言三班',15,2026,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(13,'英语一班',12,2026,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(14,'临床一班',9,2026,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(15,'生物二班',8,2026,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(16,'计算机一班',16,2026,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(17,'法学二班',13,2026,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(18,'市场营销一班',14,2026,'2026-03-27 09:13:55','2026-03-27 09:13:55');
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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学院表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_college`
--

LOCK TABLES `sys_college` WRITE;
/*!40000 ALTER TABLE `sys_college` DISABLE KEYS */;
INSERT INTO `sys_college` VALUES (1,'计算机科学与技术学院',1,'2026-03-18 21:56:40','2026-03-18 21:56:40'),(2,'电子信息工程学院',1,'2026-03-18 21:56:40','2026-03-18 21:56:40'),(3,'经济管理学院',1,'2026-03-18 21:56:40','2026-03-18 21:56:40'),(4,'人文学院',1,'2026-03-18 21:56:40','2026-03-18 21:56:40'),(5,'外国语学院',1,'2026-03-18 21:56:40','2026-03-18 21:56:40'),(6,'纺织学院',1,'2026-03-26 20:35:25','2026-03-26 20:35:25'),(7,'**',1,'2026-03-26 21:29:32','2026-03-26 21:29:32'),(8,'生物与医药学院',1,'2026-03-27 09:13:55','2026-03-27 09:28:48'),(9,'医学院',1,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(10,'机械学院',1,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(11,'建筑学院',1,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(12,'外语学院',1,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(13,'法学院',1,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(14,'商学院',1,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(15,'文学院',1,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(16,'计算机学院',1,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(17,'艺术学院',1,'2026-03-27 09:13:55','2026-03-27 09:13:55');
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
) ENGINE=InnoDB AUTO_INCREMENT=188 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_operation_log`
--

LOCK TABLES `sys_operation_log` WRITE;
/*!40000 ALTER TABLE `sys_operation_log` DISABLE KEYS */;
INSERT INTO `sys_operation_log` VALUES (1,1,'POST /sys-student','[{\"classId\":2,\"collegeId\":1,\"password\":\"***\",\"realName\":\"lr\",\"status\":1,\"studentId\":\"123\"}]','0:0:0:0:0:0:0:1','2026-03-26 20:30:31'),(2,1,'POST /sys-college','新增学院：collegeName=纺织学院，status=1','0:0:0:0:0:0:0:1','2026-03-26 20:35:25'),(3,1,'POST /sys-class','新增班级：className=软件2304，collegeId=1','127.0.0.1','2026-03-26 20:38:41'),(4,1,'POST /sys-college','新增学院：collegeName=**，status=1','127.0.0.1','2026-03-26 21:29:32'),(5,1,'POST /sys-role/assign-permission','新增角色：roleId=2','127.0.0.1','2026-03-26 22:18:38'),(6,1,'PUT /user/teacher/5/classes','更新用户：5','127.0.0.1','2026-03-27 08:57:58'),(7,1,'POST /user/assign-role','新增用户：userId=5','127.0.0.1','2026-03-27 08:59:26'),(8,1,'PUT /user/teacher/5/classes','更新用户：5','127.0.0.1','2026-03-27 08:59:45'),(9,1,'POST /sys-student/import-excel','新增学生：filename=测试学生.xlsx','127.0.0.1','2026-03-27 09:13:56'),(10,1,'POST /user/import-teacher-excel','新增用户：filename=测试学生.xlsx','127.0.0.1','2026-03-27 09:25:00'),(11,1,'PUT /sys-college/8','更新学院：collegeName=生物与医药学院，status=1，8','127.0.0.1','2026-03-27 09:28:48'),(12,1,'POST /sys-role/assign-permission','新增角色：roleId=2','127.0.0.1','2026-03-27 16:37:15'),(13,1,'POST /sys-role/assign-permission','新增角色：roleId=2','127.0.0.1','2026-03-27 17:08:08'),(14,1,'POST /sys-role/assign-permission','新增角色：roleId=2','127.0.0.1','2026-03-27 21:31:05'),(15,1,'POST /user','新增用户：realName=lirui，collegeId=1，status=1','127.0.0.1','2026-03-28 09:25:31'),(16,1,'PUT /evaluation-period/2','更新数据：periodName=2025-2026学年第一学期，status=1，2','127.0.0.1','2026-04-01 11:21:50'),(17,5,'POST /student-apply/submit','新增数据：periodId=2','127.0.0.1','2026-04-01 11:22:50'),(18,1,'PUT /evaluation-approval/items/approve','更新数据：applyItemId=1','127.0.0.1','2026-04-01 11:23:15'),(19,5,'POST /student-apply/submit','新增数据：periodId=2','127.0.0.1','2026-04-01 22:21:07'),(20,1,'PUT /evaluation-approval/items/approve','更新数据：applyItemId=2','127.0.0.1','2026-04-01 22:21:26'),(21,1,'POST /user/assign-role','新增用户：userId=5','127.0.0.1','2026-04-01 22:24:42'),(22,1,'POST /academic-scores','新增数据：periodId=2，studentNo=23110506122，className=计算机科学与技术1班，studentName=sjk，intellectualScore=90','127.0.0.1','2026-04-01 22:25:56'),(23,1,'POST /student-apply/upload-material','新增数据：filename=简历.pdf','127.0.0.1','2026-04-02 10:59:59'),(24,1,'POST /student-apply/upload-material','新增数据：filename=附件2：2026年春季心理筛查学生操作说明.pdf','127.0.0.1','2026-04-02 11:01:29'),(25,1,'POST /ruleCategory-categories/copy-by-period','新增规则分类：sourcePeriodId=2，targetPeriodId=1','127.0.0.1','2026-04-03 21:38:51'),(26,5,'POST /student-apply/upload-material','新增数据：filename=附件2：2026年春季心理筛查学生操作说明.pdf','127.0.0.1','2026-04-03 21:43:20'),(27,5,'POST /student-apply/upload-material','新增数据：filename=技术架构设计说明书.docx','127.0.0.1','2026-04-03 22:02:00'),(28,5,'POST /student-apply/upload-material','新增数据：filename=计算机科学与技术学院本科学生综合素质评价实施细则.pdf','127.0.0.1','2026-04-03 22:03:06'),(29,5,'POST /student-apply/submit','新增数据：periodId=2','127.0.0.1','2026-04-03 22:04:44'),(30,1,'PUT /evaluation-approval/items/approve','更新数据：applyItemId=4','127.0.0.1','2026-04-03 22:08:23'),(31,1,'PUT /evaluation-approval/items/approve','更新数据：applyItemId=3','127.0.0.1','2026-04-03 22:08:25'),(32,5,'POST /student-apply/submit','新增数据：periodId=2','127.0.0.1','2026-04-03 22:09:27'),(33,1,'PUT /evaluation-approval/items/approve','更新数据：applyItemId=5','127.0.0.1','2026-04-03 22:09:42'),(34,5,'POST /student-apply/submit','新增数据：periodId=2','127.0.0.1','2026-04-03 22:10:12'),(35,1,'PUT /evaluation-approval/items/approve','更新数据：applyItemId=6','127.0.0.1','2026-04-03 22:10:25'),(36,1,'PUT /ruleCategory-categories/17','更新规则分类：ruleId=1，categoryName=任职，parentId=9，studentVisible=1，categoryBaseScore=0，17','127.0.0.1','2026-04-05 17:35:11'),(37,1,'PUT /ruleCategory-categories/16','更新规则分类：ruleId=2，categoryName=任职，parentId=2，studentVisible=1，categoryBaseScore=0，16','127.0.0.1','2026-04-05 17:35:17'),(38,1,'PUT /ruleCategory-categories/18','更新规则分类：ruleId=3，categoryName=任职，parentId=19，studentVisible=1，categoryBaseScore=0，18','127.0.0.1','2026-04-05 18:40:53'),(39,1,'PUT /ruleCategory-categories/19','更新规则分类：ruleId=3，categoryName=德育评价，parentId=0，scoreCap=10，studentVisible=1，categoryBaseScore=0，19','127.0.0.1','2026-04-05 18:45:23'),(40,1,'PUT /ruleCategory-categories/23','更新规则分类：ruleId=3，categoryName=素质能力评价，parentId=0，scoreCap=20，studentVisible=1，categoryBaseScore=0，23','127.0.0.1','2026-04-05 18:46:31'),(41,1,'PUT /ruleCategory-categories/24','更新规则分类：ruleId=3，categoryName=身心素养-基础（满分3分），parentId=23，scoreCap=3，studentVisible=1，categoryBaseScore=3，24','127.0.0.1','2026-04-05 18:47:24'),(42,1,'PUT /ruleCategory-categories/24','更新规则分类：ruleId=3，categoryName=身心素养-基础，parentId=23，scoreCap=3，studentVisible=1，categoryBaseScore=3，24','127.0.0.1','2026-04-05 18:47:36'),(43,1,'PUT /ruleCategory-categories/25','更新规则分类：ruleId=3，categoryName=身心素养-奖励分，parentId=23，scoreCap=1，studentVisible=1，categoryBaseScore=0，25','127.0.0.1','2026-04-05 18:47:44'),(44,1,'PUT /ruleCategory-categories/26','更新规则分类：ruleId=3，categoryName=审美和人文素养-基础分，parentId=23，scoreCap=3，studentVisible=1，categoryBaseScore=0，26','127.0.0.1','2026-04-05 18:48:16'),(45,1,'PUT /ruleCategory-categories/27','更新规则分类：ruleId=3，categoryName=审美和人文素养-奖励分，parentId=23，scoreCap=1，studentVisible=1，categoryBaseScore=0，27','127.0.0.1','2026-04-05 18:48:30'),(46,1,'PUT /ruleCategory-categories/28','更新规则分类：ruleId=3，categoryName=劳动素养-基础分，parentId=23，scoreCap=3，studentVisible=1，categoryBaseScore=0，28','127.0.0.1','2026-04-05 18:49:04'),(47,1,'PUT /ruleCategory-categories/29','更新规则分类：ruleId=3，categoryName=劳动素养-奖励分，parentId=23，scoreCap=1，studentVisible=1，categoryBaseScore=0，29','127.0.0.1','2026-04-05 18:49:17'),(48,1,'PUT /ruleCategory-categories/30','更新规则分类：ruleId=3，categoryName=创新素养-基础性评价，parentId=23，scoreCap=6，studentVisible=1，categoryBaseScore=0，30','127.0.0.1','2026-04-05 18:50:04'),(49,1,'PUT /ruleCategory-categories/31','更新规则分类：ruleId=3，categoryName=创新素养-发展性评价，parentId=23，scoreCap=2，studentVisible=1，categoryBaseScore=0，31','127.0.0.1','2026-04-05 18:50:18'),(50,1,'PUT /ruleCategory-categories/21','更新规则分类：ruleId=3，categoryName=德育-奖励分，parentId=19，scoreCap=3，studentVisible=1，categoryBaseScore=0，21','127.0.0.1','2026-04-05 18:51:50'),(51,1,'PUT /ruleCategory-categories/20','更新规则分类：ruleId=3，categoryName=德育-基础分，parentId=19，studentVisible=1，categoryBaseScore=0，20','127.0.0.1','2026-04-05 18:52:11'),(52,1,'PUT /ruleCategory-categories/20','更新规则分类：ruleId=3，categoryName=德育-基础分，parentId=19，studentVisible=1，categoryBaseScore=7，20','127.0.0.1','2026-04-05 18:52:22'),(53,1,'PUT /ruleCategory-categories/26','更新规则分类：ruleId=3，categoryName=审美和人文素养-基础分，parentId=23，scoreCap=3，studentVisible=1，categoryBaseScore=3，26','127.0.0.1','2026-04-05 18:52:32'),(54,1,'PUT /ruleCategory-categories/28','更新规则分类：ruleId=3，categoryName=劳动素养-基础分，parentId=23，scoreCap=3，studentVisible=1，categoryBaseScore=3，28','127.0.0.1','2026-04-05 18:52:38'),(55,5,'POST /student-apply/upload-material','新增数据：filename=附件2：2026年春季心理筛查学生操作说明.pdf','127.0.0.1','2026-04-05 18:54:30'),(56,5,'POST /student-apply/upload-material','新增数据：filename=计算机科学与技术学院本科学生综合素质评价实施细则 (1) (2).pdf','127.0.0.1','2026-04-05 18:54:52'),(57,5,'POST /student-apply/submit','新增数据：periodId=3','127.0.0.1','2026-04-05 18:54:55'),(58,1,'PUT /evaluation-approval/items/approve','更新数据：applyItemId=8','127.0.0.1','2026-04-05 18:55:16'),(59,1,'PUT /evaluation-approval/items/approve','更新数据：applyItemId=7','127.0.0.1','2026-04-05 18:55:18'),(60,1,'POST /academic-scores','新增数据：periodId=3，studentNo=23110506122，className=计算机科学与技术1班，studentName=sjk，intellectualScore=90','127.0.0.1','2026-04-05 19:25:20'),(61,1,'DELETE /rule-items','删除规则项：id=192','127.0.0.1','2026-04-05 19:27:22'),(62,1,'DELETE /rule-items','删除规则项：id=191','127.0.0.1','2026-04-05 19:27:24'),(63,1,'DELETE /rule-items','删除规则项：id=190','127.0.0.1','2026-04-05 19:27:26'),(64,1,'DELETE /rule-items','删除规则项：id=189','127.0.0.1','2026-04-05 19:27:28'),(65,1,'DELETE /rule-items','删除规则项：id=188','127.0.0.1','2026-04-05 19:27:30'),(66,1,'DELETE /rule-items','删除规则项：id=187','127.0.0.1','2026-04-05 19:27:33'),(67,1,'DELETE /rule-items','删除规则项：id=186','127.0.0.1','2026-04-05 19:27:36'),(68,1,'DELETE /rule-items','删除规则项：id=185','127.0.0.1','2026-04-05 19:27:38'),(69,1,'DELETE /rule-items','删除规则项：id=184','127.0.0.1','2026-04-05 19:27:45'),(70,1,'DELETE /rule-items','删除规则项：id=182','127.0.0.1','2026-04-05 19:27:48'),(71,1,'DELETE /rule-items','删除规则项：id=183','127.0.0.1','2026-04-05 19:27:50'),(72,1,'DELETE /rule-items','删除规则项：id=193','127.0.0.1','2026-04-05 19:28:00'),(73,1,'DELETE /rule-items','删除规则项：id=194','127.0.0.1','2026-04-05 19:28:02'),(74,1,'DELETE /rule-items','删除规则项：id=195','127.0.0.1','2026-04-05 19:28:03'),(75,1,'DELETE /rule-items','删除规则项：id=196','127.0.0.1','2026-04-05 19:28:05'),(76,1,'PUT /rule-items/197','更新规则项：ruleId=3，itemName=无故不参加校院组织的大学生心理普查、阳光 体育等身心健康活动、无故不参加军...，itemType=1，level=基础扣分，baseScore=1.5，status=1，scoreMode=SUB，moduleCode=QUALITY','127.0.0.1','2026-04-05 19:33:59'),(77,1,'PUT /rule-items/165','更新规则项：ruleId=3，itemName=德育-课堂迟到（每次），itemType=1，level=考勤，baseScore=0.2，status=1，scoreMode=SUB，moduleCode=MORAL','127.0.0.1','2026-04-05 19:34:50'),(78,1,'PUT /rule-items/166','更新规则项：ruleId=3，itemName=德育-课堂早退（每次），itemType=1，level=考勤，baseScore=0.2，status=1，scoreMode=SUB，moduleCode=MORAL','127.0.0.1','2026-04-05 19:34:57'),(79,1,'PUT /rule-items/167','更新规则项：ruleId=3，itemName=德育-课堂旷课（每节），itemType=1，level=考勤，baseScore=0.5，status=1，scoreMode=SUB，moduleCode=MORAL','127.0.0.1','2026-04-05 19:35:06'),(80,1,'PUT /rule-items/170','更新规则项：ruleId=3，itemName=德育-集体活动缺勤（每次），itemType=1，level=活动，baseScore=0.3，status=1，scoreMode=SUB，moduleCode=MORAL','127.0.0.1','2026-04-05 19:35:22'),(81,1,'PUT /rule-items/160','更新规则项：ruleId=3，itemName=通报批评，itemType=1，level=处分，baseScore=0.8，status=1，scoreMode=SUB，moduleCode=MORAL','127.0.0.1','2026-04-05 19:35:38'),(82,1,'PUT /rule-items/160','更新规则项：ruleId=3，itemName=德育-通报批评，itemType=1，level=处分，baseScore=0.8，status=1，scoreMode=SUB，moduleCode=MORAL','127.0.0.1','2026-04-05 19:36:03'),(83,1,'PUT /rule-items/197','更新规则项：ruleId=3，itemName=身心素养-基础扣分，itemType=1，level=基础扣分，baseScore=1.5，status=1，scoreMode=SUB，moduleCode=QUALITY','127.0.0.1','2026-04-05 19:36:53'),(84,1,'PUT /rule-items/229','更新规则项：ruleId=3，itemName=身心素养-校运会仪仗队，itemType=0，level=活动，baseScore=0.4，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 19:38:12'),(85,1,'POST /rule-items','新增规则项：ruleId=3，itemName=身心素养-校运会健美操，itemType=0，level=校级，baseScore=0.4，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 19:39:31'),(86,1,'POST /rule-items','新增规则项：ruleId=3，itemName=身心素养-运动会观众，itemType=0，level=校级，baseScore=0.1，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 19:42:48'),(87,1,'POST /rule-items','新增规则项：ruleId=3，itemName=身心素养-积极参与学院运动训练，itemType=0，level=校级，baseScore=0.4，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 19:43:18'),(88,1,'POST /rule-items','新增规则项：ruleId=3，itemName=身心素养-参加校运会、12.9万米接力、校篮球、足球、 排球等学校体育赛事，itemType=0，level=校级，baseScore=0.4，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 19:44:26'),(89,1,'PUT /rule-items/229','更新规则项：ruleId=3，itemName=身心素养-校运会仪仗队，itemType=0，level=校级，baseScore=0.4，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 19:44:55'),(90,1,'POST /rule-items','新增规则项：ruleId=3，itemName=身心素养-关注同学者（根据具体加分多次选择），itemType=0，level=校级，baseScore=0.1，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 19:45:46'),(91,1,'PUT /rule-items/274','更新规则项：ruleId=3，itemName=劳动素养-基础扣分，itemType=1，level=违纪，baseScore=1.5，status=1，scoreMode=SUB，moduleCode=QUALITY','127.0.0.1','2026-04-05 19:52:04'),(92,1,'POST /rule-items','新增规则项：ruleId=3，itemName=劳动素养-宿舍卫生成绩低于90分，itemType=1，level=校级，baseScore=0.2，status=1，scoreMode=SUB，moduleCode=QUALITY','127.0.0.1','2026-04-05 19:52:52'),(93,1,'DELETE /rule-items','删除规则项：id=325','127.0.0.1','2026-04-05 20:13:25'),(94,1,'DELETE /rule-items','删除规则项：id=340','127.0.0.1','2026-04-05 20:13:27'),(95,1,'DELETE /rule-items','删除规则项：id=326','127.0.0.1','2026-04-05 20:13:56'),(96,1,'DELETE /rule-items','删除规则项：id=341','127.0.0.1','2026-04-05 20:13:59'),(97,1,'PUT /rule-items/327','更新规则项：ruleId=3，itemName=其他加分，itemType=0，level=其他，baseScore=0.1，status=1，scoreMode=ADD，moduleCode=MORAL','127.0.0.1','2026-04-05 20:14:07'),(98,1,'PUT /rule-items/310','更新规则项：ruleId=3，itemName=劳动素养-英语四级，itemType=0，level=过级，baseScore=0.4，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:37:18'),(99,1,'PUT /rule-items/311','更新规则项：ruleId=3，itemName=劳动素养-英语六级，itemType=0，level=过级，baseScore=0.6，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:37:24'),(100,1,'DELETE /rule-items','删除规则项：id=312','127.0.0.1','2026-04-05 20:38:14'),(101,1,'POST /rule-items','新增规则项：ruleId=3，itemName=劳动素养-宿舍加分，itemType=0，level=校级，baseScore=0.1，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:39:54'),(102,1,'POST /rule-items','新增规则项：ruleId=3，itemName=劳动素养-志愿服务加分，itemType=0，level=校级，baseScore=0.1，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:40:15'),(103,1,'POST /ruleCategory-categories','新增规则分类：id=32，ruleId=3，categoryName=创始实践能力加分，parentId=30，scoreCap=4，studentVisible=1，categoryBaseScore=0','127.0.0.1','2026-04-05 20:43:03'),(104,1,'POST /ruleCategory-categories','新增规则分类：id=33，ruleId=3，categoryName=基础竞赛加分，parentId=30，scoreCap=2，studentVisible=1，categoryBaseScore=0','127.0.0.1','2026-04-05 20:43:24'),(105,1,'POST /rule-items','新增规则项：ruleId=3，itemName=积极参加对外国际交流活动，itemType=0，level=校级，baseScore=4.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:44:58'),(106,1,'POST /rule-items','新增规则项：ruleId=3，itemName=创新素养-参加学校认定的省级及以上创新创业竞赛活动，itemType=0，level=校级，baseScore=3.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:45:37'),(107,1,'POST /rule-items','新增规则项：ruleId=3，itemName=创新素养-积极申报省级及以上创新创业项目，itemType=0，level=校级，baseScore=4.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:46:13'),(108,1,'PUT /rule-items/358','更新规则项：ruleId=3，itemName=创新实践-参加学校认定的省级及以上创新创业竞赛活动，itemType=0，level=校级，baseScore=3.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:46:35'),(109,1,'PUT /rule-items/359','更新规则项：ruleId=3，itemName=创新实践-积极申报省级及以上创新创业项目，itemType=0，level=校级，baseScore=4.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:46:41'),(110,1,'PUT /rule-items/357','更新规则项：ruleId=3，itemName=创新实践-积极参加对外国际交流活动，itemType=0，level=校级，baseScore=4.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:46:50'),(111,1,'POST /rule-items','新增规则项：ruleId=3，itemName=创新实践-积极参加校级创新创业竞赛，itemType=0，level=校级，baseScore=2.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:47:36'),(112,1,'POST /rule-items','新增规则项：ruleId=3，itemName=创新实践-积极参加各类技能培训认证和国内对外交流活动，itemType=0，level=校级，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:48:15'),(113,1,'POST /rule-items','新增规则项：ruleId=3，itemName=创新实践-积极参加大学生四、六级考试、研究生考试，itemType=0，level=校级，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:48:54'),(114,1,'PUT /ruleCategory-categories/32','更新规则分类：ruleId=3，categoryName=创新素养-创始实践能力加分，parentId=30，scoreCap=4，studentVisible=1，categoryBaseScore=0，32','127.0.0.1','2026-04-05 20:49:23'),(115,1,'PUT /ruleCategory-categories/33','更新规则分类：ruleId=3，categoryName=创新素养-基础竞赛加分，parentId=30，scoreCap=2，studentVisible=1，categoryBaseScore=0，33','127.0.0.1','2026-04-05 20:49:34'),(116,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-国家级-一等奖，itemType=0，level=国家级，baseScore=2.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:51:28'),(117,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-国家级-二等奖，itemType=0，level=国家级，baseScore=1.8，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:51:54'),(118,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-国家级-三等奖，itemType=0，level=校级，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:52:11'),(119,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-国家级-优秀奖，itemType=0，level=国家级，baseScore=1.2，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:52:33'),(120,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-省级-一等奖，itemType=0，level=省级，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:52:54'),(121,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-省级-二等奖，itemType=0，level=省级，baseScore=1.2，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:53:13'),(122,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-省级-三等奖，itemType=0，level=省级，baseScore=1.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:53:33'),(123,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-省级-优秀奖，itemType=0，level=省级，baseScore=0.8，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:53:51'),(124,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-市校级-一等奖，itemType=0，level=校级，baseScore=1.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:54:13'),(125,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-市校级-二等奖，itemType=0，level=校级，baseScore=0.8，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:54:31'),(126,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-市校级-三等奖，itemType=0，level=校级，baseScore=0.6，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:54:49'),(127,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-市校级-优秀奖，itemType=0，level=校级，baseScore=0.4，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:55:09'),(128,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-院级-一等奖，itemType=0，level=院级，baseScore=0.6，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:55:31'),(129,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-院级-二等奖，itemType=0，level=院级，baseScore=0.4，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:55:56'),(130,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-院级-三等奖，itemType=0，level=院级，baseScore=0.3，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:56:15'),(131,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛加分-院级-优秀奖，itemType=0，level=院级，baseScore=0.2，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:56:37'),(132,1,'PUT /rule-items/310','更新规则项：ruleId=3，itemName=竞赛加分-四级过级，itemType=0，level=过级，baseScore=0.4，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:57:41'),(133,1,'PUT /rule-items/311','更新规则项：ruleId=3，itemName=竞赛加分-六级过级，itemType=0，level=过级，baseScore=0.6，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:57:56'),(134,1,'PUT /rule-items/313','更新规则项：ruleId=3，itemName=创新素养-基础性评价（竞赛/科研/专利/论文/双创），itemType=0，level=基础，baseScore=6.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:58:18'),(135,1,'PUT /rule-items/314','更新规则项：ruleId=3，itemName=创新素养-论文发表第一作者（篇），itemType=0，level=论文，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:58:24'),(136,1,'PUT /rule-items/315','更新规则项：ruleId=3，itemName=创新素养-论文发表第二作者（按1/2），itemType=0，level=论文，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:58:29'),(137,1,'PUT /rule-items/316','更新规则项：ruleId=3，itemName=创新素养-论文发表第三作者（按1/4），itemType=0，level=论文，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:58:34'),(138,1,'PUT /rule-items/317','更新规则项：ruleId=3，itemName=创新素养-论文发表第四作者及以后不加分（说明项），itemType=0，level=论文规则，baseScore=0.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 20:58:39'),(139,1,'DELETE /rule-items','删除规则项：id=337','127.0.0.1','2026-04-05 20:58:41'),(140,1,'DELETE /rule-items','删除规则项：id=352','127.0.0.1','2026-04-05 20:58:42'),(141,1,'PUT /rule-items/379','更新规则项：ruleId=3，itemName=A+竞赛-国家级-一等奖，itemType=0，level=国家级一等奖，baseScore=2.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:05:45'),(142,1,'PUT /rule-items/380','更新规则项：ruleId=3，itemName=A+竞赛-国家级-二等奖，itemType=0，level=国家级二等奖，baseScore=1.8，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:05:54'),(143,1,'PUT /rule-items/381','更新规则项：ruleId=3，itemName=A+竞赛-国家级-三等奖，itemType=0，level=国家级三等奖，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:06:00'),(144,1,'PUT /rule-items/382','更新规则项：ruleId=3，itemName=A+竞赛-国家级-优秀奖，itemType=0，level=国家级优秀奖，baseScore=1.2，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:06:06'),(145,1,'PUT /rule-items/383','更新规则项：ruleId=3，itemName=A+竞赛-省级-一等奖，itemType=0，level=省级一等奖，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:06:16'),(146,1,'PUT /rule-items/384','更新规则项：ruleId=3，itemName=A+竞赛-省级-二等奖，itemType=0，level=省级二等奖，baseScore=1.2，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:06:22'),(147,1,'PUT /rule-items/385','更新规则项：ruleId=3，itemName=A+竞赛-省级-三等奖，itemType=0，level=省级三等奖，baseScore=1.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:06:31'),(148,1,'PUT /rule-items/386','更新规则项：ruleId=3，itemName=A+竞赛-省级-优秀奖，itemType=0，level=省级优秀奖，baseScore=0.8，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:06:38'),(149,1,'PUT /rule-items/387','更新规则项：ruleId=3，itemName=A+竞赛-市校级-一等奖，itemType=0，level=市校级一等奖，baseScore=1.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:06:49'),(150,1,'PUT /rule-items/388','更新规则项：ruleId=3，itemName=A+竞赛-市校级-二等奖，itemType=0，level=市校级二等奖，baseScore=0.8，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:06:53'),(151,1,'PUT /rule-items/389','更新规则项：ruleId=3，itemName=A+竞赛-市校级-三等奖，itemType=0，level=市校级三等奖，baseScore=0.6，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:06:57'),(152,1,'PUT /rule-items/390','更新规则项：ruleId=3，itemName=A+竞赛-市校级-优秀奖，itemType=0，level=市校级优秀奖，baseScore=0.4，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:07:05'),(153,1,'DELETE /rule-items','删除规则项：id=391','127.0.0.1','2026-04-05 21:07:18'),(154,1,'DELETE /rule-items','删除规则项：id=392','127.0.0.1','2026-04-05 21:07:21'),(155,1,'DELETE /rule-items','删除规则项：id=393','127.0.0.1','2026-04-05 21:07:23'),(156,1,'DELETE /rule-items','删除规则项：id=394','127.0.0.1','2026-04-05 21:07:24'),(157,1,'DELETE /rule-items','删除规则项：id=313','127.0.0.1','2026-04-05 21:07:59'),(158,1,'DELETE /rule-items','删除规则项：id=314','127.0.0.1','2026-04-05 21:08:00'),(159,1,'DELETE /rule-items','删除规则项：id=315','127.0.0.1','2026-04-05 21:08:02'),(160,1,'DELETE /rule-items','删除规则项：id=316','127.0.0.1','2026-04-05 21:08:04'),(161,1,'DELETE /rule-items','删除规则项：id=317','127.0.0.1','2026-04-05 21:08:06'),(162,1,'DELETE /rule-items','删除规则项：id=318','127.0.0.1','2026-04-05 21:08:07'),(163,1,'PUT /rule-items/422','更新规则项：ruleId=3，itemName=论文专利-EI国际会议论文（作报告），itemType=0，level=论文，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:14:21'),(164,1,'PUT /rule-items/419','更新规则项：ruleId=3，itemName=论文专利-CCF推荐A/B/C类会议论文，itemType=0，level=论文，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:14:27'),(165,1,'PUT /rule-items/416','更新规则项：ruleId=3，itemName=论文专利-EI期刊论文（与专业密切相关），itemType=0，level=论文，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:14:35'),(166,1,'PUT /rule-items/413','更新规则项：ruleId=3，itemName=论文专利-SCI收录期刊论文，itemType=0，level=论文，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:14:41'),(167,1,'PUT /rule-items/410','更新规则项：ruleId=3，itemName=论文专利-北大核心期刊，itemType=0，level=论文，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:14:46'),(168,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛论文-知网CNKI检索期刊论文，itemType=0，level=校级，baseScore=1.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:15:41'),(169,1,'POST /rule-items','新增规则项：ruleId=3，itemName=竞赛论文-出版专著，itemType=0，level=校级，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:16:08'),(170,1,'POST /rule-items','新增规则项：ruleId=3，itemName=论文专利-软件著作权，itemType=0，level=校级，baseScore=0.8，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:16:38'),(171,1,'POST /rule-items','新增规则项：ruleId=3，itemName=论文专利-外观设计专利、实用新型专利，itemType=0，level=校级，baseScore=1.0，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:17:16'),(172,1,'POST /rule-items','新增规则项：ruleId=3，itemName=论文专利-发明专利，itemType=0，level=校级，baseScore=1.5，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:17:32'),(173,1,'DELETE /rule-items','删除规则项：id=329','127.0.0.1','2026-04-05 21:18:02'),(174,1,'DELETE /rule-items','删除规则项：id=344','127.0.0.1','2026-04-05 21:18:04'),(175,1,'DELETE /rule-items','删除规则项：id=330','127.0.0.1','2026-04-05 21:18:23'),(176,1,'DELETE /rule-items','删除规则项：id=345','127.0.0.1','2026-04-05 21:18:24'),(177,1,'PUT /rule-items/378','更新规则项：ruleId=3，itemName=竞赛加分-院级-优秀奖，itemType=0，level=院级，baseScore=0.2，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:18:59'),(178,1,'PUT /rule-items/377','更新规则项：ruleId=3，itemName=竞赛加分-院级-三等奖，itemType=0，level=院级，baseScore=0.3，status=1，scoreMode=ADD，moduleCode=QUALITY','127.0.0.1','2026-04-05 21:19:02'),(179,5,'POST /student-apply/upload-material','新增数据：filename=作业2 黑盒测试技术-等价类划分-02.xlsx','127.0.0.1','2026-04-06 10:27:57'),(180,5,'POST /student-apply/submit','新增数据：periodId=3','127.0.0.1','2026-04-06 10:27:59'),(181,1,'PUT /evaluation-approval/items/approve','更新数据：applyItemId=12','127.0.0.1','2026-04-06 10:28:34'),(182,1,'PUT /evaluation-approval/items/approve','更新数据：applyItemId=11','127.0.0.1','2026-04-06 10:28:36'),(183,1,'PUT /evaluation-approval/items/approve','更新数据：applyItemId=10','127.0.0.1','2026-04-06 10:28:39'),(184,1,'PUT /evaluation-approval/items/approve','更新数据：applyItemId=9','127.0.0.1','2026-04-06 10:28:50'),(185,5,'POST /student-apply/upload-material','新增数据：filename=作业3 黑盒测试技术-边界值分析-组名 (1).xlsx','127.0.0.1','2026-04-06 10:29:26'),(186,5,'POST /student-apply/submit','新增数据：periodId=3','127.0.0.1','2026-04-06 10:29:28'),(187,1,'PUT /evaluation-approval/items/approve','更新数据：applyItemId=13','127.0.0.1','2026-04-06 10:29:38');
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
) ENGINE=InnoDB AUTO_INCREMENT=207 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES (1,'教师管理','sys:user:menu',100,1,'2026-03-16 16:20:34','2026-03-26 21:02:52'),(2,'角色管理','sys:role:menu',100,1,'2026-03-16 16:20:34','2026-03-26 05:23:10'),(3,'权限管理','sys:perm:menu',100,1,'2026-03-16 16:20:34','2026-03-26 05:23:10'),(4,'学生管理','sys:student:menu',100,1,'2026-03-16 16:20:34','2026-03-26 05:23:10'),(5,'规则分类管理','sys:category:menu',101,1,'2026-03-17 10:29:13','2026-03-26 05:23:10'),(6,'规则总览','sys:rule:menu',101,1,'2026-03-17 10:30:08','2026-03-26 05:23:10'),(7,'规则项管理','sys:item:menu',101,1,'2026-03-17 10:30:45','2026-03-26 05:23:10'),(16,'学院管理','sys:college:menu',101,1,'2026-03-21 13:18:05','2026-03-26 05:23:10'),(17,'班级管理','sys:class:menu',101,1,'2026-03-21 13:18:05','2026-03-26 05:23:10'),(18,'智育成绩管理','sys:academic:menu',101,1,'2026-03-27 21:27:03','2026-03-27 21:27:03'),(100,'系统管理','sys:group:system',0,1,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(101,'教学评估','sys:group:evaluation',0,1,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(102,'用户分配角色','sys:user:assign:menu',100,1,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(103,'角色分配菜单','sys:role:assign:menu',100,1,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(104,'综测申报审批','sys:approval:menu',101,1,'2026-03-27 19:44:58','2026-03-27 19:44:58'),(200,'操作日志','sys:log:menu',100,1,'2026-03-26 17:57:43','2026-03-26 17:57:43'),(201,'班级综测成绩','sys:class-score:menu',101,1,'2026-03-28 09:09:48','2026-03-28 09:09:48'),(202,'申诉处理','sys:appeal:menu',101,1,'2026-03-28 09:30:10','2026-03-28 09:30:10'),(203,'综测流程与时间','sys:period:flow:menu',101,1,'2026-03-28 10:29:03','2026-03-28 10:29:03'),(204,'综测异议处理','sys:objection:menu',101,1,'2026-03-28 10:29:03','2026-03-28 10:29:03'),(205,'学生申报提示维护','sys:submit-tip:menu',101,1,'2026-03-30 20:05:56','2026-03-30 20:05:56');
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
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` VALUES (17,3,1,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(18,3,2,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(19,3,3,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(20,3,4,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(24,1,4,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(25,3,5,'2026-03-17 10:31:42','2026-03-17 10:31:43'),(26,3,6,'2026-03-17 10:31:49','2026-03-17 10:31:50'),(27,3,7,'2026-03-17 10:31:57','2026-03-17 10:31:59'),(32,3,16,'2026-03-21 13:18:05','2026-03-21 13:18:05'),(33,3,17,'2026-03-21 13:18:05','2026-03-21 13:18:05'),(34,3,100,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(35,3,101,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(36,3,102,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(37,3,103,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(45,3,200,'2026-03-26 17:57:43','2026-03-26 17:57:43'),(61,3,104,'2026-03-27 19:44:59','2026-03-27 19:44:59'),(62,3,18,'2026-03-27 21:27:03','2026-03-27 21:27:03'),(64,2,4,'2026-03-27 21:31:05','2026-03-27 21:31:05'),(65,2,5,'2026-03-27 21:31:05','2026-03-27 21:31:05'),(66,2,6,'2026-03-27 21:31:05','2026-03-27 21:31:05'),(67,2,7,'2026-03-27 21:31:05','2026-03-27 21:31:05'),(68,2,18,'2026-03-27 21:31:05','2026-03-27 21:31:05'),(69,2,104,'2026-03-27 21:31:05','2026-03-27 21:31:05'),(70,3,201,'2026-03-28 09:09:48','2026-03-28 09:09:48'),(71,2,201,'2026-03-28 09:09:49','2026-03-28 09:09:49'),(72,3,202,'2026-03-28 09:30:10','2026-03-28 09:30:10'),(73,2,202,'2026-03-28 09:30:10','2026-03-28 09:30:10'),(74,3,203,'2026-03-28 10:29:03','2026-03-28 10:29:03'),(75,3,204,'2026-03-28 10:29:03','2026-03-28 10:29:03'),(76,2,204,'2026-03-28 10:29:03','2026-03-28 10:29:03'),(78,3,205,'2026-03-30 20:05:56','2026-03-30 20:05:56'),(79,2,205,'2026-03-30 20:05:56','2026-03-30 20:05:56');
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_teacher_class`
--

DROP TABLE IF EXISTS `sys_teacher_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_teacher_class` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `teacher_user_id` int NOT NULL COMMENT '教师用户ID（sys_user.id）',
  `class_id` int NOT NULL COMMENT '班级ID（sys_class.id）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_class` (`teacher_user_id`,`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师负责班级';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_teacher_class`
--

LOCK TABLES `sys_teacher_class` WRITE;
/*!40000 ALTER TABLE `sys_teacher_class` DISABLE KEYS */;
INSERT INTO `sys_teacher_class` VALUES (2,5,1,'2026-03-27 08:59:45','2026-03-27 08:59:45'),(3,5,2,'2026-03-27 08:59:45','2026-03-27 08:59:45');
/*!40000 ALTER TABLE `sys_teacher_class` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','{bcrypt}$2a$10$ijC.UqIhDVWnFOFI9e8fPusbeffVvabJ279LQ1Etqvl4IwZedW0ge','admin',0,0,1,'2026-03-11 16:34:10','2026-03-14 16:03:11'),(2,'2023001','{bcrypt}$2a$10$ijC.UqIhDVWnFOFI9e8fPusbeffVvabJ279LQ1Etqvl4IwZedW0ge','张三',1,101,1,'2026-03-16 14:23:33','2026-03-26 13:28:19'),(3,'2023002','{bcrypt}$2a$10$ijC.UqIhDVWnFOFI9e8fPusbeffVvabJ279LQ1Etqvl4IwZedW0ge','李四',1,101,1,'2026-03-16 14:23:33','2026-03-17 11:35:40'),(4,'2023003','{bcrypt}$2a$10$ijC.UqIhDVWnFOFI9e8fPusbeffVvabJ279LQ1Etqvl4IwZedW0ge','管理员',2,NULL,1,'2026-03-16 14:23:33','2026-03-17 11:35:40'),(5,'23110506122','{bcrypt}$2a$10$BoncPzPOjuy45nWoIRyyXOPvWHpyN3UX5rfCYAmJyXhiM8W/MrEOq','sjk',1,1,1,'2026-03-26 13:28:09','2026-03-26 13:28:30'),(6,'123','{bcrypt}$2a$10$UZANPGdsFIRFBUFVlGeVHOoz/eiOmuhE3dM3N7xyEipI6./Nkykj2','lr',1,2,1,'2026-03-26 20:30:31','2026-03-26 20:30:31'),(7,'2023010001','{bcrypt}$2a$10$i7wGe9aOfJuOV0pyN5OeUujSM5B2QtAVi3FtzTPPCB19wowNwvoZm','张伟',16,16,1,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(8,'2023010002','{bcrypt}$2a$10$0q0TVfWN2FFiTgtGt09k4Ouxm/KfHJCAj.P9xP0kZNHf8/yJ5rcw2','李婷',13,17,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(9,'2023010003','{bcrypt}$2a$10$.INxU2lUlZBLj4W9rlPRXO352A/cxi8pa5BVo/zETHC9Lg7Xv2bW.','王刚',9,14,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(10,'2023010004','{bcrypt}$2a$10$cVNMyzJdR4Qe8FuZ8wg6wOgKvpjkamhtKxkGch0LHjl3OXxiQfapm','刘晓',15,12,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(11,'2023010005','{bcrypt}$2a$10$VRuCK5BdHE5eiYoon.EB1uptxzvLhauR7exASuv.4/DQsEl.c0FkO','陈浩',14,18,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(12,'2023010006','{bcrypt}$2a$10$gTyd8JGCIP6WZ1Tj0dDH7O98bHrh51I51VGuvrfwbWpEePQiAm6.q','赵丽',17,10,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(13,'2023010007','{bcrypt}$2a$10$9jADhgyph7STebxOnwRbK.a03F5o9TCtx./dKuPY9cQayK.uCQIjG','黄磊',10,9,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(14,'2023010008','{bcrypt}$2a$10$jgoXDhvrI0lWTakdh0Cv9uzgJoxVBC6iDCLZWAgTofIJ/HUs00fOO','周婷',11,11,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(15,'2023010009','{bcrypt}$2a$10$rwvGZ3B4KabL3/DVD5KuLOC0DF4F5AY6THoOeoYT706q5YrwugFjm','吴翔',12,13,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(16,'2023010010','{bcrypt}$2a$10$5MfCyLQ/XspkiS1AkHWreugyjLYL2Z8jqedNTTv0ZdWeBSnBjs3Wa','王丽',8,15,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(17,'10001','{bcrypt}$2a$10$qfYg1Kff119dyj6sIHoeMunBrqkX8ct6XqVaVkuPPmCZCWIDuoj/e','张伟',16,NULL,1,'2026-03-27 09:24:59','2026-03-27 09:24:59'),(18,'10002','{bcrypt}$2a$10$Psam4Df.doHw7aOQMHWIcesFOLb26w8oAnR9V7PPlblVUvG0tToNW','李婷',13,NULL,1,'2026-03-27 09:24:59','2026-03-27 09:24:59'),(19,'10003','{bcrypt}$2a$10$oMeN7bprHBvOCPGmnQq7FO2Phn.jP50shDBUd6AmFVk510ePSi7IO','王刚',9,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(20,'10004','{bcrypt}$2a$10$/e5n.dlRVZl/POHxVx0EW.fFT6YLsHd15Jx1nUcsFrx3MlHcfLJUa','刘晓',15,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(21,'10005','{bcrypt}$2a$10$EMr8rmKrxNKaLoqd1WQpZ.HCYGP3Kb1m648HNeaLoUx2bX5TMKFY.','陈浩',14,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(22,'10006','{bcrypt}$2a$10$lk6HAXQm9MFk/Hg7xJB2W.CQwTFxHdy9q/SaK1BpzwJhfZ53ZG4bm','赵丽',17,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(23,'10007','{bcrypt}$2a$10$BoXWfaPx9PNblTAp5BSQuOWbWGbQwRXWKH8gyM3mR/XwO2E4FMLEq','黄磊',10,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(24,'10008','{bcrypt}$2a$10$OR75wCCWFd0bnimstgZma.HwfO7lAiiBe7ack8044SlKhU3JgVdI2','周婷',11,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(25,'10009','{bcrypt}$2a$10$IKpBAbJrYUxb5mfIbzVeeufR7omMovJtPCdocpSjkiQwyvz6XrHvS','吴翔',12,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(26,'10010','{bcrypt}$2a$10$eMY5xVYZYi3pcXOiyV4zZ.FAApTiRv41rewMYUbsKx/QFoVoFtI3K','王丽',8,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(27,'lirui','{bcrypt}$2a$10$UgFi9.Nrt5sMqCLmsZVSvO/dJaCCdSgBkbjJ9fAXH9E3D046O3PNG','lirui',1,NULL,1,'2026-03-28 09:25:31','2026-03-28 09:25:31');
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
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1,3,'2026-03-16 14:23:34','2026-03-16 14:34:42'),(2,2,1,'2026-03-16 14:23:34','2026-03-16 14:34:42'),(3,3,2,'2026-03-16 14:23:34','2026-03-16 14:34:42'),(4,4,3,'2026-03-16 14:34:49','2026-03-16 14:34:50'),(8,6,1,'2026-03-26 20:30:31','2026-03-26 20:30:31'),(10,7,1,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(11,8,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(12,9,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(13,10,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(14,11,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(15,12,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(16,13,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(17,14,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(18,15,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(19,16,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(20,17,2,'2026-03-27 09:24:59','2026-03-27 09:24:59'),(21,18,2,'2026-03-27 09:24:59','2026-03-27 09:24:59'),(22,19,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(23,20,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(24,21,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(25,22,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(26,23,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(27,24,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(28,25,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(29,26,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(30,27,2,'2026-03-28 09:25:31','2026-03-28 09:25:31'),(31,5,1,'2026-04-01 22:24:42','2026-04-01 22:24:42');
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

-- Dump completed on 2026-04-06 11:25:04
