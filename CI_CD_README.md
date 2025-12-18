# CI/CD 配置文档

本项目配置了完整的企业级CI/CD流程，支持自动化构建、测试、安全扫描和部署。

## 📋 目录结构

```
.
├── .github/
│   └── workflows/
│       ├── ci-cd.yml          # 主CI/CD工作流
│       ├── release.yml         # 发布工作流
│       └── nightly-build.yml   # 夜间构建
├── scripts/
│   ├── deploy.sh              # 部署脚本
│   └── health-check.sh        # 健康检查脚本
├── docker-compose.yml         # 生产环境Docker Compose
├── docker-compose.dev.yml     # 开发环境Docker Compose
├── my-portfolio/
│   ├── Dockerfile             # 前端Dockerfile
│   └── nginx.conf             # Nginx配置
└── my-portfolio-backend/
    └── Dockerfile             # 后端Dockerfile
```

## 🚀 功能特性

### 1. 自动化CI/CD流程

- ✅ **代码质量检查**: 自动运行代码检查工具
- ✅ **自动化测试**: 单元测试和集成测试
- ✅ **安全扫描**: 依赖安全扫描和代码安全扫描
- ✅ **多环境部署**: 支持dev、staging、production环境
- ✅ **Docker镜像构建**: 自动构建和推送Docker镜像
- ✅ **健康检查**: 部署后自动健康检查
- ✅ **回滚机制**: 部署失败自动回滚

### 2. 工作流说明

#### 主CI/CD工作流 (`ci-cd.yml`)

触发条件：
- Push到 `main`、`develop` 或 `release/**` 分支
- 创建Pull Request
- 手动触发（workflow_dispatch）

工作流程：
1. **代码质量检查**: 运行代码检查工具
2. **前端构建测试**: 安装依赖、运行测试、构建前端
3. **后端构建测试**: 运行单元测试、构建后端
4. **安全扫描**: 依赖和代码安全扫描
5. **Docker镜像构建**: 构建并推送镜像到容器注册表
6. **环境部署**: 根据分支自动部署到对应环境
7. **通知**: 发送部署结果通知

#### 发布工作流 (`release.yml`)

用于创建正式版本发布：
- 创建Git标签时自动触发
- 生成变更日志
- 创建GitHub Release

#### 夜间构建 (`nightly-build.yml`)

每日自动运行：
- 完整构建和测试
- 生成测试报告
- 确保代码库稳定性

## 🔧 配置说明

### GitHub Secrets配置

在GitHub仓库设置中添加以下Secrets：

```bash
# 容器注册表
GITHUB_TOKEN              # 自动提供，用于推送镜像

# 环境URL
DEV_ENV_URL              # 开发环境URL
STAGING_ENV_URL          # 预发布环境URL
PROD_ENV_URL             # 生产环境URL

# Kubernetes配置（如果使用K8s部署）
DEV_KUBECONFIG           # 开发环境K8s配置
STAGING_KUBECONFIG       # 预发布环境K8s配置
PROD_KUBECONFIG          # 生产环境K8s配置

# 安全扫描（可选）
SNYK_TOKEN               # Snyk安全扫描Token

# 前端环境变量
VITE_API_BASE_URL        # API基础URL
```

### 环境变量配置

1. 复制环境变量示例文件：
```bash
cp .env.example .env.dev
cp .env.example .env.staging
cp .env.example .env.prod
```

2. 根据各环境修改配置：
- `.env.dev`: 开发环境配置
- `.env.staging`: 预发布环境配置
- `.env.prod`: 生产环境配置

## 📦 本地部署

### 使用Docker Compose

#### 生产环境
```bash
docker-compose up -d
```

#### 开发环境
```bash
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

### 使用部署脚本

```bash
# 部署到开发环境
./scripts/deploy.sh dev

# 部署到预发布环境
./scripts/deploy.sh staging

# 部署到生产环境
./scripts/deploy.sh production v1.0.0
```

### 健康检查

```bash
./scripts/health-check.sh
```

## 🔄 部署流程

### 开发环境 (develop分支)
- 自动触发部署
- 用于开发测试

### 预发布环境 (main分支)
- 自动触发部署
- 运行集成测试
- 用于预发布验证

### 生产环境
- 需要手动触发
- 需要确认操作
- 包含健康检查和回滚机制

## 🐳 Docker镜像

### 构建镜像

```bash
# 前端
cd my-portfolio
docker build -t portfolio-frontend:latest .

# 后端
cd my-portfolio-backend
docker build -t portfolio-backend:latest .
```

### 镜像标签

CI/CD会自动为镜像打标签：
- `latest`: 默认分支的最新版本
- `{branch}-{sha}`: 分支和提交SHA
- `v{version}`: 版本标签

## 🔒 安全特性

1. **依赖扫描**: 自动扫描npm和Maven依赖漏洞
2. **代码扫描**: 使用Super Linter进行代码安全检查
3. **非root用户**: Docker容器使用非root用户运行
4. **安全头**: Nginx配置了安全响应头
5. **密钥管理**: 使用GitHub Secrets管理敏感信息

## 📊 监控和日志

- 健康检查端点：
  - 前端: `http://localhost/health`
  - 后端: `http://localhost:8080/actuator/health`

- 查看日志：
```bash
# Docker Compose
docker-compose logs -f

# 单个服务
docker-compose logs -f backend
docker-compose logs -f frontend
```

## 🛠️ 故障排查

### 构建失败
1. 检查GitHub Actions日志
2. 验证环境变量配置
3. 检查依赖版本兼容性

### 部署失败
1. 检查健康检查端点
2. 查看容器日志
3. 验证环境变量
4. 检查网络连接

### 回滚
```bash
# 使用部署脚本回滚
./scripts/deploy.sh production <previous-version>
```

## 📝 最佳实践

1. **分支策略**:
   - `develop`: 开发分支
   - `main`: 主分支（预发布）
   - `release/**`: 发布分支
   - `feature/**`: 功能分支

2. **提交信息**:
   - 使用清晰的提交信息
   - 遵循约定式提交规范

3. **测试**:
   - 确保所有测试通过
   - 保持测试覆盖率

4. **版本管理**:
   - 使用语义化版本
   - 创建Git标签进行发布

## 🔗 相关链接

- [GitHub Actions文档](https://docs.github.com/en/actions)
- [Docker文档](https://docs.docker.com/)
- [Docker Compose文档](https://docs.docker.com/compose/)

## 📞 支持

如有问题，请：
1. 查看GitHub Actions日志
2. 检查本文档的故障排查部分
3. 联系DevOps团队

