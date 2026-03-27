-- 教师与负责班级的多对多关系（一名教师可管理多个班级）
CREATE TABLE IF NOT EXISTS `sys_teacher_class` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `teacher_user_id` int NOT NULL COMMENT '教师用户ID（sys_user.id）',
  `class_id` int NOT NULL COMMENT '班级ID（sys_class.id）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_class` (`teacher_user_id`, `class_id`),
  KEY `idx_teacher_user_id` (`teacher_user_id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='教师负责班级';
