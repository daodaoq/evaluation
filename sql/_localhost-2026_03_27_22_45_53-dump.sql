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

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '6a12693a-28b0-11f1-b8c5-246db74e60f7:1-437';

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
  PRIMARY KEY (`id`),
  KEY `idx_audit_apply_item` (`apply_item_id`)
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
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测周期表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_period`
--

LOCK TABLES `evaluation_period` WRITE;
/*!40000 ALTER TABLE `evaluation_period` DISABLE KEYS */;
INSERT INTO `evaluation_period` VALUES (1,'2026春季综测','2026-03-01 00:00:00','2026-07-31 23:59:59',1,'2026-03-26 05:53:29','2026-03-26 05:53:29'),(2,'2025-2026学年第一学期','2025-09-01 00:00:00','2026-01-31 23:59:59',1,'2026-03-27 19:27:01','2026-03-27 19:27:01');
/*!40000 ALTER TABLE `evaluation_period` ENABLE KEYS */;
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
  `moral_weight` decimal(5,2) NOT NULL DEFAULT '10.00' COMMENT '德育权重',
  `academic_weight` decimal(5,2) NOT NULL DEFAULT '70.00' COMMENT '学业权重',
  `quality_weight` decimal(5,2) NOT NULL DEFAULT '20.00' COMMENT '素质能力权重',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测规则表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_rule`
--

LOCK TABLES `evaluation_rule` WRITE;
/*!40000 ALTER TABLE `evaluation_rule` DISABLE KEYS */;
INSERT INTO `evaluation_rule` VALUES (1,1,'2026春季默认规则','v1',1,'2026-03-26 05:53:29','2026-03-26 05:53:29',10.00,70.00,20.00),(2,2,'计算机学院本科综测细则(2025)-全量','v2025.2',1,'2026-03-27 19:27:01','2026-03-27 19:27:01',10.00,70.00,20.00);
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
) ENGINE=InnoDB AUTO_INCREMENT=160 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='规则指标项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_rule_item`
--

