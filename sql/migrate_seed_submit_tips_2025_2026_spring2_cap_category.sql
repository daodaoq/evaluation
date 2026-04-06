-- 为「2025-2026 学年第二学期（综测-上限拆分类）」周期批量写入学生端申报提示。
-- 与 migrate_seed_rule_2025_cap_as_category.sql、resources/综测细则.txt（计算机政字〔2025〕17号）口径一致。
-- section_code 使用规则分类主键 id（数字字符串），与后台「申报提示维护」及学生端按分类展示一致。
--
-- 幂等：先删除本周期内标题以「｜综测提示」结尾的记录，再按当前规则下全部分类各插入 1 条。

START TRANSACTION;

SET @period_name := '2025-2026学年第二学期（综测-上限拆分类）';

SET @period_id := (
    SELECT id FROM evaluation_period WHERE period_name = @period_name ORDER BY id DESC LIMIT 1
);

SET @rule_id := (
    SELECT r.id
    FROM evaluation_rule r
    WHERE r.period_id = @period_id
    ORDER BY r.id DESC
    LIMIT 1
);

-- 若周期或规则不存在则不做写入（避免误插到其他库）
DELETE FROM evaluation_student_submit_tip
WHERE @period_id IS NOT NULL
  AND period_id = @period_id
  AND title LIKE '%｜综测提示';

INSERT INTO evaluation_student_submit_tip (
    period_id,
    section_code,
    title,
    content,
    sort_order,
    status,
    operator_user_id,
    create_time,
    update_time
)
SELECT
    @period_id,
    CAST(x.id AS CHAR),
    CONCAT(x.category_name, '｜综测提示'),
    (CASE x.category_name
        WHEN '任职' THEN '请按实际任职情况填写申报分值，并上传任职证明、考核或公示材料，最终以学院审核认定为准。'
        WHEN '德育评价' THEN '本项为目录：请在子类「德育基础与违纪减分」「德育奖励分（细则合计上限3分）」中选择对应细则分别申报。'
        WHEN '德育基础与违纪减分' THEN '处分、课堂与集体活动考勤、违纪行为等按细则对应减分项申报，建议在备注中写清时间、次数及处理依据。'
        WHEN '德育奖励分（细则合计上限3分）' THEN '好人好事、通报表扬、学生干部任职等加分归入本类且合计不超过3分；多职务按最高、次高×0.5、第三×0.3折算，第四及以后不加分。'
        WHEN '学业水平评价' THEN '按平均学分绩点折算总分（本类上限70分）；免修与等级制折算按细则执行，考试作弊、旷考、禁考、缺考按0分口径，以教务与学院认定为准。'
        WHEN '素质能力评价' THEN '本项为目录：请在身心、审美人文、劳动、创新等子类中分别选择细则完成申报。'
        WHEN '身心素养-基础（满分3分）' THEN '基础满分3分；缺席心理普查、阳光体育、军训及相关任务等按细则在对应减分项申报。'
        WHEN '身心素养-体育等奖项及大型文体加分（上限1分）' THEN '文体竞赛按级别与等级加分，代表学院参加大型文体活动等按项计分，本类加分合计不超过1分，请上传证书或官方公示材料。'
        WHEN '审美人文-第二课堂参与' THEN '第二课堂项目负责人与普通成员按项目分别记分，请上传立项、结项或学院认定材料。'
        WHEN '审美人文-文化艺术与报道等加分（上限1分）' THEN '文化活动、宣传报道、文艺活动等加分归入本类且合计不超过1分，请在备注中写清平台、时间与本人角色。'
        WHEN '劳动素养-宿舍违纪减分' THEN '虐待动物、擅自夜不归宿、损坏公物、影响集体休息及不按时熄灯等按细则对应项申报减分。'
        WHEN '劳动素养-社会实践与过级等加分（上限1分）' THEN '社会实践与志愿服务按获奖等级加分，英语四级0.4分、六级0.6分；集体项目队员可按细则折算，本类加分合计不超过1分。'
        WHEN '创新素养-基础性评价与论文等（上限6分）' THEN '竞赛、科研、专利、论文及双创训练等归入本类，论文作者顺位与加分按细则折算，本类合计不超过6分。'
        WHEN '创新素养-发展性评价（上限2分）' THEN '标志性科研成果与A+竞赛等归入本类，合计不超过2分，须上传权威证明或官方公示链接等材料。'
        ELSE CONCAT('请根据《综测细则》在本分类选择对应细则申报，材料须真实清晰，最终以学院审核认定为准。（', x.category_name, '）')
    END),
    1000 + x.rn,
    1,
    NULL,
    NOW(),
    NOW()
FROM (
    SELECT
        c.id,
        c.category_name,
        ROW_NUMBER() OVER (ORDER BY c.sort_order, c.id) AS rn
    FROM evaluation_rule_item_category c
    WHERE @period_id IS NOT NULL
      AND @rule_id IS NOT NULL
      AND c.rule_id = @rule_id
) AS x;

COMMIT;
