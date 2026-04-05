-- 为 rule_id = 3 下、分类 id 18～31（与截图红框一致）各增加两条细则：
--   · 其他加分（突发·每次0.1）  ADD 0.1
--   · 其他减分（突发·每次0.1）  SUB 0.1
-- 名称须与 ApplyScoreConstants.ADHOC_OTHER_* 及 StudentApplyMapper 白名单一致。
--
-- 若你库中 rule_id 或 id 与截图不一致，请先改 WHERE 条件再执行。
-- 幂等：同一 rule_id + item_category + item_name 已存在则跳过。

START TRANSACTION;

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
    '其他加分（突发·每次0.1）',
    0,
    c.id,
    '其他',
    0.10,
    0,
    0,
    1,
    'ADD',
    NULL,
    1.000,
    CASE
        WHEN c.category_name LIKE '%德育%' OR c.category_name LIKE '%任职%' THEN 'MORAL'
        WHEN c.category_name LIKE '%学业%' OR c.category_name LIKE '%智育%' THEN 'ACADEMIC'
        ELSE 'QUALITY'
    END,
    CASE
        WHEN c.category_name LIKE '%德育%' OR c.category_name LIKE '%任职%' THEN 'MISC'
        WHEN c.category_name LIKE '%学业%' OR c.category_name LIKE '%智育%' THEN 'RULE'
        WHEN c.category_name LIKE '%身心%' OR c.category_name LIKE '%体育%' OR c.category_name LIKE '%心理%' THEN 'BODYMIND'
        WHEN c.category_name LIKE '%审美%' OR c.category_name LIKE '%人文%' THEN 'ART'
        WHEN c.category_name LIKE '%劳动%' THEN 'LABOR'
        WHEN c.category_name LIKE '%创新%' THEN 'INNOVATION'
        WHEN c.category_name LIKE '%素质%' THEN 'INNOVATION'
        ELSE 'INNOVATION'
    END,
    NOW(),
    NOW()
FROM evaluation_rule_item_category c
WHERE c.rule_id = 3
  AND c.id BETWEEN 18 AND 31
  AND NOT EXISTS (
        SELECT 1
        FROM evaluation_rule_item ri
        WHERE ri.rule_id = c.rule_id
          AND ri.item_category = c.id
          AND ri.item_name = '其他加分（突发·每次0.1）'
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
    '其他减分（突发·每次0.1）',
    1,
    c.id,
    '其他',
    0.10,
    0,
    0,
    1,
    'SUB',
    NULL,
    1.000,
    CASE
        WHEN c.category_name LIKE '%德育%' OR c.category_name LIKE '%任职%' THEN 'MORAL'
        WHEN c.category_name LIKE '%学业%' OR c.category_name LIKE '%智育%' THEN 'ACADEMIC'
        ELSE 'QUALITY'
    END,
    CASE
        WHEN c.category_name LIKE '%德育%' OR c.category_name LIKE '%任职%' THEN 'MISC'
        WHEN c.category_name LIKE '%学业%' OR c.category_name LIKE '%智育%' THEN 'RULE'
        WHEN c.category_name LIKE '%身心%' OR c.category_name LIKE '%体育%' OR c.category_name LIKE '%心理%' THEN 'BODYMIND'
        WHEN c.category_name LIKE '%审美%' OR c.category_name LIKE '%人文%' THEN 'ART'
        WHEN c.category_name LIKE '%劳动%' THEN 'LABOR'
        WHEN c.category_name LIKE '%创新%' THEN 'INNOVATION'
        WHEN c.category_name LIKE '%素质%' THEN 'INNOVATION'
        ELSE 'INNOVATION'
    END,
    NOW(),
    NOW()
FROM evaluation_rule_item_category c
WHERE c.rule_id = 3
  AND c.id BETWEEN 18 AND 31
  AND NOT EXISTS (
        SELECT 1
        FROM evaluation_rule_item ri
        WHERE ri.rule_id = c.rule_id
          AND ri.item_category = c.id
          AND ri.item_name = '其他减分（突发·每次0.1）'
    );

COMMIT;
