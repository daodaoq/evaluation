-- 移除 evaluation_rule 表中未再使用的德育/学业/素质权重列（综测合成权重由策略表等其它机制维护）
-- 请在测试库验证后执行；若列已不存在可手工跳过对应语句。

ALTER TABLE evaluation_rule
    DROP COLUMN moral_weight,
    DROP COLUMN academic_weight,
    DROP COLUMN quality_weight;
