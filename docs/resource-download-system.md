# 资源共享下载系统 - 技术方案文档

## 1. 系统概述

### 1.1 功能需求
- 提供资源共享下载功能
- 支持多种文件类型：音视频、名著小说、图片、压缩包、程序安装包、MD文档、DOCX文档等
- 用户需登录才能下载
- 预计资源规模：500GB
- 采用对象存储 + CDN 方案

### 1.2 核心目标
- 弹性扩展，适配资源增长
- 低成本运维
- 用户体验流畅（CDN加速）
- 安全可靠（登录验证）

---

## 2. 技术架构

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                         用户层                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │   Web端     │  │  移动端     │  │     管理后台             │  │
│  └──────┬──────┘  └──────┬──────┘  └───────────┬─────────────┘  │
└─────────┼────────────────┼─────────────────────┼────────────────┘
          │                │                     │
          ▼                ▼                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                       API 网关层                                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              Spring Boot 后端服务                        │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │   │
│  │  │  认证服务    │  │  资源服务    │  │  下载服务    │    │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘    │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
          │                │                     │
          ▼                ▼                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                       存储层                                    │
│  ┌─────────────────────┐        ┌─────────────────────────────┐│
  │      MySQL          │        │      对象存储 (OSS/COS/S3)   ││
  │   (元数据存储)      │        │   ┌─────────┬─────────┐       ││
  └─────────────────────┘        │   │  Bucket │  CDN    │       ││
                                 │   └─────────┴─────────┘       ││
                                 └─────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 技术选型

| 层级 | 技术栈 | 说明 |
|------|--------|------|
| 前端框架 | Vue 3 + Element Plus | 组件化开发 |
| 后端框架 | Spring Boot 3 | 稳定可靠 |
| 数据库 | MySQL | 元数据存储 |
| 对象存储 | 阿里云 OSS / 腾讯云 COS | 500GB 资源存储 |
| CDN | 阿里云 CDN / 腾讯云 CDN | 加速分发 |
| 文件传输 | 直链下载 + CDN | 减少服务器压力 |

---

## 3. 数据库设计

### 3.1 核心实体

#### 3.1.1 资源表 (resource)

```sql
CREATE TABLE `resource` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL COMMENT '资源名称',
  `description` TEXT COMMENT '资源描述',
  `category_id` BIGINT COMMENT '分类ID',
  `type` VARCHAR(50) NOT NULL COMMENT '资源类型: AUDIO/VIDEO/DOCUMENT/ARCHIVE/IMAGE/SOFTWARE/OTHER',
  `file_type` VARCHAR(50) COMMENT '文件格式: pdf, mp4, zip...',
  `file_size` BIGINT COMMENT '文件大小(字节)',
  `storage_path` VARCHAR(500) COMMENT '存储路径',
  `cdn_url` VARCHAR(500) COMMENT 'CDN下载链接',
  `preview_url` VARCHAR(500) COMMENT '预览链接(图片/音视频)',
  `thumbnail_url` VARCHAR(500) COMMENT '缩略图',
  `download_count` BIGINT DEFAULT 0 COMMENT '下载次数',
  `view_count` BIGINT DEFAULT 0 COMMENT '查看次数',
  `is_premium` TINYINT DEFAULT 0 COMMENT '是否需要积分',
  `required_points` INT DEFAULT 0 COMMENT '所需积分',
  `is_active` TINYINT DEFAULT 1 COMMENT '是否启用',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `created_by` BIGINT COMMENT '创建者ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_category` (`category_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_is_active` (`is_active`)
);
```

#### 3.1.2 资源分类表 (resource_category)

```sql
CREATE TABLE `resource_category` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `icon` VARCHAR(100) COMMENT '分类图标',
  `description` VARCHAR(500) COMMENT '分类描述',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父分类ID',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `is_active` TINYINT DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 3.1.3 下载记录表 (download_log)

