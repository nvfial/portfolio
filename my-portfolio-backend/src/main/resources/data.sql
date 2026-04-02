-- ============================================
-- Portfolio 数据库初始化数据
-- 注意：此文件仅在 spring.jpa.hibernate.ddl-auto=create 时执行
--
-- 设计原则：数据应从GitHub仓库或本地上传自动获取，不应写死
-- ============================================

-- 清空所有业务数据（保留表结构）
TRUNCATE TABLE `project_images`;
TRUNCATE TABLE `project_tags`;
TRUNCATE TABLE `projects`;
TRUNCATE TABLE `media_files`;
TRUNCATE TABLE `comments`;
TRUNCATE TABLE `knowledge_relations`;
TRUNCATE TABLE `knowledge_nodes`;
TRUNCATE TABLE `article_operations`;
TRUNCATE TABLE `article_versions`;
TRUNCATE TABLE `knowledge_articles`;
TRUNCATE TABLE `knowledge_categories`;
TRUNCATE TABLE `knowledge_domains`;
TRUNCATE TABLE `gallery_artworks`;
TRUNCATE TABLE `gallery_collections`;
TRUNCATE TABLE `testimonials`;
TRUNCATE TABLE `contacts`;
TRUNCATE TABLE `visit_logs`;
TRUNCATE TABLE `visitor_logs`;
TRUNCATE TABLE `role_permissions`;
TRUNCATE TABLE `user_roles`;
TRUNCATE TABLE `permissions`;
TRUNCATE TABLE `roles`;
TRUNCATE TABLE `users`;

-- ============================================
-- 基础配置数据（系统必需的初始化数据）
-- ============================================

-- 插入默认角色
INSERT INTO `roles` (`name`, `description`, `created_at`, `updated_at`) VALUES
('ADMIN', '系统管理员，拥有所有权限', NOW(), NOW()),
('USER', '普通用户，基本访问权限', NOW(), NOW()),
('GUEST', '访客，仅有只读权限', NOW(), NOW())
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 插入默认权限
INSERT INTO `permissions` (`name`, `description`, `created_at`, `updated_at`) VALUES
('PROJECT_READ', '查看项目列表', NOW(), NOW()),
('PROJECT_CREATE', '创建项目', NOW(), NOW()),
('PROJECT_UPDATE', '更新项目', NOW(), NOW()),
('PROJECT_DELETE', '删除项目', NOW(), NOW()),
('PROJECT_ANALYZE', '分析项目（GitHub/本地）', NOW(), NOW()),
('KNOWLEDGE_READ', '查看知识库', NOW(), NOW()),
('KNOWLEDGE_MANAGE', '管理知识库', NOW(), NOW()),
('GALLERY_READ', '查看画廊', NOW(), NOW()),
('GALLERY_MANAGE', '管理画廊', NOW(), NOW()),
('CONTACT_READ', '查看联系消息', NOW(), NOW()),
('CONTACT_MANAGE', '管理联系消息', NOW(), NOW()),
('TESTIMONIAL_READ', '查看推荐信', NOW(), NOW()),
('TESTIMONIAL_MANAGE', '管理推荐信', NOW(), NOW()),
('USER_MANAGE', '用户管理', NOW(), NOW()),
('ANALYTICS_VIEW', '查看分析统计', NOW(), NOW())
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 为ADMIN角色分配所有权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `roles` r, `permissions` p WHERE r.name = 'ADMIN'
ON DUPLICATE KEY UPDATE `role_id` = `role_id`;

-- 为USER角色分配基本权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `roles` r, `permissions` p 
WHERE r.name = 'USER' AND p.name IN ('PROJECT_READ', 'KNOWLEDGE_READ', 'GALLERY_READ', 'CONTACT_READ', 'TESTIMONIAL_READ')
ON DUPLICATE KEY UPDATE `role_id` = `role_id`;

-- 为GUEST角色分配只读权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `roles` r, `permissions` p 
WHERE r.name = 'GUEST' AND p.name IN ('PROJECT_READ', 'KNOWLEDGE_READ', 'GALLERY_READ')
ON DUPLICATE KEY UPDATE `role_id` = `role_id`;

-- 插入默认管理员用户（密码：admin123，需要通过BCrypt加密存储）
-- 注意：实际部署时请修改默认密码
INSERT INTO `users` (`username`, `email`, `password`, `full_name`, `avatar_url`, `bio`, `is_active`, `created_at`, `updated_at`) VALUES
('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '管理员', NULL, '系统管理员账号', 1, NOW(), NOW()),
('demo', 'demo@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '演示用户', NULL, '演示账号', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `email` = VALUES(`email`);

-- 为admin用户分配ADMIN角色
INSERT INTO `user_roles` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `users` u, `roles` r WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON DUPLICATE KEY UPDATE `user_id` = `user_id`;

-- 为demo用户分配USER角色
INSERT INTO `user_roles` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `users` u, `roles` r WHERE u.username = 'demo' AND r.name = 'USER'
ON DUPLICATE KEY UPDATE `user_id` = `user_id`;

-- ============================================
-- 知识库默认领域（可选）
-- ============================================
INSERT INTO `knowledge_domains` (`name`, `description`, `icon`, `sort_order`, `is_active`, `created_at`, `updated_at`) VALUES
('技术文档', '技术相关文档和教程', 'book', 1, 1, NOW(), NOW()),
('项目笔记', '项目开发笔记和总结', 'folder', 2, 1, NOW(), NOW()),
('学习笔记', '学习过程中的记录', 'graduation-cap', 3, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- ============================================
-- 注意：项目数据应通过以下方式获取：
-- 1. GitHub仓库分析服务自动导入
-- 2. 本地上传代码包解压分析
-- 3. 手动通过管理后台上传
-- 请勿在此文件中添加硬编码的项目数据
-- ============================================