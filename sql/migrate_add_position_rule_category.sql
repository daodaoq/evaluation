-- 为每条规则总览补充根级「任职」分类（任职分自填归集锚点）。
-- 若该规则下已有名为「任职」或「任职分」的分类则跳过（与 StudentApplyServiceImpl.ensurePositionRuleCategoryExists 一致）。

INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
SELECT r.id, '任职', 0, NULL, 1, -50, 0, NOW(), NOW()
FROM evaluation_rule r
WHERE NOT EXISTS (
  SELECT 1 FROM evaluation_rule_item_category c
  WHERE c.rule_id = r.id
    AND TRIM(IFNULL(c.category_name, '')) IN ('任职', '任职分')
);
