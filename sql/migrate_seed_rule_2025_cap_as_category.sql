-- 依据 resources/综测细则.txt（计算机政字〔2025〕17号），按「细则中带明确上限/满分的块」拆成独立规则分类。
-- 与当前表结构一致：evaluation_rule_item_category 含 parent_id、score_cap、student_visible、sort_order、category_base_score。
-- 加分上限由分类 score_cap 在 CategoryUnitScoreCalculator 中对「归入该分类 id 的加分」截断，本脚本不再写 evaluation_rule_item_limit。
--
-- 前置建议：migrate_comprehensive_evaluation_alignment.sql、migrate_rule_category_cap_visibility.sql、
--          migrate_rule_category_base_score.sql 已执行。
--
-- 幂等：按 rule_name 删除后重建；周期按 period_name 不存在则新增。

START TRANSACTION;

SET @period_name := '2025-2026学年第二学期（综测-上限拆分类）';
SET @rule_name := '计算机学院本科综测细则(2025)-按上限拆分类';
SET @version_code := 'v2025-cap-cat-1';

INSERT INTO evaluation_period (period_name, start_time, end_time, status, create_time, update_time)
SELECT @period_name, '2026-02-01 00:00:00', '2026-08-31 23:59:59', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM evaluation_period WHERE period_name = @period_name);

SET @period_id := (
    SELECT id FROM evaluation_period WHERE period_name = @period_name ORDER BY id DESC LIMIT 1
);

DELETE lim
FROM evaluation_rule_item_limit lim
INNER JOIN evaluation_rule_item ri ON lim.rule_item_id = ri.id
INNER JOIN evaluation_rule r ON ri.rule_id = r.id
WHERE r.rule_name = @rule_name;

DELETE ri
FROM evaluation_rule_item ri
INNER JOIN evaluation_rule r ON ri.rule_id = r.id
WHERE r.rule_name = @rule_name;

DELETE rc
FROM evaluation_rule_item_category rc
INNER JOIN evaluation_rule r ON rc.rule_id = r.id
WHERE r.rule_name = @rule_name;

DELETE FROM evaluation_rule WHERE rule_name = @rule_name;

INSERT INTO evaluation_rule (period_id, rule_name, version_code, status, create_time, update_time)
VALUES (@period_id, @rule_name, @version_code, 1, NOW(), NOW());
SET @rule_id := LAST_INSERT_ID();

-- ========== 规则分类树 ==========
-- 根级：任职（学生端任职分锚点，与 migrate_add_position_rule_category 一致）
INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '任职', 0, NULL, 1, -50, 0, NOW(), NOW());
SET @cat_position := LAST_INSERT_ID();

INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '德育评价', 0, NULL, 1, 0, 0, NOW(), NOW());
SET @cat_moral_root := LAST_INSERT_ID();

INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '德育基础与违纪减分', @cat_moral_root, NULL, 1, 0, 0, NOW(), NOW());
SET @cat_moral_deduct := LAST_INSERT_ID();

-- 细则：德育评价奖励分上限 3 分
INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '德育奖励分（细则合计上限3分）', @cat_moral_root, 3.00, 1, 10, 0, NOW(), NOW());
SET @cat_moral_bonus3 := LAST_INSERT_ID();

-- 学业模块 70 分：用分类上限与细则一致（加分项归入本分类）
INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '学业水平评价', 0, 70.00, 1, 10, 0, NOW(), NOW());
SET @cat_academic := LAST_INSERT_ID();

INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '素质能力评价', 0, NULL, 1, 20, 0, NOW(), NOW());
SET @cat_quality_root := LAST_INSERT_ID();

-- 身心：基础满分 3 分（以分类基础分体现）；加分块上限 1 分
INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '身心素养-基础（满分3分）', @cat_quality_root, NULL, 1, 0, 3.00, NOW(), NOW());
SET @cat_body_base := LAST_INSERT_ID();

INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '身心素养-体育等奖项及大型文体加分（上限1分）', @cat_quality_root, 1.00, 1, 10, 0, NOW(), NOW());
SET @cat_body_bonus1 := LAST_INSERT_ID();

-- 审美：第二课堂基础（无「加分上限1分」约束）；文化艺术/报道等加分上限 1 分
INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '审美人文-第二课堂参与', @cat_quality_root, NULL, 1, 20, 0, NOW(), NOW());
SET @cat_art_second := LAST_INSERT_ID();

INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '审美人文-文化艺术与报道等加分（上限1分）', @cat_quality_root, 1.00, 1, 30, 0, NOW(), NOW());
SET @cat_art_bonus1 := LAST_INSERT_ID();

INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '劳动素养-宿舍违纪减分', @cat_quality_root, NULL, 1, 40, 0, NOW(), NOW());
SET @cat_labor_deduct := LAST_INSERT_ID();

-- 劳动素养评价加分满分 1 分（社会实践、过级等合计）
INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '劳动素养-社会实践与过级等加分（上限1分）', @cat_quality_root, 1.00, 1, 50, 0, NOW(), NOW());
SET @cat_labor_bonus1 := LAST_INSERT_ID();

-- 创新素养 8 分 = 基础性 6 + 发展性 2
INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '创新素养-基础性评价与论文等（上限6分）', @cat_quality_root, 6.00, 1, 60, 0, NOW(), NOW());
SET @cat_inno_base6 := LAST_INSERT_ID();

INSERT INTO evaluation_rule_item_category
(rule_id, category_name, parent_id, score_cap, student_visible, sort_order, category_base_score, create_time, update_time)
VALUES (@rule_id, '创新素养-发展性评价（上限2分）', @cat_quality_root, 2.00, 1, 70, 0, NOW(), NOW());
SET @cat_inno_dev2 := LAST_INSERT_ID();

-- ========== 规则项（item_category 指向分类 id）==========

-- 德育减分
INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
) VALUES
(@rule_id, '德育-通报批评', 1, @cat_moral_deduct, '处分', 0.80, 0, 0, 1, 'SUB', 'MORAL_DISCIPLINE', 1.000, 'MORAL', 'DISCIPLINE', NOW(), NOW()),
(@rule_id, '德育-警告', 1, @cat_moral_deduct, '处分', 1.50, 0, 0, 1, 'SUB', 'MORAL_DISCIPLINE', 1.000, 'MORAL', 'DISCIPLINE', NOW(), NOW()),
(@rule_id, '德育-严重警告', 1, @cat_moral_deduct, '处分', 2.00, 0, 0, 1, 'SUB', 'MORAL_DISCIPLINE', 1.000, 'MORAL', 'DISCIPLINE', NOW(), NOW()),
(@rule_id, '德育-记过', 1, @cat_moral_deduct, '处分', 4.00, 0, 0, 1, 'SUB', 'MORAL_DISCIPLINE', 1.000, 'MORAL', 'DISCIPLINE', NOW(), NOW()),
(@rule_id, '德育-留校察看', 1, @cat_moral_deduct, '处分', 6.00, 0, 0, 1, 'SUB', 'MORAL_DISCIPLINE', 1.000, 'MORAL', 'DISCIPLINE', NOW(), NOW()),
(@rule_id, '德育-课堂迟到（每次）', 1, @cat_moral_deduct, '考勤', 0.10, 0, 0, 1, 'SUB', 'MORAL_ATTENDANCE', 1.000, 'MORAL', 'ATTENDANCE', NOW(), NOW()),
(@rule_id, '德育-课堂早退（每次）', 1, @cat_moral_deduct, '考勤', 0.10, 0, 0, 1, 'SUB', 'MORAL_ATTENDANCE', 1.000, 'MORAL', 'ATTENDANCE', NOW(), NOW()),
(@rule_id, '德育-课堂旷课（每节）', 1, @cat_moral_deduct, '考勤', 1.00, 0, 0, 1, 'SUB', 'MORAL_ATTENDANCE', 1.000, 'MORAL', 'ATTENDANCE', NOW(), NOW()),
(@rule_id, '德育-集体活动迟到（每次）', 1, @cat_moral_deduct, '活动', 0.10, 0, 0, 1, 'SUB', 'MORAL_ACTIVITY', 1.000, 'MORAL', 'ACTIVITY', NOW(), NOW()),
(@rule_id, '德育-集体活动早退（每次）', 1, @cat_moral_deduct, '活动', 0.10, 0, 0, 1, 'SUB', 'MORAL_ACTIVITY', 1.000, 'MORAL', 'ACTIVITY', NOW(), NOW()),
(@rule_id, '德育-集体活动缺勤（每次）', 1, @cat_moral_deduct, '活动', 0.50, 0, 0, 1, 'SUB', 'MORAL_ACTIVITY', 1.000, 'MORAL', 'ACTIVITY', NOW(), NOW()),
(@rule_id, '德育-校园教学楼内吸烟（每次）', 1, @cat_moral_deduct, '行为', 1.00, 0, 0, 1, 'SUB', 'MORAL_BEHAVIOR', 1.000, 'MORAL', 'BEHAVIOR', NOW(), NOW()),
(@rule_id, '德育-诚信问题（学术失信/恶意欠费等，每次）', 1, @cat_moral_deduct, '诚信', 2.00, 0, 0, 1, 'SUB', 'MORAL_INTEGRITY', 1.000, 'MORAL', 'INTEGRITY', NOW(), NOW()),
(@rule_id, '德育-严重违规行为（基础分清零）', 1, @cat_moral_deduct, '一票否决', 10.00, 0, 1, 1, 'MAX_ONLY', 'MORAL_ZERO', 1.000, 'MORAL', 'ZERO', NOW(), NOW());

