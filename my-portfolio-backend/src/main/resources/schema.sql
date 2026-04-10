-- ============================================
-- Portfolio 数据库建表语句（优化版）
-- 数据库: MySQL 8.0+ / PostgreSQL
-- 字符集: utf8mb4
-- ============================================

USE portfolio_db;

-- ============================================
-- 1. 用户管理核心表
-- ============================================
DROP TABLE IF EXISTS `user_roles`;
DROP TABLE IF EXISTS `role_permissions`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `roles`;
DROP TABLE IF EXISTS `permissions`;

CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    `display_name` VARCHAR(100) DEFAULT NULL COMMENT '显示名称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/DISABLED',
    `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE `roles` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL COMMENT '角色代码',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

CREATE TABLE `permissions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    `code` VARCHAR(100) NOT NULL COMMENT '权限代码',
    `resource` VARCHAR(50) DEFAULT NULL COMMENT '资源',
    `action` VARCHAR(50) DEFAULT NULL COMMENT '操作',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

CREATE TABLE `user_roles` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`),
    CONSTRAINT `fk_user_roles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_roles_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

CREATE TABLE `role_permissions` (
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`role_id`, `permission_id`),
    CONSTRAINT `fk_role_permissions_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_role_permissions_permission` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ============================================
-- 2. 项目管理表（扩展版）
-- ============================================
DROP TABLE IF EXISTS `project_images`;
DROP TABLE IF EXISTS `project_tags`;
DROP TABLE IF EXISTS `projects`;

