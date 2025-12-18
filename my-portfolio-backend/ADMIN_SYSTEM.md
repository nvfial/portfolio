# 管理后台系统使用指南

## 功能概览

已为您创建了完整的管理后台系统，包括：

### ✅ 已完成功能

1. **前端API集成**
   - ✅ 创建了统一的API服务层 (`src/utils/api.js`)
   - ✅ 所有视图已连接后端API
   - ✅ 自动访问追踪 (`src/utils/visitor.js`)

2. **管理后台页面**
   - ✅ 仪表板 (`/admin`) - 显示访问统计、消息统计
   - ✅ 消息管理 (`/admin/messages`) - 管理联系消息和推荐信
   - ✅ 数据大屏 (`/admin/screen`) - 可视化数据展示

3. **后端监控功能**
   - ✅ IP追踪 - 记录每个访问者的IP地址
   - ✅ 访问量统计 - 今日/本周/本月/总访问量
   - ✅ 消息统计 - 联系消息和推荐信统计
   - ✅ 页面访问分析 - 热门页面排行
   - ✅ 项目稳定度 - 数据大屏显示

4. **数据库**
   - ✅ 访问日志表 (`visit_logs`)
   - ✅ 自动记录每次访问

---

## 访问管理后台

### 方式一：通过导航栏
1. 访问网站首页
2. 点击导航栏中的 **"管理后台"** 链接
3. 进入管理后台

### 方式二：直接访问
- 仪表板：`http://localhost:5173/admin`
- 消息管理：`http://localhost:5173/admin/messages`
- 数据大屏：`http://localhost:5173/admin/screen`

---

## 功能详解

### 1. 仪表板 (`/admin`)

**功能**：
- 📊 **访问统计卡片**：今日访问、总访问量、消息总数、独立IP数
- 📈 **访问趋势图**：最近7天的访问趋势（折线图）
- 📋 **热门页面排行**：显示访问最多的页面
- 🌐 **IP访问排行**：显示访问次数最多的IP地址

**数据更新**：每30秒自动刷新

---

### 2. 消息管理 (`/admin/messages`)

**功能**：
- 💬 **联系消息管理**：
  - 查看所有联系消息
  - 显示姓名、邮箱、主题、消息内容
  - 显示提交时间
  - 删除消息功能

- ✨ **推荐信管理**：
  - 查看所有推荐信
  - 显示作者、内容、提交时间
  - 删除推荐信功能

**操作**：
- 点击"删除"按钮可删除消息
- 切换标签页查看不同类型的消息

---

### 3. 数据大屏 (`/admin/screen`)

**功能**：
- 📊 **核心指标展示**：
  - 总访问量（大号显示）
  - 独立IP数
  - 消息总数
  - 项目数量

- 📈 **可视化图表**：
  - 访问趋势图（折线图）
  - IP访问排行（柱状图）
  - 页面访问分布（饼图）

- 🎯 **项目稳定度**：
  - 显示每个项目的稳定度百分比
  - 颜色编码：绿色（95%+）、蓝色（85-95%）、黄色（<85%）
  - 实时更新

**特点**：
- 深色主题，适合大屏展示
- 实时时间显示
- 每30秒自动刷新数据

---

## 访问追踪机制

### 自动追踪
每次用户访问网站时，系统会自动记录：
- ✅ IP地址（通过第三方API获取）
- ✅ 用户代理（浏览器信息）
- ✅ 来源页面（referrer）
- ✅ 访问路径（当前页面）
- ✅ 屏幕分辨率
- ✅ 语言设置
- ✅ 访问时间

### 追踪时机
- 页面加载时
- 页面从隐藏变为可见时

---

## API端点

### 访问统计
- `POST /api/admin/visits/track` - 追踪访问
- `GET /api/admin/stats/visits` - 获取访问统计
- `GET /api/admin/stats/ips` - 获取IP统计
- `GET /api/admin/stats/messages` - 获取消息统计
- `GET /api/admin/stats/projects` - 获取项目统计
- `GET /api/admin/dashboard` - 获取完整仪表板数据

### 消息管理
- `GET /api/contacts` - 获取所有联系消息
- `DELETE /api/contacts/{id}` - 删除联系消息
- `GET /api/testimonials` - 获取所有推荐信
- `DELETE /api/testimonials/{id}` - 删除推荐信

---

## 数据库表结构

### visit_logs 表
```sql
CREATE TABLE `visit_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `ip` VARCHAR(45) NOT NULL,
    `user_agent` TEXT,
    `referrer` VARCHAR(512),
    `language` VARCHAR(50),
    `screen_resolution` VARCHAR(50),
    `path` VARCHAR(255) NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_ip` (`ip`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_path` (`path`)
);
```

---

## 使用步骤

### 1. 启动后端
```bash
cd my-portfolio-backend
mvn spring-boot:run
```

### 2. 启动前端
```bash
cd my-portfolio
npm run dev
```

### 3. 访问管理后台
- 打开浏览器访问：`http://localhost:5173/admin`

### 4. 查看数据
- 访问网站任意页面，系统会自动记录访问
- 在管理后台查看访问统计和消息

---

## 注意事项

1. **IP获取**：使用第三方API (`api.ipify.org`)，如果网络问题可能获取失败
2. **数据刷新**：管理后台每30秒自动刷新，数据大屏实时更新
3. **权限控制**：目前所有API开放访问，生产环境建议添加认证
4. **数据库**：确保MySQL数据库已创建并执行了 `schema.sql`

---

## 技术栈

### 前端
- Vue 3 + Vue Router
- Axios（HTTP请求）
- ECharts（数据可视化）
- GSAP（动画）

### 后端
- Spring Boot 3.3.5
- Spring Data JPA
- MySQL 8.0+
- MapStruct（对象映射）

---

## 下一步优化建议

1. **添加认证**：为管理后台添加登录功能
2. **权限控制**：区分管理员和普通用户
3. **数据导出**：支持导出访问日志和统计数据
4. **实时通知**：新消息到达时实时通知
5. **IP地理位置**：显示IP对应的地理位置
6. **访问分析**：更详细的访问行为分析

---

## 常见问题

**Q: 为什么看不到访问数据？**
A: 确保：
1. 后端服务已启动
2. 数据库连接正常
3. 访问了网站页面（触发追踪）

**Q: IP显示为 "unknown"？**
A: 可能是网络问题导致无法获取IP，检查网络连接

**Q: 数据大屏图表不显示？**
A: 确保已安装 echarts：`npm install echarts`

---

## 总结

✅ 前端已完全连接后端API
✅ 管理后台功能完整
✅ 访问追踪自动运行
✅ 数据大屏实时展示
✅ 消息管理功能完善

现在您可以：
1. 实时监控网站访问情况
2. 查看访问者IP和访问路径
3. 管理联系消息和推荐信
4. 通过数据大屏查看项目稳定度

所有功能已就绪，可以开始使用了！






