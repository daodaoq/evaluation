-- 综测细则（2025版）对齐建议 SQL（后台端）
-- 说明：
-- 1) 本脚本用于补齐“细则管理 + 审批 + 公示复核”能力；
-- 2) 执行前请先在测试库验证；
-- 3) 部分字段采用 IF NOT EXISTS 语法，MySQL 8.0+ 推荐。

START TRANSACTION;

-- 1) 周期时间字段改为 datetime（原 varchar 不利于范围过滤）
ALTER TABLE evaluation_period
    MODIFY COLUMN start_time DATETIME NOT NULL COMMENT '开始时间',
    MODIFY COLUMN end_time   DATETIME NOT NULL COMMENT '结束时间';

-- 2) 规则项补充“同类不重复取最高”“互斥组”“次数折算”等细则控制字段
ALTER TABLE evaluation_rule_item
    ADD COLUMN IF NOT EXISTS score_mode VARCHAR(32) NOT NULL DEFAULT 'ADD' COMMENT '计分方式：ADD/SUB/MAX_ONLY',
    ADD COLUMN IF NOT EXISTS dedupe_group VARCHAR(64) DEFAULT NULL COMMENT '同类去重组（同组取最高）',
    ADD COLUMN IF NOT EXISTS coeff DECIMAL(6,3) NOT NULL DEFAULT 1.000 COMMENT '系数（如第二职务*0.5）',
    ADD COLUMN IF NOT EXISTS module_code VARCHAR(32) DEFAULT NULL COMMENT '模块编码：MORAL/ACADEMIC/QUALITY',
    ADD COLUMN IF NOT EXISTS submodule_code VARCHAR(32) DEFAULT NULL COMMENT '子模块编码';

-- 3) 申报项补充证据元信息（便于防重复、复核与追溯）
ALTER TABLE evaluation_apply_item
    ADD COLUMN IF NOT EXISTS evidence_no VARCHAR(128) DEFAULT NULL COMMENT '证书/文件编号',
    ADD COLUMN IF NOT EXISTS award_level VARCHAR(64) DEFAULT NULL COMMENT '奖项级别',
    ADD COLUMN IF NOT EXISTS award_grade VARCHAR(64) DEFAULT NULL COMMENT '奖项等级',
    ADD COLUMN IF NOT EXISTS occurred_time DATETIME DEFAULT NULL COMMENT '事件发生时间',
    ADD COLUMN IF NOT EXISTS remark VARCHAR(255) DEFAULT NULL COMMENT '申报备注';

-- 4) 结果表补充分项得分（便于德育/学业/素质展示与备案）
ALTER TABLE evaluation_result
    ADD COLUMN IF NOT EXISTS moral_score DECIMAL(6,2) NOT NULL DEFAULT 0.00 COMMENT '德育分',
    ADD COLUMN IF NOT EXISTS academic_score DECIMAL(6,2) NOT NULL DEFAULT 0.00 COMMENT '学业分',
    ADD COLUMN IF NOT EXISTS quality_score DECIMAL(6,2) NOT NULL DEFAULT 0.00 COMMENT '素质能力分';

-- 5) 公示与异议（细则第十四、十五条）
CREATE TABLE IF NOT EXISTS evaluation_publicity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公示ID',
    period_id BIGINT NOT NULL COMMENT '综测周期ID',
    class_id BIGINT DEFAULT NULL COMMENT '班级ID（可空表示全院）',
    start_time DATETIME NOT NULL COMMENT '公示开始时间',
    end_time DATETIME NOT NULL COMMENT '公示结束时间',
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN' COMMENT '状态 OPEN/CLOSED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='综测结果公示表';

CREATE TABLE IF NOT EXISTS evaluation_objection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '异议ID',
    publicity_id BIGINT NOT NULL COMMENT '公示ID',
    student_id BIGINT NOT NULL COMMENT '提出异议学生ID',
    target_student_id BIGINT NOT NULL COMMENT '异议目标学生ID',
    reason VARCHAR(500) NOT NULL COMMENT '异议理由',
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态 PENDING/ACCEPTED/REJECTED',
    reply VARCHAR(500) DEFAULT NULL COMMENT '处理回复',
    reviewer_id BIGINT DEFAULT NULL COMMENT '处理人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='综测异议与复核表';

-- 6) 常用索引
CREATE INDEX IF NOT EXISTS idx_apply_student_period ON evaluation_apply(student_id, period_id);
CREATE INDEX IF NOT EXISTS idx_apply_item_apply_status ON evaluation_apply_item(apply_id, status);
CREATE INDEX IF NOT EXISTS idx_audit_apply_item ON evaluation_audit_record(apply_item_id);
CREATE INDEX IF NOT EXISTS idx_result_student_period ON evaluation_result(student_id, period_id);

COMMIT;
