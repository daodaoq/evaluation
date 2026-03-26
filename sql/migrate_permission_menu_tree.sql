-- =============================================================================
-- 菜单权限树重构 + 新增「用户分配角色」「角色分配菜单」
-- 适用表结构（与项目 dump 一致）：
--   sys_permission(id, perm_name, perm_code, parent_id, status, create_time, update_time)
-- 执行前请备份数据库；若你库中 id 与下文冲突，请先调整 id 或改用更大号段（如 100+）。
-- =============================================================================

SET NAMES utf8mb4;

-- -----------------------------------------------------------------------------
-- 1. 新增两级父菜单（目录节点，无页面路由，仅用于侧栏分组）
-- -----------------------------------------------------------------------------
INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `parent_id`, `status`, `create_time`, `update_time`)
VALUES
  (100, '系统管理', 'sys:group:system', 0, 1, NOW(), NOW()),
  (101, '教学评估', 'sys:group:evaluation', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  `perm_name` = VALUES(`perm_name`),
  `parent_id` = VALUES(`parent_id`),
  `status` = VALUES(`status`),
  `update_time` = NOW();

-- -----------------------------------------------------------------------------
-- 2. 将原有「顶级」菜单挂到对应父目录下（id 与当前种子数据一致：1~7,16,17）
--    系统管理：用户 / 角色 / 权限 / 学生
--    教学评估：规则分类 / 规则总览 / 规则项 / 学院 / 班级
-- -----------------------------------------------------------------------------
UPDATE `sys_permission` SET `parent_id` = 100, `update_time` = NOW() WHERE `id` IN (1, 2, 3, 4);
UPDATE `sys_permission` SET `parent_id` = 101, `update_time` = NOW() WHERE `id` IN (5, 6, 7, 16, 17);

-- -----------------------------------------------------------------------------
-- 3. 新增两个功能菜单（与「用户/角色管理」同级挂在「系统管理」下，避免挂在 id=1/2 下导致侧栏父级变纯目录、失去直达列表页）
--    perm_code 含 :menu 便于后端 /sys-permission/menus 的 LIKE 筛选
-- -----------------------------------------------------------------------------
INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `parent_id`, `status`, `create_time`, `update_time`)
VALUES
  (102, '用户分配角色', 'sys:user:assign:menu', 100, 1, NOW(), NOW()),
  (103, '角色分配菜单', 'sys:role:assign:menu', 100, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  `perm_name` = VALUES(`perm_name`),
  `parent_id` = VALUES(`parent_id`),
  `status` = VALUES(`status`),
  `update_time` = NOW();

-- -----------------------------------------------------------------------------
-- 4. 自增指针（避免后续插入与手工 id 冲突；按需调整）
-- -----------------------------------------------------------------------------
ALTER TABLE `sys_permission` AUTO_INCREMENT = 200;

-- -----------------------------------------------------------------------------
-- 5. 给「系统管理员」角色（示例 role_id = 3）补全：父目录 + 新菜单
--    若你库中管理员角色 id 不同，请改 WHERE 或增删 INSERT。
-- -----------------------------------------------------------------------------
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `perm_id`, `create_time`, `update_time`)
VALUES
  (3, 100, NOW(), NOW()),
  (3, 101, NOW(), NOW()),
  (3, 102, NOW(), NOW()),
  (3, 103, NOW(), NOW());

-- 说明：
-- - 父目录 100/101 需分配给能看到整棵子树的账号；否则仅叶子权限在用户列表中时，树可能无法挂父（前端 listToTree 依赖 parent_id）。
-- - 若你希望两个入口出现在「用户管理」折叠下，需同时调整侧栏交互（例如父级用 router-link + 子级子菜单），当前脚本采用与 用户/角色 平级。