```sql
CREATE TABLE `download_log` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '下载用户ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `user_agent` VARCHAR(500) COMMENT '用户代理',
  `downloaded_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_user` (`user_id`),
  INDEX `idx_resource` (`resource_id`)
);
```

### 3.2 资源类型枚举

| 类型 | 枚举值 | 文件格式示例 | 支持预览 |
|------|--------|--------------|----------|
| 音频 | AUDIO | mp3, wav, flac, aac | ✅ |
| 视频 | VIDEO | mp4, mkv, avi, webm | ✅ |
| 文档 | DOCUMENT | pdf, doc, docx, md, txt | 部分 |
| 压缩包 | ARCHIVE | zip, rar, 7z, tar.gz | ❌ |
| 图片 | IMAGE | jpg, png, gif, webp | ✅ |
| 软件 | SOFTWARE | exe, dmg, apk, deb | ❌ |
| 其他 | OTHER | - | ❌ |

---

## 4. 后端设计

### 4.1 API 接口设计

#### 4.1.1 资源分类

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/resources/categories | 获取全部分类 | 公开 |
| POST | /api/resources/categories | 创建分类 | 管理员 |
| PUT | /api/resources/categories/{id} | 更新分类 | 管理员 |
| DELETE | /api/resources/categories/{id} | 删除分类 | 管理员 |

#### 4.1.2 资源管理

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/resources | 获取资源列表 | 公开 |
| GET | /api/resources/{id} | 获取资源详情 | 登录 |
| POST | /api/resources | 上传资源 | 管理员 |
| PUT | /api/resources/{id} | 更新资源 | 管理员 |
| DELETE | /api/resources/{id} | 删除资源 | 管理员 |

#### 4.1.3 下载接口

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/resources/{id}/download | 获取下载链接 | 登录 |
| POST | /api/resources/{id}/record | 记录下载 | 登录 |

#### 4.1.4 资源搜索

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/resources/search | 搜索资源 | 公开 |
| GET | /api/resources/popular | 热门资源 | 公开 |
| GET | /api/resources/recent | 最新资源 | 公开 |

### 4.2 核心服务设计

#### 4.2.1 ResourceService

```java
public interface ResourceService {
    // CRUD
    Page<ResourceDTO> getResources(ResourceQuery query);
    ResourceDTO getById(Long id);
    ResourceDTO create(ResourceDTO dto);
    ResourceDTO update(Long id, ResourceDTO dto);
    void delete(Long id);
    
    // 文件操作
    String uploadFile(MultipartFile file);
    void deleteFile(String storagePath);
    String getDownloadUrl(Long id);
    String getPreviewUrl(Long id);
    
    // 统计
    void incrementDownloadCount(Long id);
    Page<ResourceDTO> getPopularResources(int limit);
    Page<ResourceDTO> getRecentResources(int limit);
}
```

#### 4.2.2 StorageService (策略模式)

```java
public interface StorageService {
    String upload(MultipartFile file);
    void delete(String path);
    String getUrl(String path);
}

// 实现类
@Component
@Profile("aliyun")
public class AliyunOssStorageService implements StorageService { ... }

@Component
@Profile("local")
public class LocalStorageService implements StorageService { ... }
```

### 4.3 安全设计

#### 4.3.1 下载验证流程

```
用户点击下载 → 检查登录状态 → 验证资源权限 → 生成签名URL → 返回下载链接
```

#### 4.3.2 CDN 防盗链

- 配置 Referer 白名单
- 开启 URL 签名（可选）
- 限制单 IP 下载频率

---

## 5. 前端设计

### 5.1 页面结构

```
资源中心
├── 分类导航 (侧边栏)
│   ├── 全部资源
│   ├── 音视频
│   ├── 文档资料
│   ├── 图片素材
│   ├── 软件工具
│   └── 其他
├── 资源列表 (主区域)
│   ├── 筛选/排序
│   └── 资源卡片
└── 分页

资源详情页
├── 预览区 (图片/音视频)
├── 信息区
│   ├── 名称/描述
│   ├── 文件信息 (大小/格式)
│   └── 下载按钮
└── 相关推荐

管理后台
├── 资源管理
│   ├── 列表/搜索
│   ├── 上传 (拖拽/选择)
│   └── 批量操作
├── 分类管理
└── 下载统计
```

