-- 在规则分类 id = 31（如「创新素养-发展性评价」）下，按奖项等级批量插入细则；
-- 指标名格式：A+竞赛-{级别}-{等级}，分值与种子脚本 tmp_award_score 一致。
-- rule_id 从该分类行读取，无需手写。
-- 幂等：同一 rule_id + item_category(31) + item_name 已存在则跳过。
--
-- 执行前请确认存在 evaluation_rule_item_category.id = 31。

START TRANSACTION;

DROP TEMPORARY TABLE IF EXISTS tmp_award_a_plus;

CREATE TEMPORARY TABLE tmp_award_a_plus (
    lv VARCHAR(20),
    grade VARCHAR(20),
    score DECIMAL(6, 2)
);

INSERT INTO tmp_award_a_plus (lv, grade, score) VALUES
('国家级', '一等奖', 1.00), ('国家级', '二等奖', 0.90), ('国家级', '三等奖', 0.80), ('国家级', '优秀奖', 0.70),
('省级',   '一等奖', 0.80), ('省级',   '二等奖', 0.70), ('省级',   '三等奖', 0.50), ('省级',   '优秀奖', 0.40),
('市校级', '一等奖', 0.60), ('市校级', '二等奖', 0.50), ('市校级', '三等奖', 0.40), ('市校级', '优秀奖', 0.20),
('院级',   '一等奖', 0.40), ('院级',   '二等奖', 0.30), ('院级',   '三等奖', 0.20), ('院级',   '优秀奖', 0.10);

INSERT INTO evaluation_rule_item (
    rule_id,
    item_name,
    item_type,
    item_category,
    level,
    base_score,
    is_competition,
    need_material,
    status,
    score_mode,
    dedupe_group,
    coeff,
    module_code,
    submodule_code,
    create_time,
    update_time
)
SELECT
    c.rule_id,
    CONCAT('A+竞赛-', t.lv, '-', t.grade),
    0,
    31,
    CONCAT(t.lv, t.grade),
    t.score,
    1,
    1,
    1,
    'ADD',
    'QUALITY_INNOVATION_DEV',
    1.000,
    'QUALITY',
    'INNOVATION',
    NOW(),
    NOW()
FROM tmp_award_a_plus t
INNER JOIN evaluation_rule_item_category c ON c.id = 31
WHERE NOT EXISTS (
        SELECT 1
        FROM evaluation_rule_item ri
        WHERE ri.rule_id = c.rule_id
          AND ri.item_category = 31
          AND ri.item_name = CONCAT('A+竞赛-', t.lv, '-', t.grade)
    );

DROP TEMPORARY TABLE IF EXISTS tmp_award_a_plus;

COMMIT;
