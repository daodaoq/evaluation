-- =============================================================================
-- 为「教师」角色补充默认菜单权限（解决教师登录后侧栏仅首页的问题）
-- 原因：种子数据中 sys_role_permission 仅有管理员(3)、学生(1)，教师(2)无任何记录。
-- 说明：
--   - role_id = 2 对应 sys_role 中「教师 / TEACHER」
--   - 分配教学评估侧常用菜单；101 为 migrate_permission_menu_tree.sql 中的「教学评估」目录
--   - 若尚未执行迁移、表中无 id=101，可忽略该条或先执行迁移脚本
-- =============================================================================

SET NAMES utf8mb4;

INSERT IGNORE INTO `sys_role_permission` (`role_id`, `perm_id`, `create_time`, `update_time`)
VALUES
  (2, 101, NOW(), NOW()),
  (2, 5, NOW(), NOW()),
  (2, 6, NOW(), NOW()),
  (2, 7, NOW(), NOW()),
  (2, 16, NOW(), NOW()),
  (2, 17, NOW(), NOW());
