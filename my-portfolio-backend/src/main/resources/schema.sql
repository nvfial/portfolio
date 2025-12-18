-- ============================================
-- Portfolio 数据库建表语句（更新版）
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS portfolio_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE portfolio_db;

-- ============================================
-- 1. 项目表 (projects)
-- ============================================
DROP TABLE IF EXISTS `project_tags`;
DROP TABLE IF EXISTS `projects`;

CREATE TABLE `projects` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
    `title` VARCHAR(255) NOT NULL COMMENT '项目标题',
    `description` TEXT NOT NULL COMMENT '项目描述',
    `image_url` VARCHAR(512) DEFAULT NULL COMMENT '项目图片URL',
    `category` VARCHAR(100) DEFAULT NULL COMMENT '项目分类',
    `link` VARCHAR(512) DEFAULT NULL COMMENT '项目链接',
    `github` VARCHAR(512) DEFAULT NULL COMMENT 'GitHub链接',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_category` (`category`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- ============================================
-- 2. 项目标签表 (project_tags)
-- ============================================
CREATE TABLE `project_tags` (
    `project_id` BIGINT NOT NULL COMMENT '项目ID',
    `tag` VARCHAR(100) NOT NULL COMMENT '标签名称',
    PRIMARY KEY (`project_id`, `tag`),
    CONSTRAINT `fk_project_tags_project` 
        FOREIGN KEY (`project_id`) 
        REFERENCES `projects` (`id`) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    INDEX `idx_tag` (`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目标签表';

-- ============================================
-- 3. 联系消息表 (contacts)
-- ============================================
DROP TABLE IF EXISTS `contacts`;

CREATE TABLE `contacts` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '联系消息ID',
    `name` VARCHAR(255) NOT NULL COMMENT '联系人姓名',
    `email` VARCHAR(255) NOT NULL COMMENT '联系人邮箱',
    `subject` VARCHAR(255) DEFAULT NULL COMMENT '消息主题',
    `message` TEXT NOT NULL COMMENT '消息内容',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_email` (`email`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='联系消息表';

-- ============================================
-- 4. 推荐信表 (testimonials)
-- ============================================
DROP TABLE IF EXISTS `testimonials`;

CREATE TABLE `testimonials` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '推荐信ID',
    `author` VARCHAR(255) NOT NULL COMMENT '作者姓名',
    `content` TEXT NOT NULL COMMENT '推荐内容',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_author` (`author`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐信表';

-- ============================================
-- 5. 访问日志表 (visit_logs) - 新增
-- ============================================
DROP TABLE IF EXISTS `visit_logs`;

CREATE TABLE `visit_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '访问日志ID',
    `ip` VARCHAR(45) NOT NULL COMMENT '访问者IP',
    `user_agent` TEXT COMMENT '用户代理',
    `referrer` VARCHAR(512) DEFAULT NULL COMMENT '来源页面',
    `language` VARCHAR(50) DEFAULT NULL COMMENT '语言',
    `screen_resolution` VARCHAR(50) DEFAULT NULL COMMENT '屏幕分辨率',
    `path` VARCHAR(255) NOT NULL COMMENT '访问路径',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    PRIMARY KEY (`id`),
    INDEX `idx_ip` (`ip`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_path` (`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问日志表';

-- ============================================
-- 初始化数据（可选）
-- ============================================

-- 插入示例项目数据
INSERT INTO `projects` (`title`, `description`, `image_url`, `category`, `link`, `github`) VALUES
('电商网站', '使用 Vue 3 和 Tailwind CSS 构建的响应式电商平台，支持购物车、支付集成和用户管理。', 'https://via.placeholder.com/400x250?text=E-Commerce', '前端', '#', '#'),
('博客平台', 'Node.js + Express 后端，支持 Markdown 编辑、评论系统和SEO优化。', 'https://via.placeholder.com/400x250?text=Blog+Platform', '全栈', '#', '#'),
('移动记账 App', 'React Native 开发，支持数据同步、图表分析和多币种管理。', 'https://via.placeholder.com/400x250?text=Budget+App', '移动端', '#', '#'),
('设计系统', '完整的设计系统，包含组件库、设计规范和文档。', 'https://via.placeholder.com/400x250?text=Design+System', '设计', '#', '#'),
('实时聊天应用', '使用 Socket.io 构建的实时聊天应用，支持群组、私聊和文件分享。', 'https://via.placeholder.com/400x250?text=Chat+App', '全栈', '#', '#'),
('数据可视化仪表板', '交互式数据可视化平台，支持多种图表类型和数据导出。', 'https://via.placeholder.com/400x250?text=Dashboard', '前端', '#', '#')
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 插入项目标签
INSERT INTO `project_tags` (`project_id`, `tag`) VALUES
(1, 'Vue 3'), (1, 'Tailwind CSS'), (1, 'Pinia'),
(2, 'Node.js'), (2, 'Express'), (2, 'MongoDB'),
(3, 'React Native'), (3, 'Firebase'), (3, 'Chart.js'),
(4, 'Figma'), (4, 'Storybook'), (4, 'Design Tokens'),
(5, 'Socket.io'), (5, 'React'), (5, 'Node.js'),
(6, 'D3.js'), (6, 'Vue 3'), (6, 'TypeScript')
ON DUPLICATE KEY UPDATE `project_id` = VALUES(`project_id`);

-- 插入示例推荐信数据
INSERT INTO `testimonials` (`author`, `content`) VALUES
('张三', '非常专业的前端开发，代码质量很高，沟通也很顺畅。'),
('李四', '项目交付及时，技术实力强，强烈推荐！'),
('王五', '工作认真负责，能够很好地理解需求并实现。'),
('赵六', '技术栈很全面，全栈开发能力突出。'),
('钱七', '代码规范，文档完善，维护起来很方便。'),
('孙八', '响应速度快，问题解决及时，合作愉快。')
ON DUPLICATE KEY UPDATE `author` = VALUES(`author`);
