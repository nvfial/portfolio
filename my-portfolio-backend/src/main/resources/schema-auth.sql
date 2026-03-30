-- 用户管理表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    avatar VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    last_login_at DATETIME,
    last_login_ip VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    code VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 权限表
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(100) NOT NULL UNIQUE,
    resource VARCHAR(50),
    action VARCHAR(50),
    description VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户-角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 角色-权限关联表
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 文章版本历史表
CREATE TABLE IF NOT EXISTS article_versions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT NOT NULL,
    version INT NOT NULL,
    content LONGTEXT,
    diff TEXT,
    change_summary JSON,
    editor_id BIGINT NOT NULL,
    edit_reason VARCHAR(200),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (article_id) REFERENCES knowledge_articles(id) ON DELETE CASCADE,
    FOREIGN KEY (editor_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_article_version (article_id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 文章操作日志表
CREATE TABLE IF NOT EXISTS article_operations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT NOT NULL,
    operation VARCHAR(50) NOT NULL,
    from_status VARCHAR(50),
    to_status VARCHAR(50),
    operator_id BIGINT NOT NULL,
    comment TEXT,
    ip_address VARCHAR(50),
    metadata JSON,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (article_id) REFERENCES knowledge_articles(id) ON DELETE CASCADE,
    FOREIGN KEY (operator_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_article_operations (article_id),
    INDEX idx_operator (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 修改知识库文章表添加工作流字段
ALTER TABLE knowledge_articles
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'DRAFT' AFTER is_featured,
    ADD COLUMN IF NOT EXISTS author_id BIGINT AFTER status,
    ADD COLUMN IF NOT EXISTS reviewer_id BIGINT AFTER author_id,
    ADD COLUMN IF NOT EXISTS review_comment TEXT AFTER reviewer_id,
    ADD COLUMN IF NOT EXISTS submitted_at DATETIME AFTER review_comment,
    ADD COLUMN IF NOT EXISTS reviewed_at DATETIME AFTER submitted_at,
    ADD COLUMN IF NOT EXISTS published_at DATETIME AFTER reviewed_at,
    ADD COLUMN IF NOT EXISTS version INT DEFAULT 1 AFTER published_at,
    ADD COLUMN IF NOT EXISTS seo_title VARCHAR(200) AFTER version,
    ADD COLUMN IF NOT EXISTS seo_description VARCHAR(500) AFTER seo_title,
    ADD COLUMN IF NOT EXISTS score DECIMAL(10, 4) DEFAULT 0 AFTER seo_description;

-- 添加外键约束
ALTER TABLE knowledge_articles
    ADD CONSTRAINT fk_article_author FOREIGN KEY (author_id) REFERENCES users(id),
    ADD CONSTRAINT fk_article_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id);

-- 添加索引
CREATE INDEX IF NOT EXISTS idx_article_status ON knowledge_articles(status);
CREATE INDEX IF NOT EXISTS idx_article_author ON knowledge_articles(author_id);
CREATE INDEX IF NOT EXISTS idx_article_published_at ON knowledge_articles(published_at);

-- 插入默认角色
INSERT INTO roles (name, description, code) VALUES
    ('管理员', '系统管理员，拥有所有权限', 'ADMIN'),
    ('审核员', '内容审核员', 'REVIEWER'),
    ('作者', '内容作者', 'AUTHOR'),
    ('普通用户', '普通注册用户', 'USER'),
    ('访客', '未登录访客', 'GUEST') ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 插入默认权限
INSERT INTO permissions (name, code, resource, action, description) VALUES
    ('查看公开内容', 'content:read', 'content', 'read', '查看已发布的内容'),
    ('创建内容', 'content:create', 'content', 'create', '创建新内容'),
    ('编辑自己的内容', 'content:edit:own', 'content', 'edit', '编辑自己创建的内容'),
    ('提交审核', 'content:submit', 'content', 'submit', '提交内容审核'),
    ('审核内容', 'content:review', 'content', 'review', '审核他人提交的内容'),
    ('编辑任何内容', 'content:edit:all', 'content', 'edit', '编辑所有内容'),
    ('删除内容', 'content:delete', 'content', 'delete', '删除内容'),
    ('管理用户', 'user:manage', 'user', 'manage', '管理系统用户'),
    ('管理角色', 'role:manage', 'role', 'manage', '管理系统角色'),
    ('系统设置', 'system:config', 'system', 'config', '系统配置管理') ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 为管理员角色分配所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.code = 'ADMIN'
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 为审核员角色分配审核相关权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.code = 'REVIEWER' AND p.code IN ('content:read', 'content:review', 'content:edit:all')
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 为作者角色分配创作相关权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.code = 'AUTHOR' AND p.code IN ('content:read', 'content:create', 'content:edit:own', 'content:submit')
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 为普通用户分配基础权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.code = 'USER' AND p.code IN ('content:read')
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 创建默认管理员用户 (密码: admin123)
INSERT INTO users (username, email, password, display_name, status) VALUES
    ('admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'ACTIVE')
ON DUPLICATE KEY UPDATE username = username;

-- 为管理员分配角色
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.code = 'ADMIN'
ON DUPLICATE KEY UPDATE user_id = user_id;