-- 德育奖励（上限 3 由分类 score_cap 控制）
INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
) VALUES
(@rule_id, '德育荣誉-好人好事-国家级', 0, @cat_moral_bonus3, '国家级', 3.00, 0, 1, 1, 'ADD', 'MORAL_HONOR_GOOD', 1.000, 'MORAL', 'HONOR', NOW(), NOW()),
(@rule_id, '德育荣誉-好人好事-省级', 0, @cat_moral_bonus3, '省级', 2.00, 0, 1, 1, 'ADD', 'MORAL_HONOR_GOOD', 1.000, 'MORAL', 'HONOR', NOW(), NOW()),
(@rule_id, '德育荣誉-好人好事-市/校级', 0, @cat_moral_bonus3, '市校级', 1.50, 0, 1, 1, 'ADD', 'MORAL_HONOR_GOOD', 1.000, 'MORAL', 'HONOR', NOW(), NOW()),
(@rule_id, '德育荣誉-好人好事-院级', 0, @cat_moral_bonus3, '院级', 1.00, 0, 1, 1, 'ADD', 'MORAL_HONOR_GOOD', 1.000, 'MORAL', 'HONOR', NOW(), NOW()),
(@rule_id, '德育荣誉-通报表扬-国家级', 0, @cat_moral_bonus3, '国家级', 2.00, 0, 1, 1, 'ADD', 'MORAL_HONOR_COMMEND', 1.000, 'MORAL', 'HONOR', NOW(), NOW()),
(@rule_id, '德育荣誉-通报表扬-省级', 0, @cat_moral_bonus3, '省级', 1.50, 0, 1, 1, 'ADD', 'MORAL_HONOR_COMMEND', 1.000, 'MORAL', 'HONOR', NOW(), NOW()),
(@rule_id, '德育荣誉-通报表扬-市/校级', 0, @cat_moral_bonus3, '市校级', 1.00, 0, 1, 1, 'ADD', 'MORAL_HONOR_COMMEND', 1.000, 'MORAL', 'HONOR', NOW(), NOW()),
(@rule_id, '德育荣誉-通报表扬-院级', 0, @cat_moral_bonus3, '院级', 0.50, 0, 1, 1, 'ADD', 'MORAL_HONOR_COMMEND', 1.000, 'MORAL', 'HONOR', NOW(), NOW()),
(@rule_id, '学生干部-执行主席/辅导员助理', 0, @cat_moral_bonus3, '学院', 2.50, 0, 1, 1, 'ADD', 'MORAL_CADRE', 1.000, 'MORAL', 'CADRE', NOW(), NOW()),
(@rule_id, '学生干部-轮值主席/大社联主席/会长', 0, @cat_moral_bonus3, '学院', 2.00, 0, 1, 1, 'ADD', 'MORAL_CADRE', 1.000, 'MORAL', 'CADRE', NOW(), NOW()),
(@rule_id, '学生干部-部门负责人/党支部副书记/社联部门负责人', 0, @cat_moral_bonus3, '学院', 1.80, 0, 1, 1, 'ADD', 'MORAL_CADRE', 1.000, 'MORAL', 'CADRE', NOW(), NOW()),
(@rule_id, '学生干部-班长/团支书', 0, @cat_moral_bonus3, '班级', 2.00, 0, 1, 1, 'ADD', 'MORAL_CADRE', 1.000, 'MORAL', 'CADRE', NOW(), NOW()),
(@rule_id, '学生干部-班委/团支部委员/党支部委员/社联副部长', 0, @cat_moral_bonus3, '班级', 1.50, 0, 1, 1, 'ADD', 'MORAL_CADRE', 1.000, 'MORAL', 'CADRE', NOW(), NOW()),
(@rule_id, '学生干部-宿舍长', 0, @cat_moral_bonus3, '班级', 1.20, 0, 1, 1, 'ADD', 'MORAL_CADRE', 1.000, 'MORAL', 'CADRE', NOW(), NOW()),
(@rule_id, '学生干部-干事（考核合格）', 0, @cat_moral_bonus3, '班级', 1.20, 0, 1, 1, 'ADD', 'MORAL_CADRE', 1.000, 'MORAL', 'CADRE', NOW(), NOW()),
(@rule_id, '学生干部-社团干事', 0, @cat_moral_bonus3, '班级', 0.80, 0, 1, 1, 'ADD', 'MORAL_CADRE', 1.000, 'MORAL', 'CADRE', NOW(), NOW()),
(@rule_id, '学生干部-次高职务折算', 0, @cat_moral_bonus3, '折算', 1.00, 0, 0, 1, 'ADD', 'MORAL_CADRE', 0.500, 'MORAL', 'CADRE', NOW(), NOW()),
(@rule_id, '学生干部-第三职务折算', 0, @cat_moral_bonus3, '折算', 1.00, 0, 0, 1, 'ADD', 'MORAL_CADRE', 0.300, 'MORAL', 'CADRE', NOW(), NOW()),
(@rule_id, '学生干部-满意度不足50%折算', 0, @cat_moral_bonus3, '折算', 1.00, 0, 0, 1, 'ADD', 'MORAL_CADRE', 0.500, 'MORAL', 'CADRE', NOW(), NOW());

