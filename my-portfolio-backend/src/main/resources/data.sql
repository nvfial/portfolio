-- ============================================
-- Portfolio 数据库初始化数据
-- 注意：此文件仅在 spring.jpa.hibernate.ddl-auto=create 时执行
-- ============================================

-- 如果表已存在数据，先清空（可选）
-- TRUNCATE TABLE `project_tags`;
-- TRUNCATE TABLE `projects`;
-- TRUNCATE TABLE `contacts`;
-- TRUNCATE TABLE `testimonials`;

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






