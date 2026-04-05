-- 规则分类：基础分（计入该分类节点，与子分类汇总）
ALTER TABLE evaluation_rule_item_category
  ADD COLUMN category_base_score DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '分类基础分' AFTER sort_order;
