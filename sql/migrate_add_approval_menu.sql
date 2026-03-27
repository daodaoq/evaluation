-- 新增「综测申报审批」菜单权限
-- 建议在 migrate_permission_menu_tree.sql 之后执行

SET NAMES utf8mb4;

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `parent_id`, `status`, `create_time`, `update_time`)
VALUES
  (104, '综测申报审批', 'sys:approval:menu', 101, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  `perm_name` = VALUES(`perm_name`),
  `perm_code` = VALUES(`perm_code`),
  `parent_id` = VALUES(`parent_id`),
  `status` = VALUES(`status`),
  `update_time` = NOW();

-- 默认给系统管理员（role_id=3）授权
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `perm_id`, `create_time`, `update_time`)
VALUES
  (3, 104, NOW(), NOW());

ALTER TABLE `sys_permission` AUTO_INCREMENT = 200;