-- 学业
INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
) VALUES
(@rule_id, '学业水平-平均学分绩点折算总分', 0, @cat_academic, '系统计算', 70.00, 0, 0, 1, 'ADD', 'ACADEMIC_GPA', 1.000, 'ACADEMIC', 'GPA', NOW(), NOW()),
(@rule_id, '学业水平-课程免修折算70分（规则说明项）', 0, @cat_academic, '规则', 0.00, 0, 0, 1, 'ADD', 'ACADEMIC_RULE', 1.000, 'ACADEMIC', 'RULE', NOW(), NOW()),
(@rule_id, '学业水平-等级制折算（优95/良84/中73/及62）说明项', 0, @cat_academic, '规则', 0.00, 0, 0, 1, 'ADD', 'ACADEMIC_RULE', 1.000, 'ACADEMIC', 'RULE', NOW(), NOW()),
(@rule_id, '学业水平-考试作弊/旷考/禁考/缺考按0分说明项', 1, @cat_academic, '规则', 0.00, 0, 0, 1, 'SUB', 'ACADEMIC_RULE', 1.000, 'ACADEMIC', 'RULE', NOW(), NOW());

CREATE TEMPORARY TABLE tmp_award_score (
  lv VARCHAR(20),
  grade VARCHAR(20),
  score DECIMAL(6,2)
);