### 5.2 资源卡片组件

```vue
<template>
  <div class="resource-card">
    <div class="thumbnail">
      <img :src="resource.thumbnailUrl || defaultImage" />
      <span class="type-badge">{{ resource.type }}</span>
    </div>
    <div class="info">
      <h3>{{ resource.name }}</h3>
      <p class="meta">
        <span>{{ formatSize(resource.fileSize) }}</span>
        <span>{{ resource.fileType }}</span>
        <span>{{ resource.downloadCount }} 次下载</span>
      </p>
    </div>
  </div>
</template>
```

---

## 6. 存储方案详情

### 6.1 阿里云 OSS 配置

#### 6.1.1 Bucket 设置
- **地域**: 华北2 (北京) 或按需选择
- **存储类型**: 标准存储
- **访问权限**: 私有 (通过 CDN 访问)
- **防盗链**: 开启 Referer 白名单

#### 6.1.2 目录结构

```
resources/
├── audio/          # 音频文件
├── video/          # 视频文件
├── document/       # 文档资料
├── image/          # 图片素材
├── software/       # 软件工具
└── archive/        # 压缩包
```

#### 6.1.3 文件命名规则

```
{type}/{year}/{month}/{uuid}.{ext}
示例: video/2024/04/550e8400-e29b-41d4-a716-446655440000.mp4
```

### 6.2 CDN 配置

- **加速区域**: 中国大陆
- **缓存配置**:
  - 图片/音视频: 长期缓存 (max-age=31536000)
  - 其他文件: 1天缓存
- **回源配置**: 回源到 OSS

### 6.3 成本估算 (500GB 资源)

| 项目 | 配置 | 月费用参考 |
|------|------|------------|
| OSS 存储 | 500GB 标准存储 | ~60 元 |
| CDN 流量 | 假设月下载 100GB | ~20 元 |
| API 请求 | 少量 | 免费 |
| **合计** | | **约 80 元/月** |

---

## 7. 实现计划

### 7.1 第一阶段：基础功能 (预计 2-3 天)

1. **后端**
   - Resource 实体和 Repository
   - ResourceService 和实现
   - 资源 CRUD API
   - OSS 存储服务集成

2. **前端**
   - 资源中心页面 (列表+分类)
   - 资源详情页
   - 下载功能

3. **管理后台**
   - 资源管理页面
   - 上传功能 (简单版)

### 7.2 第二阶段：增强功能 (预计 1-2 天)

1. **预览功能**: 图片/音视频在线预览
2. **搜索功能**: 全文搜索/筛选
3. **统计功能**: 下载统计/热门推荐
4. **上传增强**: 拖拽上传/分片上传

### 7.3 第三阶段：优化 (可选)

1. **下载限速**: 保护带宽
2. **积分系统**: 下载积分机制
3. **会员专享**: VIP 下载权限

---

## 8. 风险与对策

| 风险 | 应对方案 |
|------|----------|
| OSS 费用超预期 | 设置费用告警，开启生命周期管理归档低频访问文件 |
| 下载并发过高 | CDN 弹性扩展，限制单 IP 速率 |
| 文件安全问题 | 私有 Bucket + 签名 URL，URL 短期有效 |
| 存储空间不足 | 定期清理无用资源，提示用户及时处理 |

---

## 9. 总结

本方案采用 **对象存储 + CDN** 架构，适合 500GB 级别的资源共享下载系统。

**优势**:
- 弹性扩展，存储无忧
- CDN 加速，用户体验好
- 成本可控 (~80元/月)
- 安全可靠

**下一步**: 确认方案后即可开始实现。