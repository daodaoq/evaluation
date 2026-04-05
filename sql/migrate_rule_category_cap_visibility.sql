-- 规则分类：分数上限、学生端可见性、排序；修正 parent_id 与 id 一致用 BIGINT
-- 执行前请备份。若列已存在请跳过对应语句。

ALTER TABLE evaluation_rule_item_category
  MODIFY COLUMN parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父级分类id';

ALTER TABLE evaluation_rule_item_category
  ADD COLUMN score_cap DECIMAL(10,2) NULL DEFAULT NULL COMMENT '该分类子树内加分合计上限，NULL 不限制' AFTER parent_id,
  ADD COLUMN student_visible TINYINT NOT NULL DEFAULT 1 COMMENT '1=学生端展示；0=隐藏（子树一并隐藏）' AFTER score_cap,
  ADD COLUMN sort_order INT NOT NULL DEFAULT 0 COMMENT '同级排序，越小越靠前' AFTER student_visible;