INSERT INTO tmp_award_score (lv, grade, score) VALUES
('国家级', '一等奖', 1.00), ('国家级', '二等奖', 0.90), ('国家级', '三等奖', 0.80), ('国家级', '优秀奖', 0.70),
('省级',   '一等奖', 0.80), ('省级',   '二等奖', 0.70), ('省级',   '三等奖', 0.50), ('省级',   '优秀奖', 0.40),
('市校级', '一等奖', 0.60), ('市校级', '二等奖', 0.50), ('市校级', '三等奖', 0.40), ('市校级', '优秀奖', 0.20),
('院级',   '一等奖', 0.40), ('院级',   '二等奖', 0.30), ('院级',   '三等奖', 0.20), ('院级',   '优秀奖', 0.10);

-- 身心：基础减分（在「满分3分」分类下做扣分）
INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
) VALUES
(@rule_id, '身心素养-基础减分（每次）', 1, @cat_body_base, '基础扣分', 1.50, 0, 0, 1, 'SUB', 'QUALITY_BODYMIND_DEDUCT', 1.000, 'QUALITY', 'BODYMIND', NOW(), NOW());

-- 身心：加分块（上限 1）
INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
)
SELECT
    @rule_id,
    CONCAT('身心素养-', lv, '-', grade),
    0,
    @cat_body_bonus1,
    CONCAT(lv, grade),
    score,
    1,
    1,
    1,
    'ADD',
    'QUALITY_BODYMIND_AWARD',
    1.000,
    'QUALITY',
    'BODYMIND',
    NOW(),
    NOW()
FROM tmp_award_score;

INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
) VALUES
(@rule_id, '身心素养-代表学院参加大型文体活动（每项）', 0, @cat_body_bonus1, '活动', 0.40, 0, 1, 1, 'ADD', 'QUALITY_BODYMIND_ACTIVITY', 1.000, 'QUALITY', 'BODYMIND', NOW(), NOW());

-- 审美：第二课堂（无「加分上限1分」）
INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
) VALUES
(@rule_id, '审美人文-第二课堂项目负责人（每项）', 0, @cat_art_second, '第二课堂', 1.50, 0, 1, 1, 'ADD', 'QUALITY_ART_SECOND_CLASS', 1.000, 'QUALITY', 'ART', NOW(), NOW()),
(@rule_id, '审美人文-第二课堂普通成员（每项）', 0, @cat_art_second, '第二课堂', 1.00, 0, 1, 1, 'ADD', 'QUALITY_ART_SECOND_CLASS', 1.000, 'QUALITY', 'ART', NOW(), NOW());

-- 审美：加分上限 1
INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
)
SELECT
    @rule_id,
    CONCAT('审美人文-文化活动-', lv, '-', grade),
    0,
    @cat_art_bonus1,
    CONCAT(lv, grade),
    score,
    1,
    1,
    1,
    'ADD',
    'QUALITY_ART_AWARD',
    1.000,
    'QUALITY',
    'ART',
    NOW(),
    NOW()
FROM tmp_award_score;

INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
) VALUES
(@rule_id, '审美人文-国家/省级官网发表新闻（每篇）', 0, @cat_art_bonus1, '宣传报道', 0.60, 0, 1, 1, 'ADD', 'QUALITY_ART_MEDIA', 1.000, 'QUALITY', 'MEDIA', NOW(), NOW()),
(@rule_id, '审美人文-校外地市官方媒体/报纸/校报（每篇）', 0, @cat_art_bonus1, '宣传报道', 0.40, 0, 1, 1, 'ADD', 'QUALITY_ART_MEDIA', 1.000, 'QUALITY', 'MEDIA', NOW(), NOW()),
(@rule_id, '审美人文-校电台/电视台/校报/理工视窗等（每篇）', 0, @cat_art_bonus1, '宣传报道', 0.20, 0, 1, 1, 'ADD', 'QUALITY_ART_MEDIA', 1.000, 'QUALITY', 'MEDIA', NOW(), NOW()),
(@rule_id, '审美人文-学院网站/学院公众号发表新闻（每篇）', 0, @cat_art_bonus1, '宣传报道', 0.10, 0, 1, 1, 'ADD', 'QUALITY_ART_MEDIA', 1.000, 'QUALITY', 'MEDIA', NOW(), NOW()),
(@rule_id, '审美人文-图片/条幅制作并采用（每人）', 0, @cat_art_bonus1, '宣传报道', 0.10, 0, 1, 1, 'ADD', 'QUALITY_ART_MEDIA', 1.000, 'QUALITY', 'MEDIA', NOW(), NOW()),
(@rule_id, '审美人文-网站/视频/PPT制作并采用（每人）', 0, @cat_art_bonus1, '宣传报道', 0.10, 0, 1, 1, 'ADD', 'QUALITY_ART_MEDIA', 1.000, 'QUALITY', 'MEDIA', NOW(), NOW()),
(@rule_id, '审美人文-公寓黑板报通报表扬（每次/人）', 0, @cat_art_bonus1, '宣传报道', 0.10, 0, 1, 1, 'ADD', 'QUALITY_ART_MEDIA', 1.000, 'QUALITY', 'MEDIA', NOW(), NOW()),
(@rule_id, '审美人文-晚会主持（每次）', 0, @cat_art_bonus1, '文艺活动', 0.30, 0, 1, 1, 'ADD', 'QUALITY_ART_ACTIVITY', 1.000, 'QUALITY', 'ACTIVITY', NOW(), NOW()),
(@rule_id, '审美人文-活动礼仪（每次）', 0, @cat_art_bonus1, '文艺活动', 0.20, 0, 1, 1, 'ADD', 'QUALITY_ART_ACTIVITY', 1.000, 'QUALITY', 'ACTIVITY', NOW(), NOW()),
(@rule_id, '审美人文-节目出演（每次）', 0, @cat_art_bonus1, '文艺活动', 0.30, 0, 1, 1, 'ADD', 'QUALITY_ART_ACTIVITY', 1.000, 'QUALITY', 'ACTIVITY', NOW(), NOW()),
(@rule_id, '审美人文-代表学院参加大型文体活动（每项）', 0, @cat_art_bonus1, '文艺活动', 0.40, 0, 1, 1, 'ADD', 'QUALITY_ART_ACTIVITY', 1.000, 'QUALITY', 'ACTIVITY', NOW(), NOW());

-- 劳动：减分
INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
) VALUES
(@rule_id, '劳动素养-虐待动物（每次）', 1, @cat_labor_deduct, '违纪', 1.50, 0, 0, 1, 'SUB', 'QUALITY_LABOR_DEDUCT', 1.000, 'QUALITY', 'LABOR', NOW(), NOW()),
(@rule_id, '劳动素养-擅自夜不归宿/瞒报（每次）', 1, @cat_labor_deduct, '违纪', 1.50, 0, 0, 1, 'SUB', 'QUALITY_LABOR_DEDUCT', 1.000, 'QUALITY', 'LABOR', NOW(), NOW()),
(@rule_id, '劳动素养-私自破坏逃生窗或公物（每次）', 1, @cat_labor_deduct, '违纪', 2.00, 0, 0, 1, 'SUB', 'QUALITY_LABOR_DEDUCT', 1.000, 'QUALITY', 'LABOR', NOW(), NOW()),
(@rule_id, '劳动素养-宿舍影响集体休息（每次）', 1, @cat_labor_deduct, '违纪', 0.50, 0, 0, 1, 'SUB', 'QUALITY_LABOR_DEDUCT', 1.000, 'QUALITY', 'LABOR', NOW(), NOW()),
(@rule_id, '劳动素养-宿舍不按时熄灯（每次）', 1, @cat_labor_deduct, '违纪', 0.20, 0, 0, 1, 'SUB', 'QUALITY_LABOR_DEDUCT', 1.000, 'QUALITY', 'LABOR', NOW(), NOW());

