-- 班级综测成绩汇总（教师看本班、可导出 Excel）
INSERT INTO sys_permission (perm_name, perm_code, parent_id, status, create_time, update_time)
SELECT '班级综测成绩', 'sys:class-score:menu', 101, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM sys_permission WHERE perm_code = 'sys:class-score:menu'
);

INSERT INTO sys_role_permission (role_id, perm_id, create_time, update_time)
SELECT r.id, p.id, NOW(), NOW()
FROM sys_permission p
JOIN sys_role r ON r.role_code = 'ADMIN'
WHERE p.perm_code = 'sys:class-score:menu'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.perm_id = p.id
);

INSERT INTO sys_role_permission (role_id, perm_id, create_time, update_time)
SELECT r.id, p.id, NOW(), NOW()
FROM sys_permission p
JOIN sys_role r ON r.role_code = 'TEACHER'
WHERE p.perm_code = 'sys:class-score:menu'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.perm_id = p.id
);
