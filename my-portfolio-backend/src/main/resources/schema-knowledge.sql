<sql>
-- 知识库核心表

CREATE TABLE IF NOT EXISTS knowledge_domains (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    color VARCHAR(20) DEFAULT '#667eea',
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS knowledge_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    domain_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    color VARCHAR(20),
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (domain_id) REFERENCES knowledge_domains(id) ON DELETE CASCADE,
    UNIQUE KEY uk_domain_slug (domain_id, slug)
);

CREATE TABLE IF NOT EXISTS knowledge_articles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(200) UNIQUE NOT NULL,
    content LONGTEXT,
    summary TEXT,
    cover_image VARCHAR(500),
    author VARCHAR(100) DEFAULT 'Admin',
    tags JSON,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    read_time INT DEFAULT 5,
    is_published BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES knowledge_categories(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS knowledge_nodes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id BIGINT,
    domain_id BIGINT,
    label VARCHAR(200) NOT NULL,
    description TEXT,
    type ENUM('domain', 'category', 'article', 'keyword', 'reference') DEFAULT 'keyword',
    x_coord FLOAT,
    y_coord FLOAT,
    z_coord FLOAT,
    importance INT DEFAULT 1,
    color VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (article_id) REFERENCES knowledge_articles(id) ON DELETE SET NULL,
    FOREIGN KEY (domain_id) REFERENCES knowledge_domains(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS knowledge_relations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_node_id BIGINT NOT NULL,
    target_node_id BIGINT NOT NULL,
    relation_type VARCHAR(50) DEFAULT 'relates',
    weight INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (source_node_id) REFERENCES knowledge_nodes(id) ON DELETE CASCADE,
    FOREIGN KEY (target_node_id) REFERENCES knowledge_nodes(id) ON DELETE CASCADE
);

-- 艺术画廊表

CREATE TABLE IF NOT EXISTS gallery_collections (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    cover_image VARCHAR(500),
    category VARCHAR(50) DEFAULT 'digital',
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS gallery_artworks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    collection_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    image_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    artist VARCHAR(100) DEFAULT 'AI Generated',
    year VARCHAR(10),
    medium VARCHAR(200),
    dimensions VARCHAR(100),
    tags JSON,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (collection_id) REFERENCES gallery_collections(id) ON DELETE CASCADE
);

-- 留言评论表

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT,
    user_name VARCHAR(100) NOT NULL,
    user_email VARCHAR(100),
    user_website VARCHAR(200),
    user_avatar VARCHAR(500),
    content TEXT NOT NULL,
    target_type ENUM('article', 'artwork', 'general') DEFAULT 'general',
    target_id BIGINT,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    likes_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE
);

-- 访客统计表

CREATE TABLE IF NOT EXISTS visitor_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(100),
    visitor_id VARCHAR(100),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    page_url VARCHAR(500),
    referer VARCHAR(500),
    event_type VARCHAR(50) DEFAULT 'pageview',
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_page_url (page_url),
    INDEX idx_created_at (created_at)
);

-- 初始数据

INSERT INTO knowledge_domains (name, slug, description, icon, color, sort_order) VALUES
('前端开发', 'frontend', '探索现代前端技术的无限可能', '⚛️', '#667eea', 1),
('后端架构', 'backend', '构建高性能的后端系统', '🚀', '#764ba2', 2),
('人工智能', 'ai', '探索AI的边界与可能', '🤖', '#f093fb', 3),
('设计艺术', 'design', '视觉设计与创意表达', '🎭', '#4facfe', 4);

INSERT INTO knowledge_categories (domain_id, name, slug, description, icon, sort_order) VALUES
(1, 'Vue.js', 'vue', '渐进式JavaScript框架', '💚', 1),
(1, 'React', 'react', '用于构建用户界面的JavaScript库', '⚛️', 2),
(1, 'TypeScript', 'typescript', 'JavaScript的超集', '🔷', 3),
(1, 'CSS动画', 'css-animation', '让界面动起来', '🎨', 4),
(2, 'Spring Boot', 'spring-boot', '简化Spring应用开发', '🍃', 1),
(2, 'Node.js', 'nodejs', 'JavaScript运行时', '🟢', 2),
(2, '微服务', 'microservice', '分布式系统架构', '🏗️', 3),
(3, '机器学习', 'machine-learning', '从数据中学习', '📊', 1),
(3, 'LLM应用', 'llm', '大语言模型实践', '💬', 2),
(4, 'UI设计', 'ui-design', '用户界面设计原则', '🎯', 1),
(4, '3D艺术', '3d-art', 'Three.js创意应用', '🎲', 2);

INSERT INTO knowledge_articles (category_id, title, slug, content, summary, is_published, view_count) VALUES
(1, 'Vue 3 入门指南', 'vue3-getting-started', '# Vue 3 入门指南\n\nVue 3 是 Vue.js 框架的最新版本，带来了一系列新特性和改进。\n\n## 组合式 API\n\nVue 3 引入了组合式 API...', '深入了解 Vue 3 的核心概念与实践', TRUE, 120),
(1, 'Vue 3 进阶技巧', 'vue3-advanced', '# Vue 3 进阶技巧\n\n掌握更高级的 Vue 3 技术...', '掌握更高级的 Vue 3 技术', TRUE, 85),
(2, 'React 深入理解', 'react-deep-dive', '# React 深入理解\n\nReact 是用于构建用户界面的 JavaScript 库...', '深入理解 React 的核心原理', TRUE, 95);

INSERT INTO gallery_collections (name, slug, description, category, sort_order) VALUES
('数字艺术', 'digital-art', '探索数字世界的艺术表达', 'digital', 1),
('3D渲染', '3d-rendering', '三维空间的视觉盛宴', '3d', 2),
('摄影作品', 'photography', '瞬间的永恒记录', 'photo', 3);

INSERT INTO gallery_artworks (collection_id, title, description, image_url, artist, category) VALUES
(1, '数字梦境', '数字世界的梦幻景象', 'https://picsum.photos/800/600?random=1', 'AI Generated', 'digital'),
(1, '赛博空间', '未来科技感3D渲染', 'https://picsum.photos/800/600?random=2', '3D Artist', 'digital'),
(2, '虚拟现实', '沉浸式虚拟体验', 'https://picsum.photos/800/600?random=3', '3D Designer', '3d'),
(3, '光影艺术', '光与影的舞蹈', 'https://picsum.photos/800/600?random=4', 'Photo Artist', 'photo');

INSERT INTO comments (user_name, user_email, content, status, created_at) VALUES
('前端开发者', 'dev@example.com', '太棒了！这个知识库系统设计得非常精美，期待更多内容！', 'approved', '2025-01-20 10:30:00'),
('设计师小王', 'designer@example.com', '艺术展示部分真的很惊艳，让人印象深刻！', 'approved', '2025-01-18 15:20:00'),
('技术爱好者', NULL, '想请教一下这个粒子效果是用什么技术实现的？', 'approved', '2025-01-15 09:45:00');
</sql>
