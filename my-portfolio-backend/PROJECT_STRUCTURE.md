# 项目结构说明

## 标准分层架构

本项目采用标准的企业级 Java 项目分层架构：

```
com.example.portfolio/
├── entity/          # 实体类（数据库表映射）
├── dto/            # 数据传输对象（Data Transfer Object）
├── vo/             # 视图对象（View Object）
├── repository/     # 数据访问层（Repository 接口）
├── service/        # 服务层接口
│   └── impl/       # 服务层实现类
├── mapper/         # 对象映射（MapStruct）
├── controller/     # 控制器层（REST API）
├── config/         # 配置类
└── util/           # 工具类
```

---

## 各层职责说明

### 1. Entity（实体层）
**位置**: `com.example.portfolio.entity`

**职责**: 
- 对应数据库表结构
- 使用 JPA 注解映射数据库字段
- 包含业务实体的核心属性

**示例**:
- `Project.java` - 项目实体
- `Contact.java` - 联系消息实体
- `Testimonial.java` - 推荐信实体

**特点**:
- 使用 `@Entity` 注解
- 使用 `@Table` 指定表名
- 使用 `@Column` 指定列名和约束
- 使用 `@CreatedDate` 自动填充创建时间

---

### 2. DTO（数据传输对象）
**位置**: `com.example.portfolio.dto`

**职责**:
- 用于 API 请求和响应的数据传输
- 包含验证注解（`@NotBlank`, `@Email` 等）
- 不包含业务逻辑

**示例**:
- `ProjectDTO.java` - 项目数据传输对象
- `ContactDTO.java` - 联系消息数据传输对象
- `TestimonialDTO.java` - 推荐信数据传输对象

**特点**:
- 使用 Jakarta Validation 注解进行数据验证
- 不直接映射数据库表
- 可以包含额外的字段用于数据传输

---

### 3. VO（视图对象）
**位置**: `com.example.portfolio.vo`

**职责**:
- 用于前端展示的数据对象
- 可以包含格式化后的字段（如日期格式化）
- 可以包含额外的展示字段

**示例**:
- `ProjectVO.java` - 项目视图对象
- `ContactVO.java` - 联系消息视图对象
- `TestimonialVO.java` - 推荐信视图对象

**特点**:
- 专门为前端展示设计
- 可以包含计算字段（如 `formattedDate`）
- 可以包含状态字段（如 `isRead`）

---

### 4. Repository（数据访问层）
**位置**: `com.example.portfolio.repository`

**职责**:
- 定义数据访问接口
- 继承 `JpaRepository` 提供基础 CRUD
- 可以定义自定义查询方法

**示例**:
- `ProjectRepository.java` - 项目数据访问接口
- `ContactRepository.java` - 联系消息数据访问接口
- `TestimonialRepository.java` - 推荐信数据访问接口

**特点**:
- 接口继承 `JpaRepository<Entity, ID>`
- 使用 `@Query` 定义自定义查询
- Spring Data JPA 自动实现

---

### 5. Service（服务层）
**位置**: 
- 接口: `com.example.portfolio.service`
- 实现: `com.example.portfolio.service.impl`

**职责**:
- 定义业务逻辑接口
- 实现具体的业务逻辑
- 处理事务管理
- 调用 Repository 进行数据操作

**示例**:
- `ProjectService.java` - 项目服务接口
- `ProjectServiceImpl.java` - 项目服务实现
- `ContactService.java` - 联系消息服务接口
- `ContactServiceImpl.java` - 联系消息服务实现

**特点**:
- 接口定义业务方法
- 实现类使用 `@Service` 注解
- 使用 `@Transactional` 管理事务
- 使用 `@Cacheable` 进行缓存
- 使用 `@Async` 进行异步处理

---

### 6. Mapper（对象映射）
**位置**: `com.example.portfolio.mapper`

**职责**:
- 使用 MapStruct 进行对象转换
- Entity ↔ DTO 转换
- DTO ↔ VO 转换

**示例**:
- `ProjectMapper.java` - 项目对象映射
- `ContactMapper.java` - 联系消息对象映射
- `TestimonialMapper.java` - 推荐信对象映射

**特点**:
- 使用 MapStruct 注解
- 编译时生成实现类
- 高性能的对象转换

---

### 7. Controller（控制器层）
**位置**: `com.example.portfolio.controller`

**职责**:
- 处理 HTTP 请求
- 调用 Service 层
- 返回响应数据
- 参数验证

**示例**:
- `ProjectController.java` - 项目控制器
- `ContactController.java` - 联系消息控制器
- `TestimonialController.java` - 推荐信控制器

**特点**:
- 使用 `@RestController` 注解
- 使用 `@RequestMapping` 定义路径
- 使用 Swagger 注解生成 API 文档
- 返回 `ResponseEntity` 统一响应格式

---

## 数据流转流程

```
前端请求
    ↓
Controller (接收请求，参数验证)
    ↓
DTO (数据传输对象)
    ↓
Service (业务逻辑处理)
    ↓
Mapper (DTO → Entity)
    ↓
Repository (数据访问)
    ↓
Entity (数据库实体)
    ↓
数据库

响应流程（反向）:
数据库 → Entity → Repository → Service → Mapper (Entity → DTO) → Controller → 前端
```

---

## 命名规范

1. **Entity**: 使用名词，如 `Project`, `Contact`
2. **DTO**: 实体名 + DTO，如 `ProjectDTO`, `ContactDTO`
3. **VO**: 实体名 + VO，如 `ProjectVO`, `ContactVO`
4. **Service**: 实体名 + Service，如 `ProjectService`
5. **ServiceImpl**: 实体名 + ServiceImpl，如 `ProjectServiceImpl`
6. **Repository**: 实体名 + Repository，如 `ProjectRepository`
7. **Controller**: 实体名 + Controller，如 `ProjectController`
8. **Mapper**: 实体名 + Mapper，如 `ProjectMapper`

---

## 技术栈

- **Spring Boot**: 应用框架
- **Spring Data JPA**: 数据访问
- **MapStruct**: 对象映射
- **Lombok**: 减少样板代码
- **Jakarta Validation**: 数据验证
- **Spring Cache**: 缓存支持
- **Spring Async**: 异步处理
- **Swagger**: API 文档

---

## 最佳实践

1. **分层清晰**: 每层职责明确，不跨层调用
2. **接口分离**: Service 层使用接口 + 实现类
3. **DTO 验证**: 在 DTO 层进行数据验证
4. **事务管理**: 在 Service 层管理事务
5. **缓存策略**: 在 Service 层使用缓存
6. **异常处理**: 统一异常处理（可扩展）
7. **API 文档**: 使用 Swagger 生成 API 文档

---

## 项目优势

1. ✅ **标准分层**: 符合企业级开发规范
2. ✅ **易于维护**: 职责清晰，代码结构清晰
3. ✅ **易于扩展**: 接口 + 实现，便于扩展
4. ✅ **高性能**: 使用缓存和异步处理
5. ✅ **类型安全**: 使用 DTO 和 VO 分离
6. ✅ **代码生成**: MapStruct 编译时生成映射代码