LOCK TABLES `evaluation_rule_item` WRITE;
/*!40000 ALTER TABLE `evaluation_rule_item` DISABLE KEYS */;
INSERT INTO `evaluation_rule_item` VALUES (1,2,'德育-通报批评',1,2,'处分',0.80,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(2,2,'德育-警告',1,2,'处分',1.50,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(3,2,'德育-严重警告',1,2,'处分',2.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(4,2,'德育-记过',1,2,'处分',4.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(5,2,'德育-留校察看',1,2,'处分',6.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_DISCIPLINE',1.000,'MORAL','DISCIPLINE'),(6,2,'德育-课堂迟到（每次）',1,2,'考勤',0.10,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_ATTENDANCE',1.000,'MORAL','ATTENDANCE'),(7,2,'德育-课堂早退（每次）',1,2,'考勤',0.10,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_ATTENDANCE',1.000,'MORAL','ATTENDANCE'),(8,2,'德育-课堂旷课（每节）',1,2,'考勤',1.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_ATTENDANCE',1.000,'MORAL','ATTENDANCE'),(9,2,'德育-集体活动迟到（每次）',1,2,'活动',0.10,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_ACTIVITY',1.000,'MORAL','ACTIVITY'),(10,2,'德育-集体活动早退（每次）',1,2,'活动',0.10,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_ACTIVITY',1.000,'MORAL','ACTIVITY'),(11,2,'德育-集体活动缺勤（每次）',1,2,'活动',0.50,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_ACTIVITY',1.000,'MORAL','ACTIVITY'),(12,2,'德育-校园教学楼内吸烟（每次）',1,2,'行为',1.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_BEHAVIOR',1.000,'MORAL','BEHAVIOR'),(13,2,'德育-诚信问题（学术失信/恶意欠费等，每次）',1,2,'诚信',2.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','MORAL_INTEGRITY',1.000,'MORAL','INTEGRITY'),(14,2,'德育-严重违规行为（基础分清零）',1,2,'一票否决',10.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','MAX_ONLY','MORAL_ZERO',1.000,'MORAL','ZERO'),(15,2,'德育荣誉-好人好事-国家级',0,2,'国家级',3.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_GOOD',1.000,'MORAL','HONOR'),(16,2,'德育荣誉-好人好事-省级',0,2,'省级',2.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_GOOD',1.000,'MORAL','HONOR'),(17,2,'德育荣誉-好人好事-市/校级',0,2,'市校级',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_GOOD',1.000,'MORAL','HONOR'),(18,2,'德育荣誉-好人好事-院级',0,2,'院级',1.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_GOOD',1.000,'MORAL','HONOR'),(19,2,'德育荣誉-通报表扬-国家级',0,2,'国家级',2.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_COMMEND',1.000,'MORAL','HONOR'),(20,2,'德育荣誉-通报表扬-省级',0,2,'省级',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_COMMEND',1.000,'MORAL','HONOR'),(21,2,'德育荣誉-通报表扬-市/校级',0,2,'市校级',1.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_COMMEND',1.000,'MORAL','HONOR'),(22,2,'德育荣誉-通报表扬-院级',0,2,'院级',0.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_HONOR_COMMEND',1.000,'MORAL','HONOR'),(23,2,'学生干部-执行主席/辅导员助理',0,2,'学院',2.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(24,2,'学生干部-轮值主席/大社联主席/会长',0,2,'学院',2.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(25,2,'学生干部-部门负责人/党支部副书记/社联部门负责人',0,2,'学院',1.80,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(26,2,'学生干部-班长/团支书',0,2,'班级',2.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(27,2,'学生干部-班委/团支部委员/党支部委员/社联副部长',0,2,'班级',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(28,2,'学生干部-宿舍长',0,2,'班级',1.20,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(29,2,'学生干部-干事（考核合格）',0,2,'班级',1.20,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(30,2,'学生干部-社团干事',0,2,'班级',0.80,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',1.000,'MORAL','CADRE'),(31,2,'学生干部-次高职务折算',0,2,'折算',1.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',0.500,'MORAL','CADRE'),(32,2,'学生干部-第三职务折算',0,2,'折算',1.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',0.300,'MORAL','CADRE'),(33,2,'学生干部-满意度不足50%折算',0,2,'折算',1.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','MORAL_CADRE',0.500,'MORAL','CADRE'),(34,2,'学业水平-平均学分绩点折算总分',0,3,'系统计算',70.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','ACADEMIC_GPA',1.000,'ACADEMIC','GPA'),(35,2,'学业水平-课程免修折算70分（规则说明项）',0,3,'规则',0.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','ACADEMIC_RULE',1.000,'ACADEMIC','RULE'),(36,2,'学业水平-等级制折算（优95/良84/中73/及62）说明项',0,3,'规则',0.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','ACADEMIC_RULE',1.000,'ACADEMIC','RULE'),(37,2,'学业水平-考试作弊/旷考/禁考/缺考按0分说明项',1,3,'规则',0.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','ACADEMIC_RULE',1.000,'ACADEMIC','RULE'),(38,2,'身心素养-国家级-一等奖',0,5,'国家级一等奖',1.00,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(39,2,'身心素养-国家级-二等奖',0,5,'国家级二等奖',0.90,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(40,2,'身心素养-国家级-三等奖',0,5,'国家级三等奖',0.80,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(41,2,'身心素养-国家级-优秀奖',0,5,'国家级优秀奖',0.70,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(42,2,'身心素养-省级-一等奖',0,5,'省级一等奖',0.80,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(43,2,'身心素养-省级-二等奖',0,5,'省级二等奖',0.70,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(44,2,'身心素养-省级-三等奖',0,5,'省级三等奖',0.50,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(45,2,'身心素养-省级-优秀奖',0,5,'省级优秀奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(46,2,'身心素养-市校级-一等奖',0,5,'市校级一等奖',0.60,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(47,2,'身心素养-市校级-二等奖',0,5,'市校级二等奖',0.50,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(48,2,'身心素养-市校级-三等奖',0,5,'市校级三等奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(49,2,'身心素养-市校级-优秀奖',0,5,'市校级优秀奖',0.20,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(50,2,'身心素养-院级-一等奖',0,5,'院级一等奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(51,2,'身心素养-院级-二等奖',0,5,'院级二等奖',0.30,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(52,2,'身心素养-院级-三等奖',0,5,'院级三等奖',0.20,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(53,2,'身心素养-院级-优秀奖',0,5,'院级优秀奖',0.10,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_AWARD',1.000,'QUALITY','BODYMIND'),(69,2,'身心素养-基础减分（每次）',1,5,'基础扣分',1.50,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','QUALITY_BODYMIND_DEDUCT',1.000,'QUALITY','BODYMIND'),(70,2,'身心素养-代表学院参加大型文体活动（每项）',0,5,'活动',0.40,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_BODYMIND_ACTIVITY',1.000,'QUALITY','BODYMIND'),(71,2,'审美人文-文化活动-国家级-一等奖',0,6,'国家级一等奖',1.00,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(72,2,'审美人文-文化活动-国家级-二等奖',0,6,'国家级二等奖',0.90,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(73,2,'审美人文-文化活动-国家级-三等奖',0,6,'国家级三等奖',0.80,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(74,2,'审美人文-文化活动-国家级-优秀奖',0,6,'国家级优秀奖',0.70,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(75,2,'审美人文-文化活动-省级-一等奖',0,6,'省级一等奖',0.80,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(76,2,'审美人文-文化活动-省级-二等奖',0,6,'省级二等奖',0.70,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(77,2,'审美人文-文化活动-省级-三等奖',0,6,'省级三等奖',0.50,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(78,2,'审美人文-文化活动-省级-优秀奖',0,6,'省级优秀奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(79,2,'审美人文-文化活动-市校级-一等奖',0,6,'市校级一等奖',0.60,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(80,2,'审美人文-文化活动-市校级-二等奖',0,6,'市校级二等奖',0.50,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(81,2,'审美人文-文化活动-市校级-三等奖',0,6,'市校级三等奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(82,2,'审美人文-文化活动-市校级-优秀奖',0,6,'市校级优秀奖',0.20,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(83,2,'审美人文-文化活动-院级-一等奖',0,6,'院级一等奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(84,2,'审美人文-文化活动-院级-二等奖',0,6,'院级二等奖',0.30,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(85,2,'审美人文-文化活动-院级-三等奖',0,6,'院级三等奖',0.20,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(86,2,'审美人文-文化活动-院级-优秀奖',0,6,'院级优秀奖',0.10,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_AWARD',1.000,'QUALITY','ART'),(102,2,'审美人文-第二课堂项目负责人（每项）',0,6,'第二课堂',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_SECOND_CLASS',1.000,'QUALITY','ART'),(103,2,'审美人文-第二课堂普通成员（每项）',0,6,'第二课堂',1.00,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_SECOND_CLASS',1.000,'QUALITY','ART'),(104,2,'审美人文-国家/省级官网发表新闻（每篇）',0,6,'宣传报道',0.60,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(105,2,'审美人文-校外地市官方媒体/报纸/校报（每篇）',0,6,'宣传报道',0.40,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(106,2,'审美人文-校电台/电视台/校报/理工视窗等（每篇）',0,6,'宣传报道',0.20,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(107,2,'审美人文-学院网站/学院公众号发表新闻（每篇）',0,6,'宣传报道',0.10,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(108,2,'审美人文-图片/条幅制作并采用（每人）',0,6,'宣传报道',0.10,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(109,2,'审美人文-网站/视频/PPT制作并采用（每人）',0,6,'宣传报道',0.10,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(110,2,'审美人文-公寓黑板报通报表扬（每次/人）',0,6,'宣传报道',0.10,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_MEDIA',1.000,'QUALITY','MEDIA'),(111,2,'审美人文-晚会主持（每次）',0,6,'文艺活动',0.30,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_ACTIVITY',1.000,'QUALITY','ACTIVITY'),(112,2,'审美人文-活动礼仪（每次）',0,6,'文艺活动',0.20,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_ACTIVITY',1.000,'QUALITY','ACTIVITY'),(113,2,'审美人文-节目出演（每次）',0,6,'文艺活动',0.30,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_ACTIVITY',1.000,'QUALITY','ACTIVITY'),(114,2,'审美人文-代表学院参加大型文体活动（每项）',0,6,'文艺活动',0.40,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_ART_ACTIVITY',1.000,'QUALITY','ACTIVITY'),(115,2,'劳动素养-实践志愿-国家级-一等奖',0,7,'国家级一等奖',1.00,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(116,2,'劳动素养-实践志愿-国家级-二等奖',0,7,'国家级二等奖',0.90,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(117,2,'劳动素养-实践志愿-国家级-三等奖',0,7,'国家级三等奖',0.80,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(118,2,'劳动素养-实践志愿-国家级-优秀奖',0,7,'国家级优秀奖',0.70,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(119,2,'劳动素养-实践志愿-省级-一等奖',0,7,'省级一等奖',0.80,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(120,2,'劳动素养-实践志愿-省级-二等奖',0,7,'省级二等奖',0.70,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(121,2,'劳动素养-实践志愿-省级-三等奖',0,7,'省级三等奖',0.50,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(122,2,'劳动素养-实践志愿-省级-优秀奖',0,7,'省级优秀奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(123,2,'劳动素养-实践志愿-市校级-一等奖',0,7,'市校级一等奖',0.60,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(124,2,'劳动素养-实践志愿-市校级-二等奖',0,7,'市校级二等奖',0.50,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(125,2,'劳动素养-实践志愿-市校级-三等奖',0,7,'市校级三等奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(126,2,'劳动素养-实践志愿-市校级-优秀奖',0,7,'市校级优秀奖',0.20,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(127,2,'劳动素养-实践志愿-院级-一等奖',0,7,'院级一等奖',0.40,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(128,2,'劳动素养-实践志愿-院级-二等奖',0,7,'院级二等奖',0.30,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(129,2,'劳动素养-实践志愿-院级-三等奖',0,7,'院级三等奖',0.20,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(130,2,'劳动素养-实践志愿-院级-优秀奖',0,7,'院级优秀奖',0.10,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',1.000,'QUALITY','LABOR'),(146,2,'劳动素养-虐待动物（每次）',1,7,'违纪',1.50,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(147,2,'劳动素养-擅自夜不归宿/瞒报（每次）',1,7,'违纪',1.50,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(148,2,'劳动素养-私自破坏逃生窗或公物（每次）',1,7,'违纪',2.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(149,2,'劳动素养-宿舍影响集体休息（每次）',1,7,'违纪',0.50,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(150,2,'劳动素养-宿舍不按时熄灯（每次）',1,7,'违纪',0.20,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','SUB','QUALITY_LABOR_DEDUCT',1.000,'QUALITY','LABOR'),(151,2,'劳动素养-英语四级',0,7,'过级',0.40,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LANGUAGE',1.000,'QUALITY','LANGUAGE'),(152,2,'劳动素养-英语六级',0,7,'过级',0.60,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LANGUAGE',1.000,'QUALITY','LANGUAGE'),(153,2,'劳动素养-集体项目队员折算（按获奖分值0.5）',0,7,'折算',1.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_LABOR_AWARD',0.500,'QUALITY','LABOR'),(154,2,'创新素养-基础性评价（竞赛/科研/专利/论文/双创）',0,8,'基础',6.00,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_INNOVATION_BASE',1.000,'QUALITY','INNOVATION'),(155,2,'创新素养-发展性评价（标志性科研成果/A+竞赛）',0,8,'发展',2.00,1,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_INNOVATION_DEV',1.000,'QUALITY','INNOVATION'),(156,2,'创新素养-论文发表第一作者（篇）',0,8,'论文',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_INNOVATION_PAPER',1.000,'QUALITY','PAPER'),(157,2,'创新素养-论文发表第二作者（按1/2）',0,8,'论文',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_INNOVATION_PAPER',0.500,'QUALITY','PAPER'),(158,2,'创新素养-论文发表第三作者（按1/4）',0,8,'论文',1.50,0,1,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_INNOVATION_PAPER',0.250,'QUALITY','PAPER'),(159,2,'创新素养-论文发表第四作者及以后不加分（说明项）',0,8,'论文规则',0.00,0,0,1,'2026-03-27 19:27:01','2026-03-27 19:27:01','ADD','QUALITY_INNOVATION_PAPER',0.000,'QUALITY','PAPER');
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='规则分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_rule_item_category`
--

LOCK TABLES `evaluation_rule_item_category` WRITE;
/*!40000 ALTER TABLE `evaluation_rule_item_category` DISABLE KEYS */;
INSERT INTO `evaluation_rule_item_category` VALUES (1,1,'test',0,'2026-03-17 11:35:16','2026-03-17 11:35:18'),(2,2,'德育评价',0,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(3,2,'学业水平评价',0,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(4,2,'素质能力评价',0,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(5,2,'身心素养',4,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(6,2,'审美和人文素养',4,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(7,2,'劳动素养',4,'2026-03-27 19:27:01','2026-03-27 19:27:01'),(8,2,'创新素养',4,'2026-03-27 19:27:01','2026-03-27 19:27:01');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生学业水平智育成绩表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_student_academic_score`
--

LOCK TABLES `evaluation_student_academic_score` WRITE;
/*!40000 ALTER TABLE `evaluation_student_academic_score` DISABLE KEYS */;
/*!40000 ALTER TABLE `evaluation_student_academic_score` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_operation_log`
--

LOCK TABLES `sys_operation_log` WRITE;
/*!40000 ALTER TABLE `sys_operation_log` DISABLE KEYS */;
INSERT INTO `sys_operation_log` VALUES (1,1,'POST /sys-student','[{\"classId\":2,\"collegeId\":1,\"password\":\"***\",\"realName\":\"lr\",\"status\":1,\"studentId\":\"123\"}]','0:0:0:0:0:0:0:1','2026-03-26 20:30:31'),(2,1,'POST /sys-college','新增学院：collegeName=纺织学院，status=1','0:0:0:0:0:0:0:1','2026-03-26 20:35:25'),(3,1,'POST /sys-class','新增班级：className=软件2304，collegeId=1','127.0.0.1','2026-03-26 20:38:41'),(4,1,'POST /sys-college','新增学院：collegeName=**，status=1','127.0.0.1','2026-03-26 21:29:32'),(5,1,'POST /sys-role/assign-permission','新增角色：roleId=2','127.0.0.1','2026-03-26 22:18:38'),(6,1,'PUT /user/teacher/5/classes','更新用户：5','127.0.0.1','2026-03-27 08:57:58'),(7,1,'POST /user/assign-role','新增用户：userId=5','127.0.0.1','2026-03-27 08:59:26'),(8,1,'PUT /user/teacher/5/classes','更新用户：5','127.0.0.1','2026-03-27 08:59:45'),(9,1,'POST /sys-student/import-excel','新增学生：filename=测试学生.xlsx','127.0.0.1','2026-03-27 09:13:56'),(10,1,'POST /user/import-teacher-excel','新增用户：filename=测试学生.xlsx','127.0.0.1','2026-03-27 09:25:00'),(11,1,'PUT /sys-college/8','更新学院：collegeName=生物与医药学院，status=1，8','127.0.0.1','2026-03-27 09:28:48'),(12,1,'POST /sys-role/assign-permission','新增角色：roleId=2','127.0.0.1','2026-03-27 16:37:15'),(13,1,'POST /sys-role/assign-permission','新增角色：roleId=2','127.0.0.1','2026-03-27 17:08:08'),(14,1,'POST /sys-role/assign-permission','新增角色：roleId=2','127.0.0.1','2026-03-27 21:31:05');
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
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES (1,'教师管理','sys:user:menu',100,1,'2026-03-16 16:20:34','2026-03-26 21:02:52'),(2,'角色管理','sys:role:menu',100,1,'2026-03-16 16:20:34','2026-03-26 05:23:10'),(3,'权限管理','sys:perm:menu',100,1,'2026-03-16 16:20:34','2026-03-26 05:23:10'),(4,'学生管理','sys:student:menu',100,1,'2026-03-16 16:20:34','2026-03-26 05:23:10'),(5,'规则分类管理','sys:category:menu',101,1,'2026-03-17 10:29:13','2026-03-26 05:23:10'),(6,'规则总览','sys:rule:menu',101,1,'2026-03-17 10:30:08','2026-03-26 05:23:10'),(7,'规则项管理','sys:item:menu',101,1,'2026-03-17 10:30:45','2026-03-26 05:23:10'),(16,'学院管理','sys:college:menu',101,1,'2026-03-21 13:18:05','2026-03-26 05:23:10'),(17,'班级管理','sys:class:menu',101,1,'2026-03-21 13:18:05','2026-03-26 05:23:10'),(18,'智育成绩管理','sys:academic:menu',101,1,'2026-03-27 21:27:03','2026-03-27 21:27:03'),(100,'系统管理','sys:group:system',0,1,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(101,'教学评估','sys:group:evaluation',0,1,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(102,'用户分配角色','sys:user:assign:menu',100,1,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(103,'角色分配菜单','sys:role:assign:menu',100,1,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(104,'综测申报审批','sys:approval:menu',101,1,'2026-03-27 19:44:58','2026-03-27 19:44:58'),(200,'操作日志','sys:log:menu',100,1,'2026-03-26 17:57:43','2026-03-26 17:57:43');
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
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` VALUES (17,3,1,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(18,3,2,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(19,3,3,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(20,3,4,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(24,1,4,'2026-03-16 16:21:56','2026-03-16 16:21:56'),(25,3,5,'2026-03-17 10:31:42','2026-03-17 10:31:43'),(26,3,6,'2026-03-17 10:31:49','2026-03-17 10:31:50'),(27,3,7,'2026-03-17 10:31:57','2026-03-17 10:31:59'),(32,3,16,'2026-03-21 13:18:05','2026-03-21 13:18:05'),(33,3,17,'2026-03-21 13:18:05','2026-03-21 13:18:05'),(34,3,100,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(35,3,101,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(36,3,102,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(37,3,103,'2026-03-26 05:23:10','2026-03-26 05:23:10'),(45,3,200,'2026-03-26 17:57:43','2026-03-26 17:57:43'),(61,3,104,'2026-03-27 19:44:59','2026-03-27 19:44:59'),(62,3,18,'2026-03-27 21:27:03','2026-03-27 21:27:03'),(64,2,4,'2026-03-27 21:31:05','2026-03-27 21:31:05'),(65,2,5,'2026-03-27 21:31:05','2026-03-27 21:31:05'),(66,2,6,'2026-03-27 21:31:05','2026-03-27 21:31:05'),(67,2,7,'2026-03-27 21:31:05','2026-03-27 21:31:05'),(68,2,18,'2026-03-27 21:31:05','2026-03-27 21:31:05'),(69,2,104,'2026-03-27 21:31:05','2026-03-27 21:31:05');
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
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','{bcrypt}$2a$10$ijC.UqIhDVWnFOFI9e8fPusbeffVvabJ279LQ1Etqvl4IwZedW0ge','admin',0,0,1,'2026-03-11 16:34:10','2026-03-14 16:03:11'),(2,'2023001','{bcrypt}$2a$10$ijC.UqIhDVWnFOFI9e8fPusbeffVvabJ279LQ1Etqvl4IwZedW0ge','张三',1,101,1,'2026-03-16 14:23:33','2026-03-26 13:28:19'),(3,'2023002','{bcrypt}$2a$10$ijC.UqIhDVWnFOFI9e8fPusbeffVvabJ279LQ1Etqvl4IwZedW0ge','李四',1,101,1,'2026-03-16 14:23:33','2026-03-17 11:35:40'),(4,'2023003','{bcrypt}$2a$10$ijC.UqIhDVWnFOFI9e8fPusbeffVvabJ279LQ1Etqvl4IwZedW0ge','管理员',2,NULL,1,'2026-03-16 14:23:33','2026-03-17 11:35:40'),(5,'23110506122','{bcrypt}$2a$10$BoncPzPOjuy45nWoIRyyXOPvWHpyN3UX5rfCYAmJyXhiM8W/MrEOq','sjk',1,1,1,'2026-03-26 13:28:09','2026-03-26 13:28:30'),(6,'123','{bcrypt}$2a$10$UZANPGdsFIRFBUFVlGeVHOoz/eiOmuhE3dM3N7xyEipI6./Nkykj2','lr',1,2,1,'2026-03-26 20:30:31','2026-03-26 20:30:31'),(7,'2023010001','{bcrypt}$2a$10$i7wGe9aOfJuOV0pyN5OeUujSM5B2QtAVi3FtzTPPCB19wowNwvoZm','张伟',16,16,1,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(8,'2023010002','{bcrypt}$2a$10$0q0TVfWN2FFiTgtGt09k4Ouxm/KfHJCAj.P9xP0kZNHf8/yJ5rcw2','李婷',13,17,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(9,'2023010003','{bcrypt}$2a$10$.INxU2lUlZBLj4W9rlPRXO352A/cxi8pa5BVo/zETHC9Lg7Xv2bW.','王刚',9,14,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(10,'2023010004','{bcrypt}$2a$10$cVNMyzJdR4Qe8FuZ8wg6wOgKvpjkamhtKxkGch0LHjl3OXxiQfapm','刘晓',15,12,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(11,'2023010005','{bcrypt}$2a$10$VRuCK5BdHE5eiYoon.EB1uptxzvLhauR7exASuv.4/DQsEl.c0FkO','陈浩',14,18,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(12,'2023010006','{bcrypt}$2a$10$gTyd8JGCIP6WZ1Tj0dDH7O98bHrh51I51VGuvrfwbWpEePQiAm6.q','赵丽',17,10,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(13,'2023010007','{bcrypt}$2a$10$9jADhgyph7STebxOnwRbK.a03F5o9TCtx./dKuPY9cQayK.uCQIjG','黄磊',10,9,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(14,'2023010008','{bcrypt}$2a$10$jgoXDhvrI0lWTakdh0Cv9uzgJoxVBC6iDCLZWAgTofIJ/HUs00fOO','周婷',11,11,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(15,'2023010009','{bcrypt}$2a$10$rwvGZ3B4KabL3/DVD5KuLOC0DF4F5AY6THoOeoYT706q5YrwugFjm','吴翔',12,13,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(16,'2023010010','{bcrypt}$2a$10$5MfCyLQ/XspkiS1AkHWreugyjLYL2Z8jqedNTTv0ZdWeBSnBjs3Wa','王丽',8,15,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(17,'10001','{bcrypt}$2a$10$qfYg1Kff119dyj6sIHoeMunBrqkX8ct6XqVaVkuPPmCZCWIDuoj/e','张伟',16,NULL,1,'2026-03-27 09:24:59','2026-03-27 09:24:59'),(18,'10002','{bcrypt}$2a$10$Psam4Df.doHw7aOQMHWIcesFOLb26w8oAnR9V7PPlblVUvG0tToNW','李婷',13,NULL,1,'2026-03-27 09:24:59','2026-03-27 09:24:59'),(19,'10003','{bcrypt}$2a$10$oMeN7bprHBvOCPGmnQq7FO2Phn.jP50shDBUd6AmFVk510ePSi7IO','王刚',9,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(20,'10004','{bcrypt}$2a$10$/e5n.dlRVZl/POHxVx0EW.fFT6YLsHd15Jx1nUcsFrx3MlHcfLJUa','刘晓',15,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(21,'10005','{bcrypt}$2a$10$EMr8rmKrxNKaLoqd1WQpZ.HCYGP3Kb1m648HNeaLoUx2bX5TMKFY.','陈浩',14,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(22,'10006','{bcrypt}$2a$10$lk6HAXQm9MFk/Hg7xJB2W.CQwTFxHdy9q/SaK1BpzwJhfZ53ZG4bm','赵丽',17,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(23,'10007','{bcrypt}$2a$10$BoXWfaPx9PNblTAp5BSQuOWbWGbQwRXWKH8gyM3mR/XwO2E4FMLEq','黄磊',10,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(24,'10008','{bcrypt}$2a$10$OR75wCCWFd0bnimstgZma.HwfO7lAiiBe7ack8044SlKhU3JgVdI2','周婷',11,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(25,'10009','{bcrypt}$2a$10$IKpBAbJrYUxb5mfIbzVeeufR7omMovJtPCdocpSjkiQwyvz6XrHvS','吴翔',12,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(26,'10010','{bcrypt}$2a$10$eMY5xVYZYi3pcXOiyV4zZ.FAApTiRv41rewMYUbsKx/QFoVoFtI3K','王丽',8,NULL,1,'2026-03-27 09:25:00','2026-03-27 09:25:00');
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
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1,3,'2026-03-16 14:23:34','2026-03-16 14:34:42'),(2,2,1,'2026-03-16 14:23:34','2026-03-16 14:34:42'),(3,3,2,'2026-03-16 14:23:34','2026-03-16 14:34:42'),(4,4,3,'2026-03-16 14:34:49','2026-03-16 14:34:50'),(8,6,1,'2026-03-26 20:30:31','2026-03-26 20:30:31'),(9,5,2,'2026-03-27 08:59:26','2026-03-27 08:59:26'),(10,7,1,'2026-03-27 09:13:55','2026-03-27 09:13:55'),(11,8,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(12,9,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(13,10,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(14,11,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(15,12,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(16,13,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(17,14,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(18,15,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(19,16,1,'2026-03-27 09:13:56','2026-03-27 09:13:56'),(20,17,2,'2026-03-27 09:24:59','2026-03-27 09:24:59'),(21,18,2,'2026-03-27 09:24:59','2026-03-27 09:24:59'),(22,19,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(23,20,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(24,21,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(25,22,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(26,23,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(27,24,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(28,25,2,'2026-03-27 09:25:00','2026-03-27 09:25:00'),(29,26,2,'2026-03-27 09:25:00','2026-03-27 09:25:00');
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

-- Dump completed on 2026-03-27 22:45:55
