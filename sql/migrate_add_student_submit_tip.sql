-- 学生端综测申报提示（教师/管理员可动态维护）
CREATE TABLE IF NOT EXISTS evaluation_student_submit_tip (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    period_id     BIGINT       NOT NULL COMMENT '综测周期ID',
    section_code  VARCHAR(64)  NOT NULL COMMENT 'moral/academic/quality_bodymind/quality_art/quality_labor/quality_innovation',
    title         VARCHAR(200) NOT NULL COMMENT '提示标题',
    content       VARCHAR(4000) NOT NULL COMMENT '提示正文',
    sort_order    INT          NOT NULL DEFAULT 0 COMMENT '同分区内排序',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1=启用 0=停用',
    operator_user_id INT       NULL COMMENT '最后操作人',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_tip_period_section (period_id, section_code),
    KEY idx_tip_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生综测申报动态提示';

-- 菜单权限：学生申报提示维护
INSERT INTO sys_permission (perm_name, perm_code, parent_id, status, create_time, update_time)
SELECT '学生申报提示维护', 'sys:submit-tip:menu', 101, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'sys:submit-tip:menu');

-- 角色授权：管理员 + 教师
INSERT INTO sys_role_permission (role_id, perm_id, create_time, update_time)
SELECT r.id, p.id, NOW(), NOW()
FROM sys_permission p
JOIN sys_role r ON r.role_code IN ('ADMIN', 'TEACHER')
WHERE p.perm_code = 'sys:submit-tip:menu'
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_permission rp
      WHERE rp.role_id = r.id AND rp.perm_id = p.id
  );