CREATE TABLE `projects` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
    `title` VARCHAR(255) NOT NULL COMMENT '项目标题',
    `description` TEXT NOT NULL COMMENT '项目描述',
    `image_url` VARCHAR(512) DEFAULT NULL COMMENT '封面图片URL',
    `category` VARCHAR(100) DEFAULT NULL COMMENT '项目分类',
    `link` VARCHAR(512) DEFAULT NULL COMMENT '项目链接',
    `github` VARCHAR(512) DEFAULT NULL COMMENT 'GitHub链接',
    `github_repo` VARCHAR(255) DEFAULT NULL COMMENT 'GitHub仓库名(username/repo)',
    `stars` INT DEFAULT 0 COMMENT 'GitHub stars数',
    `forks` INT DEFAULT 0 COMMENT 'GitHub forks数',
    `language` VARCHAR(100) DEFAULT NULL COMMENT '主要编程语言',
    `analysis_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '分析状态: PENDING/SUCCESS/FAILED',
    `analyzed_at` DATETIME DEFAULT NULL COMMENT '分析完成时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_category` (`category`),
    INDEX `idx_github_repo` (`github_repo`),
    INDEX `idx_analysis_status` (`analysis_status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

CREATE TABLE `project_tags` (
    `project_id` BIGINT NOT NULL COMMENT '项目ID',
    `tag` VARCHAR(100) NOT NULL COMMENT '标签名称',
    PRIMARY KEY (`project_id`, `tag`),
    CONSTRAINT `fk_project_tags_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE,
    INDEX `idx_tag` (`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目标签表';

CREATE TABLE `project_images` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `project_id` BIGINT NOT NULL COMMENT '项目ID',
    `url` VARCHAR(512) NOT NULL COMMENT '图片URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_project_images_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE,
    INDEX `idx_project_sort` (`project_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目多图表';

-- ============================================
-- 3. 联系消息表
-- ============================================
DROP TABLE IF EXISTS `contacts`;

CREATE TABLE `contacts` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '联系消息ID',
    `name` VARCHAR(255) NOT NULL COMMENT '联系人姓名',
    `email` VARCHAR(255) NOT NULL COMMENT '联系人邮箱',
    `subject` VARCHAR(255) DEFAULT NULL COMMENT '消息主题',
    `message` TEXT NOT NULL COMMENT '消息内容',
    `status` VARCHAR(20) DEFAULT 'UNREAD' COMMENT '状态: UNREAD/READ/REPLIED',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_email` (`email`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='联系消息表';

-- ============================================
-- 4. 推荐信表
-- ============================================
DROP TABLE IF EXISTS `testimonials`;

CREATE TABLE `testimonials` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '推荐信ID',
    `author` VARCHAR(255) NOT NULL COMMENT '作者姓名',
    `content` TEXT NOT NULL COMMENT '推荐内容',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `company` VARCHAR(100) DEFAULT NULL COMMENT '公司/职位',
    `is_featured` BOOLEAN DEFAULT FALSE COMMENT '是否精选',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_is_featured` (`is_featured`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐信表';

-- ============================================
-- 5. 知识库表
-- ============================================
DROP TABLE IF EXISTS `article_operations`;
DROP TABLE IF EXISTS `article_versions`;
DROP TABLE IF EXISTS `knowledge_relations`;
DROP TABLE IF EXISTS `knowledge_nodes`;
DROP TABLE IF EXISTS `knowledge_articles`;
DROP TABLE IF EXISTS `knowledge_categories`;
DROP TABLE IF EXISTS `knowledge_domains`;

CREATE TABLE `knowledge_domains` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '领域ID',
    `name` VARCHAR(100) NOT NULL COMMENT '领域名称',
    `slug` VARCHAR(100) NOT NULL COMMENT 'URL别名',
    `description` TEXT DEFAULT NULL COMMENT '描述',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标',
    `color` VARCHAR(20) DEFAULT '#667eea' COMMENT '颜色',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库领域表';

CREATE TABLE `knowledge_categories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `domain_id` BIGINT NOT NULL COMMENT '领域ID',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `slug` VARCHAR(100) NOT NULL COMMENT 'URL别名',
    `description` TEXT DEFAULT NULL COMMENT '描述',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标',
    `color` VARCHAR(20) DEFAULT NULL COMMENT '颜色',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_category_domain` FOREIGN KEY (`domain_id`) REFERENCES `knowledge_domains` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_domain_slug` (`domain_id`, `slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库分类表';

CREATE TABLE `knowledge_articles` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `slug` VARCHAR(200) NOT NULL COMMENT 'URL别名',
    `content` LONGTEXT DEFAULT NULL COMMENT '文章内容(Markdown)',
    `summary` TEXT DEFAULT NULL COMMENT '文章摘要',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片',
    `author` VARCHAR(100) DEFAULT 'Admin' COMMENT '作者',
    `author_id` BIGINT DEFAULT NULL COMMENT '作者ID',
    `tags` JSON DEFAULT NULL COMMENT '标签数组',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `like_count` INT DEFAULT 0 COMMENT '点赞次数',
    `read_time` INT DEFAULT 5 COMMENT '阅读时间(分钟)',
    `is_published` BOOLEAN DEFAULT FALSE COMMENT '是否发布',
    `is_featured` BOOLEAN DEFAULT FALSE COMMENT '是否精选',
    `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态: DRAFT/PENDING/PUBLISHED/ARCHIVED',
    `reviewer_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
    `review_comment` TEXT DEFAULT NULL COMMENT '审核意见',
    `submitted_at` DATETIME DEFAULT NULL COMMENT '提交审核时间',
    `reviewed_at` DATETIME DEFAULT NULL COMMENT '审核时间',
    `published_at` DATETIME DEFAULT NULL COMMENT '发布时间',
    `version` INT DEFAULT 1 COMMENT '版本号',
    `seo_title` VARCHAR(200) DEFAULT NULL COMMENT 'SEO标题',
    `seo_description` VARCHAR(500) DEFAULT NULL COMMENT 'SEO描述',
    `score` DECIMAL(10, 4) DEFAULT 0 COMMENT '质量分数',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `knowledge_categories` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_slug` (`slug`),
    INDEX `idx_category` (`category_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库文章表';

CREATE TABLE `knowledge_nodes` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '节点ID',
    `article_id` BIGINT DEFAULT NULL COMMENT '关联文章ID',
    `domain_id` BIGINT DEFAULT NULL COMMENT '关联领域ID',
    `label` VARCHAR(200) NOT NULL COMMENT '节点标签',
    `description` TEXT DEFAULT NULL COMMENT '节点描述',
    `type` ENUM('domain', 'category', 'article', 'keyword', 'reference') DEFAULT 'keyword' COMMENT '节点类型',
    `x_coord` FLOAT DEFAULT NULL COMMENT 'X坐标',
    `y_coord` FLOAT DEFAULT NULL COMMENT 'Y坐标',
    `z_coord` FLOAT DEFAULT NULL COMMENT 'Z坐标',
    `importance` INT DEFAULT 1 COMMENT '重要程度',
    `color` VARCHAR(20) DEFAULT NULL COMMENT '颜色',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_node_article` FOREIGN KEY (`article_id`) REFERENCES `knowledge_articles` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_node_domain` FOREIGN KEY (`domain_id`) REFERENCES `knowledge_domains` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识图谱节点表';

CREATE TABLE `knowledge_relations` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    `source_node_id` BIGINT NOT NULL COMMENT '源节点ID',
    `target_node_id` BIGINT NOT NULL COMMENT '目标节点ID',
    `relation_type` VARCHAR(50) DEFAULT 'relates' COMMENT '关系类型',
    `weight` INT DEFAULT 1 COMMENT '权重',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_relation_source` FOREIGN KEY (`source_node_id`) REFERENCES `knowledge_nodes` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_relation_target` FOREIGN KEY (`target_node_id`) REFERENCES `knowledge_nodes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识图谱关系表';

CREATE TABLE `article_versions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '版本ID',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `version` INT NOT NULL COMMENT '版本号',
    `content` LONGTEXT DEFAULT NULL COMMENT '内容',
    `diff` TEXT DEFAULT NULL COMMENT '差异',
    `change_summary` JSON DEFAULT NULL COMMENT '变更摘要',
    `editor_id` BIGINT NOT NULL COMMENT '编辑者ID',
    `edit_reason` VARCHAR(200) DEFAULT NULL COMMENT '编辑原因',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_version_article` FOREIGN KEY (`article_id`) REFERENCES `knowledge_articles` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_version_editor` FOREIGN KEY (`editor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_article_version` (`article_id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章版本历史表';

CREATE TABLE `article_operations` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '操作ID',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `operation` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `from_status` VARCHAR(50) DEFAULT NULL COMMENT '原状态',
    `to_status` VARCHAR(50) DEFAULT NULL COMMENT '新状态',
    `operator_id` BIGINT NOT NULL COMMENT '操作者ID',
    `comment` TEXT DEFAULT NULL COMMENT '意见',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `metadata` JSON DEFAULT NULL COMMENT '元数据',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_operation_article` FOREIGN KEY (`article_id`) REFERENCES `knowledge_articles` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_operation_operator` FOREIGN KEY (`operator_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_article_operations` (`article_id`),
    INDEX `idx_operator` (`operator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章操作日志表';

-- ============================================
-- 6. 艺术画廊表
-- ============================================
DROP TABLE IF EXISTS `gallery_artworks`;
DROP TABLE IF EXISTS `gallery_collections`;

CREATE TABLE `gallery_collections` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '合集ID',
    `name` VARCHAR(100) NOT NULL COMMENT '合集名称',
    `slug` VARCHAR(100) NOT NULL COMMENT 'URL别名',
    `description` TEXT DEFAULT NULL COMMENT '描述',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片',
    `category` VARCHAR(50) DEFAULT 'digital' COMMENT '分类',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='画廊合集表';

CREATE TABLE `gallery_artworks` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '作品ID',
    `collection_id` BIGINT NOT NULL COMMENT '合集ID',
    `title` VARCHAR(200) NOT NULL COMMENT '作品标题',
    `description` TEXT DEFAULT NULL COMMENT '描述',
    `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
    `thumbnail_url` VARCHAR(500) DEFAULT NULL COMMENT '缩略图URL',
    `artist` VARCHAR(100) DEFAULT 'AI Generated' COMMENT '作者',
    `year` VARCHAR(10) DEFAULT NULL COMMENT '年份',
    `medium` VARCHAR(200) DEFAULT NULL COMMENT '媒介/技术',
    `dimensions` VARCHAR(100) DEFAULT NULL COMMENT '尺寸',
    `tags` JSON DEFAULT NULL COMMENT '标签',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `like_count` INT DEFAULT 0 COMMENT '点赞次数',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_artwork_collection` FOREIGN KEY (`collection_id`) REFERENCES `gallery_collections` (`id`) ON DELETE CASCADE,
    INDEX `idx_collection` (`collection_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='画廊作品表';

-- ============================================
-- 7. 留言评论表
-- ============================================
DROP TABLE IF EXISTS `comments`;

CREATE TABLE `comments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID',
    `user_name` VARCHAR(100) NOT NULL COMMENT '用户名',
    `user_email` VARCHAR(100) DEFAULT NULL COMMENT '用户邮箱',
    `user_website` VARCHAR(200) DEFAULT NULL COMMENT '个人网站',
    `user_avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `target_type` ENUM('article', 'artwork', 'general') DEFAULT 'general' COMMENT '目标类型',
    `target_id` BIGINT DEFAULT NULL COMMENT '目标ID',
    `status` ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '状态',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    `likes_count` INT DEFAULT 0 COMMENT '点赞数',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE,
    INDEX `idx_target` (`target_type`, `target_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ============================================
-- 8. 统计与日志表
-- ============================================
DROP TABLE IF EXISTS `visitor_logs`;
DROP TABLE IF EXISTS `visit_logs`;

CREATE TABLE `visit_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `session_id` VARCHAR(100) DEFAULT NULL COMMENT '会话ID',
    `visitor_id` VARCHAR(100) DEFAULT NULL COMMENT '访客ID',
    `ip` VARCHAR(45) NOT NULL COMMENT 'IP地址',
    `user_agent` TEXT DEFAULT NULL COMMENT '用户代理',
    `referrer` VARCHAR(512) DEFAULT NULL COMMENT '来源页面',
    `language` VARCHAR(50) DEFAULT NULL COMMENT '语言',
    `screen_resolution` VARCHAR(50) DEFAULT NULL COMMENT '屏幕分辨率',
    `path` VARCHAR(255) NOT NULL COMMENT '访问路径',
    `country` VARCHAR(100) DEFAULT NULL COMMENT '国家',
    `region` VARCHAR(100) DEFAULT NULL COMMENT '地区',
    `city` VARCHAR(100) DEFAULT NULL COMMENT '城市',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    PRIMARY KEY (`id`),
    INDEX `idx_ip` (`ip`),
    INDEX `idx_visitor` (`visitor_id`),
    INDEX `idx_path` (`path`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_country` (`country`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问日志表';

CREATE TABLE `visitor_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `session_id` VARCHAR(100) DEFAULT NULL COMMENT '会话ID',
    `visitor_id` VARCHAR(100) DEFAULT NULL COMMENT '访客ID',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    `page_url` VARCHAR(500) DEFAULT NULL COMMENT '页面URL',
    `referer` VARCHAR(500) DEFAULT NULL COMMENT '来源',
    `event_type` VARCHAR(50) DEFAULT 'pageview' COMMENT '事件类型',
    `metadata` JSON DEFAULT NULL COMMENT '元数据',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_page_url` (`page_url`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访客日志表';

-- ============================================
-- 9. 媒体文件表
-- ============================================
DROP TABLE IF EXISTS `media_files`;

CREATE TABLE `media_files` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `filename` VARCHAR(255) NOT NULL COMMENT '文件名',
    `original_filename` VARCHAR(255) NOT NULL COMMENT '原始文件名',
    `file_url` VARCHAR(512) NOT NULL COMMENT '文件URL',
    `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型: image/video/audio/document',
    `mime_type` VARCHAR(100) NOT NULL COMMENT 'MIME类型',
    `file_size` BIGINT NOT NULL COMMENT '文件大小(字节)',
    `width` INT DEFAULT NULL COMMENT '宽度(图片/视频)',
    `height` INT DEFAULT NULL COMMENT '高度(图片/视频)',
    `duration` INT DEFAULT NULL COMMENT '时长(视频/音频,秒)',
    `storage_path` VARCHAR(512) DEFAULT NULL COMMENT '存储路径',
    `uploaded_by` BIGINT DEFAULT NULL COMMENT '上传者ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_file_type` (`file_type`),
    INDEX `idx_uploaded_by` (`uploaded_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='媒体文件表';

-- ============================================
-- 10. 资源共享表
-- ============================================
DROP TABLE IF EXISTS `download_logs`;
DROP TABLE IF EXISTS `resources`;
DROP TABLE IF EXISTS `resource_categories`;

CREATE TABLE `resource_categories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT '分类图标',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '分类描述',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父分类ID',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源分类表';

CREATE TABLE `resources` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资源ID',
    `name` VARCHAR(255) NOT NULL COMMENT '资源名称',
    `description` TEXT DEFAULT NULL COMMENT '资源描述',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `type` VARCHAR(50) NOT NULL COMMENT '资源类型: AUDIO/VIDEO/DOCUMENT/IMAGE/ARCHIVE/SOFTWARE/OTHER',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件格式: pdf, mp4, zip...',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
    `storage_path` VARCHAR(500) DEFAULT NULL COMMENT '存储路径',
    `cdn_url` VARCHAR(500) DEFAULT NULL COMMENT 'CDN下载链接',
    `preview_url` VARCHAR(500) DEFAULT NULL COMMENT '预览链接(图片/音视频)',
    `thumbnail_url` VARCHAR(500) DEFAULT NULL COMMENT '缩略图',
    `download_count` BIGINT DEFAULT 0 COMMENT '下载次数',
    `view_count` BIGINT DEFAULT 0 COMMENT '查看次数',
    `is_premium` BOOLEAN DEFAULT FALSE COMMENT '是否需要积分',
    `required_points` INT DEFAULT 0 COMMENT '所需积分',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源表';

CREATE TABLE `download_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '下载用户ID',
    `resource_id` BIGINT NOT NULL COMMENT '资源ID',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    `downloaded_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下载时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='下载记录表';

-- ============================================
-- 11. 角色系统表
-- ============================================
DROP TABLE IF EXISTS `character_skills`;
DROP TABLE IF EXISTS `character_relationships`;
DROP TABLE IF EXISTS `character_quotes`;
DROP TABLE IF EXISTS `characters`;

CREATE TABLE `characters` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name` VARCHAR(100) NOT NULL COMMENT '角色名称',
    `title` VARCHAR(100) DEFAULT NULL COMMENT '称号/职位',
    `description` TEXT DEFAULT NULL COMMENT '角色描述',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '角色图片',
    `thumbnail_url` VARCHAR(500) DEFAULT NULL COMMENT '缩略图',
    `rarity` VARCHAR(20) DEFAULT 'N' COMMENT '稀有度: N/R/SR/SSR/UR',
    `element` VARCHAR(50) DEFAULT NULL COMMENT '元素属性',
    `weapon_type` VARCHAR(50) DEFAULT NULL COMMENT '武器类型',
    `affiliation` VARCHAR(100) DEFAULT NULL COMMENT '所属势力',
    `birthplace` VARCHAR(100) DEFAULT NULL COMMENT '出生地',
    `birthday` VARCHAR(20) DEFAULT NULL COMMENT '生日',
    `voice_actor` VARCHAR(100) DEFAULT NULL COMMENT '配音演员',
    `stats_hp` INT DEFAULT 0 COMMENT '生命值',
    `stats_attack` INT DEFAULT 0 COMMENT '攻击力',
    `stats_defense` INT DEFAULT 0 COMMENT '防御力',
    `stats_speed` INT DEFAULT 0 COMMENT '速度',
    `theme_preset` VARCHAR(50) DEFAULT 'golden' COMMENT '主题预设',
    `custom_theme_config` TEXT DEFAULT NULL COMMENT '自定义主题JSON',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_rarity` (`rarity`),
    INDEX `idx_element` (`element`),
    INDEX `idx_affiliation` (`affiliation`),
    INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

CREATE TABLE `character_quotes` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '语录ID',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `quote` TEXT NOT NULL COMMENT '语录内容',
    `source` VARCHAR(200) DEFAULT NULL COMMENT '语录出处',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_quote_character` FOREIGN KEY (`character_id`) REFERENCES `characters` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色语录表';

CREATE TABLE `character_relationships` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `related_id` BIGINT NOT NULL COMMENT '关联角色ID',
    `relationship_type` VARCHAR(50) NOT NULL COMMENT '关系类型: family/partner/rival/friend/enemy',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '关系描述',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_rel_character` FOREIGN KEY (`character_id`) REFERENCES `characters` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_rel_related` FOREIGN KEY (`related_id`) REFERENCES `characters` (`id`) ON DELETE CASCADE,
    INDEX `idx_character_id` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色关系表';

CREATE TABLE `character_skills` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '技能ID',
    `character_id` BIGINT NOT NULL COMMENT '角色ID',
    `name` VARCHAR(100) NOT NULL COMMENT '技能名称',
    `description` TEXT DEFAULT NULL COMMENT '技能描述',
    `skill_type` VARCHAR(50) DEFAULT NULL COMMENT '技能类型: passive/active/ultimate',
    `element` VARCHAR(50) DEFAULT NULL COMMENT '元素属性',
    `icon_url` VARCHAR(500) DEFAULT NULL COMMENT '技能图标',
    `effect_data` TEXT DEFAULT NULL COMMENT '效果数据JSON',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_skill_character` FOREIGN KEY (`character_id`) REFERENCES `characters` (`id`) ON DELETE CASCADE,
    INDEX `idx_character_id` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色技能表';