-- 劳动：加分上限 1
INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
)
SELECT
    @rule_id,
    CONCAT('劳动素养-实践志愿-', lv, '-', grade),
    0,
    @cat_labor_bonus1,
    CONCAT(lv, grade),
    score,
    1,
    1,
    1,
    'ADD',
    'QUALITY_LABOR_AWARD',
    1.000,
    'QUALITY',
    'LABOR',
    NOW(),
    NOW()
FROM tmp_award_score;

INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
) VALUES
(@rule_id, '劳动素养-英语四级', 0, @cat_labor_bonus1, '过级', 0.40, 0, 1, 1, 'ADD', 'QUALITY_LANGUAGE', 1.000, 'QUALITY', 'LANGUAGE', NOW(), NOW()),
(@rule_id, '劳动素养-英语六级', 0, @cat_labor_bonus1, '过级', 0.60, 0, 1, 1, 'ADD', 'QUALITY_LANGUAGE', 1.000, 'QUALITY', 'LANGUAGE', NOW(), NOW()),
(@rule_id, '劳动素养-集体项目队员折算（按获奖分值0.5）', 0, @cat_labor_bonus1, '折算', 1.00, 0, 0, 1, 'ADD', 'QUALITY_LABOR_AWARD', 0.500, 'QUALITY', 'LABOR', NOW(), NOW());

-- 创新：基础性 + 论文（上限 6）
INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
) VALUES
(@rule_id, '创新素养-基础性评价（竞赛/科研/专利/论文/双创）', 0, @cat_inno_base6, '基础', 6.00, 1, 1, 1, 'ADD', 'QUALITY_INNOVATION_BASE', 1.000, 'QUALITY', 'INNOVATION', NOW(), NOW()),
(@rule_id, '创新素养-论文发表第一作者（篇）', 0, @cat_inno_base6, '论文', 1.50, 0, 1, 1, 'ADD', 'QUALITY_INNOVATION_PAPER', 1.000, 'QUALITY', 'PAPER', NOW(), NOW()),
(@rule_id, '创新素养-论文发表第二作者（按1/2）', 0, @cat_inno_base6, '论文', 1.50, 0, 1, 1, 'ADD', 'QUALITY_INNOVATION_PAPER', 0.500, 'QUALITY', 'PAPER', NOW(), NOW()),
(@rule_id, '创新素养-论文发表第三作者（按1/4）', 0, @cat_inno_base6, '论文', 1.50, 0, 1, 1, 'ADD', 'QUALITY_INNOVATION_PAPER', 0.250, 'QUALITY', 'PAPER', NOW(), NOW()),
(@rule_id, '创新素养-论文发表第四作者及以后不加分（说明项）', 0, @cat_inno_base6, '论文规则', 0.00, 0, 0, 1, 'ADD', 'QUALITY_INNOVATION_PAPER', 0.000, 'QUALITY', 'PAPER', NOW(), NOW());

-- 创新：发展性（上限 2）
INSERT INTO evaluation_rule_item (
    rule_id, item_name, item_type, item_category, level, base_score, is_competition, need_material, status,
    score_mode, dedupe_group, coeff, module_code, submodule_code, create_time, update_time
) VALUES
(@rule_id, '创新素养-发展性评价（标志性科研成果/A+竞赛）', 0, @cat_inno_dev2, '发展', 2.00, 1, 1, 1, 'ADD', 'QUALITY_INNOVATION_DEV', 1.000, 'QUALITY', 'INNOVATION', NOW(), NOW());

DROP TEMPORARY TABLE tmp_award_score;

COMMIT;
