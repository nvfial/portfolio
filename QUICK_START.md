# 快速开始指南

## 🚀 5分钟快速设置

### 步骤1：初始化Git仓库（在项目根目录运行）

```bash
# 初始化仓库
git init

# 添加所有文件
git add .

# 创建第一次提交
git commit -m "Initial commit: Add portfolio project with CI/CD"
```

### 步骤2：在GitHub创建仓库

1. 访问 https://github.com/new
2. 仓库名：`portfolio`（或你喜欢的名字）
3. **不要**勾选任何初始化选项（README、.gitignore等）
4. 点击 **Create repository**

### 步骤3：连接并推送代码

```bash
# 添加远程仓库（替换为你的GitHub用户名和仓库名）
git remote add origin https://github.com/你的用户名/portfolio.git

# 设置主分支
git branch -M main

# 推送代码（会要求输入GitHub用户名和Personal Access Token）
git push -u origin main
```

**需要Personal Access Token？**
- 访问：https://github.com/settings/tokens
- 点击 "Generate new token (classic)"
- 勾选 `repo` 和 `workflow` 权限
- 复制token，在推送时作为密码使用

### 步骤4：配置GitHub Secrets

1. 在GitHub仓库页面 → **Settings** → **Secrets and variables** → **Actions**
2. 点击 **"New repository secret"**
3. 添加以下Secret（至少添加第一个）：

| Secret名称 | 值 | 说明 |
|-----------|-----|------|
| `VITE_API_BASE_URL` | `http://localhost:8080` | 前端API地址 |

**可选Secrets**（如果还没有部署环境，可以稍后添加）：
- `DEV_ENV_URL` - 开发环境URL
- `STAGING_ENV_URL` - 预发布环境URL  
- `PROD_ENV_URL` - 生产环境URL

### 步骤5：验证CI/CD

1. 在GitHub仓库页面，点击 **Actions** 标签
2. 你应该能看到CI/CD工作流
3. 推送任何代码更改都会自动触发CI/CD

## ✅ 完成！

现在你的项目已经配置好CI/CD了！

**详细说明请查看**：`GITHUB_SETUP_GUIDE.md`

