-- 根据《本科学生综合素质评价实施细则》提炼的学生端申报提示种子数据
-- 用法：
-- 1) 先设置目标周期ID
-- 2) 再执行本脚本
-- 注意：会先删除该周期已有提示，再重新插入

SET @period_id = 1;

DELETE FROM evaluation_student_submit_tip
WHERE period_id = @period_id;

INSERT INTO evaluation_student_submit_tip
(period_id, section_code, title, content, sort_order, status, operator_user_id, create_time, update_time)
VALUES
-- 德育 moral
(@period_id, 'moral', '德育总公式', '德育成绩=基础分10-减分项+奖励分，奖励分上限3分；同一事项适用多标准时按最高分，不重复加分。', 10, 1, NULL, NOW(), NOW()),
(@period_id, 'moral', '德育减分重点', '处分、旷课/迟到早退、集体活动缺勤、教学楼吸烟、学术失信和恶意欠费等均会扣分；严重违纪情形可能导致基础分为0。', 20, 1, NULL, NOW(), NOW()),
(@period_id, 'moral', '德育奖励重点', '好人好事、通报表扬、学生干部任职可加分；多职务按“最高+次高×0.5+第三×0.3”，第四及以后不加分。', 30, 1, NULL, NOW(), NOW()),

-- 学业 academic
(@period_id, 'academic', '学业成绩口径', '学业水平评价按课程平均学分绩点口径计算，重修/通识选修/辅修课程一般不计入该口径。', 10, 1, NULL, NOW(), NOW()),
(@period_id, 'academic', '特殊成绩折算', '免修按70分；等级制折算：优95、良84、中73、及格62、不及格0；两级制折算：合格70、不合格0。', 20, 1, NULL, NOW(), NOW()),
(@period_id, 'academic', '学术诚信提醒', '考试作弊、旷考、禁考、缺考按0分计入。请以教务系统与学院最终认定为准。', 30, 1, NULL, NOW(), NOW()),

-- 身心素养 quality_bodymind
(@period_id, 'quality_bodymind', '身心素养公式', '身心素养=基础分3-减分项+奖励分（奖励项满分1）。无故缺席心理普查、阳光体育、军训任务等会扣分。', 10, 1, NULL, NOW(), NOW()),
(@period_id, 'quality_bodymind', '文体竞赛加分', '文体竞赛按国家/省/市校/院级与奖项等级加分；名次折算按细则执行。建议上传证书、发文、名单公示等材料。', 20, 1, NULL, NOW(), NOW()),
(@period_id, 'quality_bodymind', '不重复加分规则', '同一类别、关联性成果一般按最高分认定，不重复累计。', 30, 1, NULL, NOW(), NOW()),

-- 审美与人文 quality_art
(@period_id, 'quality_art', '审美人文基础说明', '该模块以文化活动、宣传报道、文艺活动等为主，基础性评价可参考第二课堂记录。', 10, 1, NULL, NOW(), NOW()),
(@period_id, 'quality_art', '宣传报道与作品加分', '宣传稿件、图片/条幅、视频/PPT、公寓黑板报、主持礼仪和节目出演均可按细则加分。请写清平台、时间、角色。', 20, 1, NULL, NOW(), NOW()),

-- 劳动素养 quality_labor
(@period_id, 'quality_labor', '劳动素养减分重点', '夜不归宿、损坏公物、影响休息、不按时熄灯等会扣分；按细则和学校管理规定执行。', 10, 1, NULL, NOW(), NOW()),
(@period_id, 'quality_labor', '实践志愿与过级加分', '社会实践/志愿服务按级别和奖项加分；英语四级0.4、六级0.6（按证书获得学期计分）。', 20, 1, NULL, NOW(), NOW()),
(@period_id, 'quality_labor', '集体项目折算', '集体项目获奖时，队员按对应奖项分值的0.5折算；同一作品同一比赛按最高奖项，不重复。', 30, 1, NULL, NOW(), NOW()),

-- 创新素养 quality_innovation
(@period_id, 'quality_innovation', '创新素养结构', '创新素养分基础性评价（6分）与发展性评价（2分），重点看竞赛、科研、专利、论文、双创训练等。', 10, 1, NULL, NOW(), NOW()),
(@period_id, 'quality_innovation', '论文加分规则', '论文加分：第一作者1.5分/篇，第二作者按1/2，第三作者按1/4，第四及以后不加分；须有正式刊号且与专业相关。', 20, 1, NULL, NOW(), NOW()),
(@period_id, 'quality_innovation', '材料提交建议', '建议上传：证书、论文首页与检索页、专利授权页、立项/结题证明、官方公示链接。', 30, 1, NULL, NOW(), NOW());

