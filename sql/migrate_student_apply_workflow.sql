-- 学生申报流程改造（支持：仅细则项 或 非细则项+材料+备注）
-- 执行前请先在测试库验证。

START TRANSACTION;

-- 1) evaluation_apply_item：支持非细则项（rule_item_id 可空）+ 来源类型 + 备注
ALTER TABLE evaluation_apply_item
    MODIFY COLUMN rule_item_id BIGINT NULL COMMENT '规则项ID（非细则项可为空）';

ALTER TABLE evaluation_apply_item
    ADD COLUMN IF NOT EXISTS source_type VARCHAR(16) NOT NULL DEFAULT 'RULE' COMMENT '来源类型：RULE/CUSTOM',
    ADD COLUMN IF NOT EXISTS custom_name VARCHAR(200) DEFAULT NULL COMMENT '非细则项名称',
    ADD COLUMN IF NOT EXISTS remark VARCHAR(255) DEFAULT NULL COMMENT '备注说明';

-- 2) 建议约束：同一申报单内，同一细则项只允许一次（custom 不受此约束）
CREATE UNIQUE INDEX IF NOT EXISTS uk_apply_rule_item
    ON evaluation_apply_item (apply_id, rule_item_id);

-- 3) 常用查询索引
CREATE INDEX IF NOT EXISTS idx_apply_item_source_status
    ON evaluation_apply_item (source_type, status);

CREATE INDEX IF NOT EXISTS idx_apply_student_period_status
    ON evaluation_apply (student_id, period_id, status);

COMMIT;
