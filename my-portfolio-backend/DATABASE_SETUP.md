# 数据库设置指南

## 数据库信息

- **数据库类型**: MySQL 8.0+
- **数据库名称**: `portfolio_db`
- **字符集**: `utf8mb4`
- **排序规则**: `utf8mb4_unicode_ci`

---

## 方式一：手动执行 SQL 脚本（推荐）

### 1. 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS portfolio_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;
```

### 2. 执行建表语句

使用 MySQL 客户端（如 MySQL Workbench、Navicat、命令行）执行：

```bash
mysql -u root -p portfolio_db < src/main/resources/schema.sql
```

或者在 MySQL 客户端中：

```sql
USE portfolio_db;
SOURCE src/main/resources/schema.sql;
```

### 3. 验证表结构

```sql
-- 查看所有表
SHOW TABLES;

-- 查看表结构
DESC projects;
DESC project_tags;
DESC contacts;
DESC testimonials;
```

---

## 方式二：使用 Spring Boot 自动创建（开发环境）

### 配置说明

在 `application.properties` 中：

```properties
# 自动创建表结构（首次运行）
spring.jpa.hibernate.ddl-auto=create

# 或者更新表结构（推荐）
spring.jpa.hibernate.ddl-auto=update

# 显示 SQL 语句
spring.jpa.show-sql=true
```

### 注意事项

- `create`: 每次启动都会删除并重新创建表（**会丢失数据**）
- `update`: 根据实体类自动更新表结构（**推荐用于开发**）
- `validate`: 只验证表结构，不修改
- `none`: 不执行任何操作（**推荐用于生产环境**）

---

## 表结构说明

### 1. projects（项目表）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| title | VARCHAR(255) | 项目标题（非空） |
| description | TEXT | 项目描述（非空） |
| image_url | VARCHAR(512) | 项目图片URL |
| category | VARCHAR(100) | 项目分类 |
| link | VARCHAR(512) | 项目链接 |
| github | VARCHAR(512) | GitHub链接 |
| created_at | TIMESTAMP | 创建时间（自动） |

**索引**:
- PRIMARY KEY (`id`)
- INDEX `idx_category` (`category`)
- INDEX `idx_created_at` (`created_at`)

---

### 2. project_tags（项目标签表）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| project_id | BIGINT | 项目ID（外键） |
| tag | VARCHAR(100) | 标签名称 |

**索引**:
- PRIMARY KEY (`project_id`, `tag`)
- FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE
- INDEX `idx_tag` (`tag`)

**说明**: 使用 `@ElementCollection` 注解，JPA 会自动创建此表。

---

### 3. contacts（联系消息表）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| name | VARCHAR(255) | 联系人姓名（非空） |
| email | VARCHAR(255) | 联系人邮箱（非空） |
| subject | VARCHAR(255) | 消息主题 |
| message | TEXT | 消息内容（非空） |
| created_at | TIMESTAMP | 创建时间（自动） |

**索引**:
- PRIMARY KEY (`id`)
- INDEX `idx_email` (`email`)
- INDEX `idx_created_at` (`created_at`)

---

### 4. testimonials（推荐信表）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| author | VARCHAR(255) | 作者姓名（非空） |
| content | TEXT | 推荐内容（非空） |
| created_at | TIMESTAMP | 创建时间（自动） |

**索引**:
- PRIMARY KEY (`id`)
- INDEX `idx_created_at` (`created_at`)
- INDEX `idx_author` (`author`)

---

## 初始化数据

`schema.sql` 文件已包含示例数据，包括：

- 6 个示例项目
- 18 条项目标签
- 6 条推荐信

如果需要清空并重新初始化数据，可以执行：

```sql
TRUNCATE TABLE `project_tags`;
TRUNCATE TABLE `projects`;
TRUNCATE TABLE `contacts`;
TRUNCATE TABLE `testimonials`;

-- 然后重新执行 schema.sql 中的 INSERT 语句
```

---

## 数据库连接配置

在 `application.properties` 中配置：

```properties
# 数据库连接
spring.datasource.url=jdbc:mysql://localhost:3306/portfolio_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA 配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true
```

---

## 常见问题

### 1. 字符编码问题

确保数据库和表都使用 `utf8mb4` 字符集：

```sql
ALTER DATABASE portfolio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 时区问题

在连接 URL 中添加时区参数：

```
serverTimezone=Asia/Shanghai
```

### 3. 外键约束

`project_tags` 表有外键约束，删除项目时会自动删除相关标签（CASCADE）。

### 4. 自动时间戳

`created_at` 字段使用 `@CreatedDate` 注解，JPA 会自动填充创建时间。

---

## 生产环境建议

1. **禁用自动创建表**:
   ```properties
   spring.jpa.hibernate.ddl-auto=none
   ```

2. **手动执行建表脚本**: 使用 `schema.sql` 手动创建表结构

3. **备份数据**: 定期备份数据库

4. **索引优化**: 根据查询需求添加合适的索引

5. **字符集**: 确保使用 `utf8mb4` 支持 emoji 等特殊字符

---

## 验证数据库连接

启动应用后，检查日志中是否有数据库连接错误。如果连接成功，会看到：

```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

如果看到表创建日志，说明表结构已成功创建。






