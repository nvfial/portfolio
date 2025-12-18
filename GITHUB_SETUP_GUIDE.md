# GitHub 仓库设置完整指南

本指南将一步步教你如何创建GitHub仓库、推送代码并配置CI/CD所需的Secrets。

## 📋 前置准备

确保你已经安装了：
- ✅ Git（如果没有，请访问 https://git-scm.com/downloads 下载）
- ✅ GitHub账号（你已经有了）

## 第一步：初始化本地Git仓库

### 1.1 检查Git是否已安装

打开终端（PowerShell或CMD），运行：

```bash
git --version
```

如果显示版本号，说明已安装。如果没有，请先安装Git。

### 1.2 配置Git用户信息（如果还没配置）

```bash
git config --global user.name "你的名字"
git config --global user.email "你的邮箱@example.com"
```

**注意**：邮箱应该使用你GitHub账号绑定的邮箱。

### 1.3 初始化Git仓库

在项目根目录（`e:\vue3practice\portfolio`）运行：

```bash
git init
```

## 第二步：创建GitHub仓库

### 2.1 登录GitHub

1. 打开浏览器，访问 https://github.com
2. 登录你的GitHub账号

### 2.2 创建新仓库

1. 点击右上角的 **"+"** 按钮
2. 选择 **"New repository"**（新建仓库）

### 2.3 填写仓库信息

- **Repository name**（仓库名称）：`portfolio` 或 `my-portfolio`（你可以自己命名）
- **Description**（描述）：可选，例如 "My Portfolio Project with CI/CD"
- **Visibility**（可见性）：
  - ✅ **Public**（公开）- 推荐，免费且功能完整
  - ⚠️ **Private**（私有）- 需要付费账号才能使用某些CI/CD功能
- **不要勾选**以下选项（因为我们已经有代码了）：
  - ❌ Add a README file
  - ❌ Add .gitignore
  - ❌ Choose a license

### 2.4 创建仓库

点击 **"Create repository"**（创建仓库）按钮

### 2.5 复制仓库地址

创建成功后，GitHub会显示一个页面，上面有仓库地址，类似：
```
https://github.com/你的用户名/portfolio.git
```

**复制这个地址**，稍后会用到。

## 第三步：连接本地仓库和GitHub仓库

### 3.1 添加远程仓库

在项目根目录运行（将 `你的用户名` 和 `仓库名` 替换为实际值）：

```bash
git remote add origin https://github.com/你的用户名/仓库名.git
```

例如：
```bash
git remote add origin https://github.com/zhangsan/portfolio.git
```

### 3.2 验证远程仓库

```bash
git remote -v
```

应该显示你刚才添加的远程仓库地址。

## 第四步：提交代码到本地仓库

### 4.1 查看当前状态

```bash
git status
```

### 4.2 添加所有文件到暂存区

```bash
git add .
```

### 4.3 创建第一次提交

```bash
git commit -m "Initial commit: Add portfolio project with CI/CD configuration"
```

### 4.4 设置默认分支为main（如果还没有）

```bash
git branch -M main
```

## 第五步：推送代码到GitHub

### 5.1 推送代码

```bash
git push -u origin main
```

**注意**：第一次推送时，GitHub可能会要求你输入用户名和密码：
- **用户名**：你的GitHub用户名
- **密码**：**不是**你的GitHub登录密码，而是 **Personal Access Token (PAT)**

### 5.2 如果提示需要认证

如果推送时提示需要认证，你需要创建一个Personal Access Token：

#### 创建Personal Access Token步骤：

1. 登录GitHub
2. 点击右上角头像 → **Settings**（设置）
3. 左侧菜单最下方，点击 **Developer settings**
4. 点击 **Personal access tokens** → **Tokens (classic)**
5. 点击 **Generate new token** → **Generate new token (classic)**
6. 填写信息：
   - **Note**（备注）：例如 "Portfolio Project"
   - **Expiration**（过期时间）：选择合适的时间，建议90天或更长
   - **Select scopes**（选择权限）：至少勾选：
     - ✅ `repo`（完整仓库访问权限）
     - ✅ `workflow`（GitHub Actions工作流权限）
7. 点击 **Generate token**（生成令牌）
8. **重要**：立即复制生成的token（只显示一次！）
9. 在推送时，密码处粘贴这个token

### 5.3 验证推送成功

推送成功后，刷新GitHub仓库页面，你应该能看到所有代码文件。

## 第六步：配置GitHub Secrets

### 6.1 进入仓库设置

1. 在GitHub仓库页面，点击 **Settings**（设置）标签
2. 左侧菜单找到 **Secrets and variables** → **Actions**

### 6.2 添加Secrets

点击 **"New repository secret"**（新建仓库密钥）按钮，逐个添加以下Secrets：

