-- 在规则分类 id = 31 下增加「与专业密切相关的论文、专著」类细则（手册：1.5 分/篇，按作者位次折算）。
-- 指标名前缀：论文专利-
-- 基础分统一 1.50，第一作者 coeff 1、第二 0.5、第三 0.25；第四作者及以后不加分单独说明项。
-- rule_id 从分类 31 读取。dedupe_group 与种子中论文项一致：QUALITY_INNOVATION_PAPER。
-- 幂等：同一 rule_id + item_category(31) + item_name 已存在则跳过。

START TRANSACTION;

DROP TEMPORARY TABLE IF EXISTS tmp_paper_patent_cat31;

CREATE TEMPORARY TABLE tmp_paper_patent_cat31 (
    subtype VARCHAR(120),
    author_rank VARCHAR(40),
    coeff DECIMAL(8, 3)
);

INSERT INTO tmp_paper_patent_cat31 (subtype, author_rank, coeff) VALUES
('北大核心期刊', '第一作者', 1.000),
('北大核心期刊', '第二作者', 0.500),
('北大核心期刊', '第三作者', 0.250),
('SCI收录期刊论文', '第一作者', 1.000),
('SCI收录期刊论文', '第二作者', 0.500),
('SCI收录期刊论文', '第三作者', 0.250),
('EI期刊论文（与专业密切相关）', '第一作者', 1.000),
('EI期刊论文（与专业密切相关）', '第二作者', 0.500),
('EI期刊论文（与专业密切相关）', '第三作者', 0.250),
('CCF推荐A/B/C类会议论文', '第一作者', 1.000),
('CCF推荐A/B/C类会议论文', '第二作者', 0.500),
('CCF推荐A/B/C类会议论文', '第三作者', 0.250),
('EI国际会议论文（作报告）', '第一作者', 1.000),
('EI国际会议论文（作报告）', '第二作者', 0.500),
('EI国际会议论文（作报告）', '第三作者', 0.250);

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
    CONCAT('论文专利-', t.subtype, '-', t.author_rank),
    0,
    31,
    '论文',
    1.50,
    0,
    1,
    1,
    'ADD',
    'QUALITY_INNOVATION_PAPER',
    t.coeff,
    'QUALITY',
    'PAPER',
    NOW(),
    NOW()
FROM tmp_paper_patent_cat31 t
INNER JOIN evaluation_rule_item_category c ON c.id = 31
WHERE NOT EXISTS (
        SELECT 1
        FROM evaluation_rule_item ri
        WHERE ri.rule_id = c.rule_id
          AND ri.item_category = 31
          AND ri.item_name = CONCAT('论文专利-', t.subtype, '-', t.author_rank)
    );

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
    '论文专利-第四作者及以后不加分（说明项）',
    0,
    31,
    '说明',
    0.00,
    0,
    0,
    1,
    'ADD',
    'QUALITY_INNOVATION_PAPER',
    0.000,
    'QUALITY',
    'PAPER',
    NOW(),
    NOW()
FROM evaluation_rule_item_category c
WHERE c.id = 31
  AND NOT EXISTS (
        SELECT 1
        FROM evaluation_rule_item ri
        WHERE ri.rule_id = c.rule_id
          AND ri.item_category = 31
          AND ri.item_name = '论文专利-第四作者及以后不加分（说明项）'
    );

DROP TEMPORARY TABLE IF EXISTS tmp_paper_patent_cat31;

COMMIT;
