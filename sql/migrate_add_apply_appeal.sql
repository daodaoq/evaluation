-- 学生对「已驳回」的申报项发起申诉；教师/管理员处理后，通过则退回待审
CREATE TABLE IF NOT EXISTS evaluation_apply_item_appeal (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '申诉ID',
  apply_item_id BIGINT NOT NULL COMMENT '申报项ID',
  student_id BIGINT NOT NULL COMMENT '学生用户ID（sys_user.id，冗余）',
  reason VARCHAR(1000) NOT NULL COMMENT '申诉理由',
  status VARCHAR(32) NOT NULL COMMENT 'PENDING/ACCEPTED/REJECTED',
  handler_id BIGINT DEFAULT NULL COMMENT '处理人用户ID',
  handler_remark VARCHAR(500) DEFAULT NULL COMMENT '处理说明',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_appeal_item (apply_item_id),
  KEY idx_appeal_status (status),
  KEY idx_appeal_student (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='申报项申诉';

INSERT INTO sys_permission (perm_name, perm_code, parent_id, status, create_time, update_time)
SELECT '申诉处理', 'sys:appeal:menu', 101, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM sys_permission WHERE perm_code = 'sys:appeal:menu'
);

INSERT INTO sys_role_permission (role_id, perm_id, create_time, update_time)
SELECT r.id, p.id, NOW(), NOW()
FROM sys_permission p
JOIN sys_role r ON r.role_code = 'ADMIN'
WHERE p.perm_code = 'sys:appeal:menu'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.perm_id = p.id
);

INSERT INTO sys_role_permission (role_id, perm_id, create_time, update_time)
SELECT r.id, p.id, NOW(), NOW()
FROM sys_permission p
JOIN sys_role r ON r.role_code = 'TEACHER'
WHERE p.perm_code = 'sys:appeal:menu'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.perm_id = p.id
);
