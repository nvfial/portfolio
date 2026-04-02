# 个人作品集网站 (My Portfolio)

一个功能完整的个人作品集展示网站，采用前后端分离架构，支持项目管理、作品展示、知识库、留言板等功能。

## 项目预览

![Vue 3](https://img.shields.io/badge/Vue-3.5+-42b883?style=flat&logo=vue.js)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=flat&logo=spring)
![Docker](https://img.shields.io/badge/Docker-3.8-2496ED?style=flat&logo=docker)

## 技术栈

### 前端
- **Vue 3** + Composition API
- **Vite** - 构建工具
- **Vue Router 4** - 路由管理
- **Pinia** - 状态管理
- **Axios** - HTTP 客户端
- **Marked** - Markdown 解析
- **Three.js** + **GSAP** - 3D 动画效果
- **ECharts** - 数据可视化

### 后端
- **Spring Boot 3.3** - Java 框架
- **Spring Security** - 安全认证
- **Spring Data JPA** - 数据持久化
- **Spring Data Redis** - 缓存
- **Spring Kafka** - 消息队列
- **MySQL** - 关系数据库
- **JWT** - 身份认证

### 基础设施
- **Docker** + **Docker Compose** - 容器化部署
- **Nginx** - 反向代理
- **PostgreSQL** (可选) - 备用数据库
- **Redis** - 缓存层
- **Neo4j** (可选) - 知识图谱
- **Elasticsearch** (可选) - 搜索引擎

## 项目结构

```
portfolio/
├── my-portfolio/              # Vue 3 前端项目
│   ├── src/
│   │   ├── components/        # 公共组件
│   │   ├── views/             # 页面视图
│   │   │   ├── admin/         # 管理后台
│   │   │   ├── KnowledgeView.vue
│   │   │   └── ...
│   │   ├── stores/            # Pinia 状态管理
│   │   ├── utils/             # 工具函数
│   │   ├── router/            # 路由配置
│   │   └── composables/       # 组合式函数
│   ├── package.json
│   └── vite.config.js
│
├── my-portfolio-backend/      # Spring Boot 后端
│   └── src/main/java/...     # Java 源码
│
├── web/                       # Nuxt 3 SSR (备用前端)
│
├── nginx/                    # Nginx 配置
│
├── docker-compose.yml        # Docker 编排配置
├── Dockerfile                # 前端镜像
├── my-portfolio-backend/Dockerfile  # 后端镜像
└── .github/workflows/        # CI/CD 流水线
```

## 功能特性

### 公开页面
- **首页** - 个人简介、技能展示、3D 粒子动画背景
- **项目展示** - 卡片式布局，支持 Grid/List/画廊视图切换
- **知识库** - Markdown 文章、分类体系、标签系统、目录树
- **角色展示** - 角色一览、详细档案、故事时间线、语录图鉴
- **关于我** - 个人经历、技能树、时间线
- **留言板** - 访客留言、评论互动

### 管理后台
- **仪表盘** - 数据统计、访问日志、图表展示
- **文章管理** - Markdown 编辑器、实时预览、分类管理
- **作品管理** - 图片上传、画廊管理
- **消息管理** - 留言审核、回复
- **数据大屏** - 可视化数据展示

### 技术亮点
- 响应式设计，适配移动端
- 知识库支持领域-分类-文章层级
- Markdown 实时预览编辑器
- 3D 粒子背景动画
- 数据可视化大屏
- **角色主题系统** - 10+ 种预设主题，支持自定义 JSON 配置，每个角色可有独特视觉风格

## 快速开始

### 环境要求
- Node.js 20+
- Java 17+
- Maven 3.8+
- Docker Desktop

### 本地开发

1. **克隆项目**
```bash
git clone https://github.com/your-repo/portfolio.git
cd portfolio
```

2. **启动前端**
```bash
cd my-portfolio
npm install
npm run dev
```

3. **启动后端**
```bash
cd my-portfolio-backend
mvn spring-boot:run
```

访问 http://localhost:5173

### Docker 部署

```bash
# 启动所有服务
docker-compose up -d

# 仅启动核心服务
docker-compose up -d web backend db redis

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

## 环境变量

### 前端 (.env.development)
```
VITE_API_BASE_URL=http://localhost:8080
```

### 后端 (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/portfolio
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

## API 接口

| 模块 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 认证 | POST | /api/auth/login | 用户登录 |
| 项目 | GET | /api/projects | 获取项目列表 |
| 知识库 | GET | /api/knowledge/domains | 获取领域列表 |
| 文章 | GET | /api/knowledge/articles | 获取文章列表 |
| 画廊 | GET | /api/gallery/collections | 获取作品集 |
| 留言 | POST | /api/contact | 提交留言 |
| 角色 | GET | /api/characters | 获取角色列表 |
| 角色 | GET | /api/characters/{id} | 获取角色详情 |
| 角色 | GET | /api/characters/active | 获取激活角色 |

## 开发指南

### 添加新页面
1. 在 `src/views/` 创建 Vue 组件
2. 在 `src/router/index.js` 添加路由
3. 在 `Navbar.vue` 添加导航项

### 添加 API
1. 后端创建 Controller/Service/Repository
2. 前端在 `src/utils/api.js` 添加请求方法

## 部署说明

### 生产环境
```bash
# 构建前端
npm run build

# 使用 Nginx 部署
nginx -c nginx.conf
```

### CI/CD
项目包含 GitHub Actions 自动化流水线：
- `ci-cd.yml` - 持续集成/部署
- `release.yml` - 版本发布
- `nightly-build.yml` - 每日构建

## 许可证

MIT License

## 贡献指南

欢迎提交 Issue 和 Pull Request！