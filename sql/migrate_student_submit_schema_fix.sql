-- 学生端申报/查询兜底修复脚本
-- 用于修复：Unknown column ai.source_type / ri.score_mode 等报错
-- 执行后可兼容当前 student-submit-web 与 StudentApplyMapper

START TRANSACTION;

-- A) 学生申报项表字段（evaluation_apply_item）
ALTER TABLE evaluation_apply_item
    MODIFY COLUMN rule_item_id BIGINT NULL COMMENT '规则项ID（非细则项可为空）';

ALTER TABLE evaluation_apply_item
    ADD COLUMN IF NOT EXISTS source_type VARCHAR(16) NOT NULL DEFAULT 'RULE' COMMENT '来源类型：RULE/CUSTOM',
    ADD COLUMN IF NOT EXISTS custom_name VARCHAR(200) DEFAULT NULL COMMENT '非细则项名称',
    ADD COLUMN IF NOT EXISTS remark VARCHAR(255) DEFAULT NULL COMMENT '备注说明',
    ADD COLUMN IF NOT EXISTS evidence_no VARCHAR(128) DEFAULT NULL COMMENT '证书/文件编号',
    ADD COLUMN IF NOT EXISTS award_level VARCHAR(64) DEFAULT NULL COMMENT '奖项级别',
    ADD COLUMN IF NOT EXISTS award_grade VARCHAR(64) DEFAULT NULL COMMENT '奖项等级',
    ADD COLUMN IF NOT EXISTS occurred_time DATETIME DEFAULT NULL COMMENT '事件发生时间';

-- B) 规则项扩展字段（evaluation_rule_item）
ALTER TABLE evaluation_rule_item
    ADD COLUMN IF NOT EXISTS score_mode VARCHAR(32) NOT NULL DEFAULT 'ADD' COMMENT '计分方式：ADD/SUB/MAX_ONLY',
    ADD COLUMN IF NOT EXISTS dedupe_group VARCHAR(64) DEFAULT NULL COMMENT '同类去重组',
    ADD COLUMN IF NOT EXISTS coeff DECIMAL(6,3) NOT NULL DEFAULT 1.000 COMMENT '系数',
    ADD COLUMN IF NOT EXISTS module_code VARCHAR(32) DEFAULT NULL COMMENT '模块编码：MORAL/ACADEMIC/QUALITY',
    ADD COLUMN IF NOT EXISTS submodule_code VARCHAR(32) DEFAULT NULL COMMENT '子模块编码';

-- C) 常用索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_apply_rule_item
    ON evaluation_apply_item (apply_id, rule_item_id);

CREATE INDEX IF NOT EXISTS idx_apply_item_source_status
    ON evaluation_apply_item (source_type, status);

CREATE INDEX IF NOT EXISTS idx_apply_student_period_status
    ON evaluation_apply (student_id, period_id, status);

COMMIT;

