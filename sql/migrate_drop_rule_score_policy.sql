-- 移除「计分策略配置」相关库表与菜单权限（与已删除的 RuleScorePolicyController / 管理端页面一致）
-- 计分逻辑仍使用 Java 内置 ScorePolicySnapshot.defaults()
-- 请在测试库验证后执行。

START TRANSACTION;

DELETE rp FROM sys_role_permission rp
INNER JOIN sys_permission p ON rp.perm_id = p.id
WHERE p.perm_code = 'sys:rule-score-policy:menu';

DELETE FROM sys_permission WHERE perm_code = 'sys:rule-score-policy:menu';

DROP TABLE IF EXISTS evaluation_rule_innovation_bucket;
DROP TABLE IF EXISTS evaluation_rule_labor_cap_exempt;
DROP TABLE IF EXISTS evaluation_rule_dedupe_policy;
DROP TABLE IF EXISTS evaluation_rule_section_base;
DROP TABLE IF EXISTS evaluation_rule_score_cap;

COMMIT;