#### 必需的基础Secrets：

1. **VITE_API_BASE_URL**
   - **Name**: `VITE_API_BASE_URL`
   - **Value**: `http://localhost:8080`（开发环境）或你的实际API地址
   - **说明**: 前端API基础URL

#### 可选的环境URL Secrets（如果还没有部署环境，可以先不填）：

2. **DEV_ENV_URL**
   - **Name**: `DEV_ENV_URL`
   - **Value**: `http://dev.example.com`（替换为你的开发环境URL）
   - **说明**: 开发环境URL

3. **STAGING_ENV_URL**
   - **Name**: `STAGING_ENV_URL`
   - **Value**: `http://staging.example.com`（替换为你的预发布环境URL）
   - **说明**: 预发布环境URL

4. **PROD_ENV_URL**
   - **Name**: `PROD_ENV_URL`
   - **Value**: `http://example.com`（替换为你的生产环境URL）
   - **说明**: 生产环境URL

#### 可选的Kubernetes配置（如果使用K8s部署）：

5. **DEV_KUBECONFIG**
   - **Name**: `DEV_KUBECONFIG`
   - **Value**: （你的K8s配置文件内容）
   - **说明**: 开发环境Kubernetes配置

6. **STAGING_KUBECONFIG**
   - **Name**: `STAGING_KUBECONFIG`
   - **Value**: （你的K8s配置文件内容）
   - **说明**: 预发布环境Kubernetes配置

7. **PROD_KUBECONFIG**
   - **Name**: `PROD_KUBECONFIG`
   - **Value**: （你的K8s配置文件内容）
   - **说明**: 生产环境Kubernetes配置

#### 可选的安全扫描Token：

8. **SNYK_TOKEN**（可选）
   - **Name**: `SNYK_TOKEN`
   - **Value**: （如果你有Snyk账号的token）
   - **说明**: Snyk安全扫描Token

### 6.3 添加Secret的步骤

对于每个Secret：
1. 点击 **"New repository secret"**
2. 在 **Name** 字段输入密钥名称（例如：`VITE_API_BASE_URL`）
3. 在 **Secret** 字段输入密钥值
4. 点击 **"Add secret"**

**注意**：
- Secret的值一旦保存就无法再查看，只能更新或删除
- Secret在CI/CD工作流中会自动可用，无需额外配置

## 第七步：验证CI/CD配置

### 7.1 查看Actions标签

1. 在GitHub仓库页面，点击 **Actions**（操作）标签
2. 你应该能看到CI/CD工作流文件

### 7.2 触发第一次CI/CD运行

有几种方式可以触发：

#### 方式1：推送新代码
```bash
# 修改任意文件，然后
git add .
git commit -m "Trigger CI/CD"
git push
```

#### 方式2：手动触发
1. 在GitHub仓库页面，点击 **Actions** 标签
2. 选择 **"CI/CD Pipeline"** 工作流
3. 点击 **"Run workflow"**（运行工作流）
4. 选择分支（选择 `main`）
5. 点击 **"Run workflow"**

### 7.3 查看运行结果

1. 在Actions页面，点击运行中的工作流
2. 可以看到各个步骤的执行情况
3. 绿色✅表示成功，红色❌表示失败

## 常见问题排查

### Q1: 推送时提示"remote: Support for password authentication was removed"

**解决方案**：需要使用Personal Access Token，参考第五步的5.2节。

### Q2: 推送时提示"Permission denied"

**解决方案**：
1. 检查远程仓库地址是否正确
2. 确认Personal Access Token有正确的权限
3. 尝试重新添加远程仓库：
   ```bash
   git remote remove origin
   git remote add origin https://github.com/你的用户名/仓库名.git
   ```

### Q3: CI/CD工作流运行失败

**解决方案**：
1. 查看Actions页面的错误日志
2. 检查Secrets是否都已正确配置
3. 确认工作流文件路径正确：`.github/workflows/ci-cd.yml`

### Q4: 如何更新Secrets

**解决方案**：
1. 进入 Settings → Secrets and variables → Actions
2. 找到要更新的Secret
3. 点击右侧的 **"Update"**（更新）按钮
4. 输入新值并保存

## 下一步

完成以上步骤后，你的项目就具备了完整的CI/CD功能：

1. ✅ 代码推送到GitHub会自动触发CI/CD流程
2. ✅ 自动运行测试和代码检查
3. ✅ 自动构建Docker镜像
4. ✅ 可以配置自动部署到不同环境

## 需要帮助？

如果遇到问题：
1. 查看GitHub Actions的日志输出
2. 检查本文档的"常见问题排查"部分
3. 查看 `CI_CD_README.md` 获取更多CI/CD相关信息

---

**提示**：建议将本指南保存为书签，方便后续参考。

