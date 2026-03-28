-- 综测流程与时间：阶段截止、归档、公示、异议、学生确认留痕、事件日志
-- 执行前请备份。若列已存在会报错，可跳过对应 ALTER。

ALTER TABLE evaluation_period
    ADD COLUMN archived TINYINT NOT NULL DEFAULT 0 COMMENT '1=归档锁定（不可改分/申报/审批等）' AFTER status,
    ADD COLUMN application_start_time DATETIME NULL COMMENT '申报开放起（空则用 start_time）' AFTER archived,
    ADD COLUMN application_end_time DATETIME NULL COMMENT '申报截止（空则用 end_time）' AFTER application_start_time,
    ADD COLUMN review_end_time DATETIME NULL COMMENT '教师审核截止（空则不限制）' AFTER application_end_time,
    ADD COLUMN public_notice_start DATETIME NULL COMMENT '公示开始' AFTER review_end_time,
    ADD COLUMN public_notice_end DATETIME NULL COMMENT '公示结束' AFTER public_notice_start,
    ADD COLUMN objection_end_time DATETIME NULL COMMENT '异议截止（空则同公示结束）' AFTER public_notice_end;

CREATE TABLE IF NOT EXISTS evaluation_objection (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    period_id       BIGINT       NOT NULL COMMENT '综测周期ID',
    student_user_id BIGINT       NOT NULL COMMENT '学生用户ID',
    class_id        INT          NULL COMMENT '班级ID快照',
    content         VARCHAR(2000) NOT NULL COMMENT '异议内容',
    status          VARCHAR(32)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/HANDLED/REJECTED',
    handler_user_id BIGINT       NULL COMMENT '处理人用户ID',
    handler_remark  VARCHAR(500) NULL COMMENT '处理说明',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_obj_period (period_id),
    KEY idx_obj_student (student_user_id),
    KEY idx_obj_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测异议（公示相关）';

CREATE TABLE IF NOT EXISTS evaluation_student_period_confirm (
    id                BIGINT   NOT NULL AUTO_INCREMENT,
    student_user_id   BIGINT   NOT NULL,
    period_id         BIGINT   NOT NULL,
    create_time       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_period (student_user_id, period_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生确认无异议（锁定不可再申报）';

CREATE TABLE IF NOT EXISTS evaluation_period_event_log (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    period_id        BIGINT       NOT NULL,
    operator_user_id INT          NOT NULL COMMENT '操作人用户ID',
    event_code       VARCHAR(64)  NOT NULL COMMENT '事件编码',
    detail           VARCHAR(1000) NULL,
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_pev_period (period_id),
    KEY idx_pev_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='综测周期关键操作留痕';

INSERT INTO sys_permission (perm_name, perm_code, parent_id, status, create_time, update_time)
SELECT '综测流程与时间', 'sys:period:flow:menu', 101, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'sys:period:flow:menu');

INSERT INTO sys_role_permission (role_id, perm_id, create_time, update_time)
SELECT r.id, p.id, NOW(), NOW()
FROM sys_permission p
JOIN sys_role r ON r.role_code = 'ADMIN'
WHERE p.perm_code = 'sys:period:flow:menu'
  AND NOT EXISTS (SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.perm_id = p.id);

INSERT INTO sys_permission (perm_name, perm_code, parent_id, status, create_time, update_time)
SELECT '综测异议处理', 'sys:objection:menu', 101, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'sys:objection:menu');

INSERT INTO sys_role_permission (role_id, perm_id, create_time, update_time)
SELECT r.id, p.id, NOW(), NOW()
FROM sys_permission p
JOIN sys_role r ON r.role_code IN ('ADMIN', 'TEACHER')
WHERE p.perm_code = 'sys:objection:menu'
  AND NOT EXISTS (SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.perm_id = p.id);
