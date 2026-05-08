# mywebsite 项目结构说明

## 目录结构

```
mywebsite/
├── pom.xml                                      # Maven 项目配置，定义依赖库
└── src/main/
    ├── java/com/example/mywebsite/
    │   ├── MywebsiteApplication.java            # 程序入口，启动点
    │   ├── GlobalControllerAdvice.java          # 全局控制器增强，统一注入 favicon
    │   ├── DataInitializer.java                  # 应用启动时初始化默认数据
    │   │
    │   ├── User.java                             # 用户实体（数据库表 user）
    │   ├── UserRepository.java                   # 用户数据库操作接口
    │   │
    │   ├── Group.java                            # 分组实体（数据库表 user_group）
    │   ├── GroupRepository.java                  # 分组数据库操作接口
    │   ├── GroupService.java                     # 分组业务逻辑服务
    │   ├── GroupController.java                  # 分组管理请求处理（后台）
    │   │
    │   ├── UserGroup.java                        # 用户-分组关联实体（数据库表 user_user_group）
    │   ├── UserGroupRepository.java              # 用户-分组关联数据库操作接口
    │   │
    │   ├── GroupPermission.java                  # 分组-页面权限关联实体（数据库表 group_permission）
    │   ├── GroupPermissionRepository.java        # 分组-页面权限数据库操作接口
    │   │
    │   ├── Page.java                             # 页面实体（数据库表 page），记录可授权页面
    │   ├── PageRepository.java                   # 页面数据库操作接口
    │   │
    │   ├── UserController.java                   # 用户认证、主页请求处理
    │   ├── PermissionController.java             # 权限管理请求处理（后台）
    │   ├── PermissionService.java                # 权限业务逻辑服务
    │   │
    │   └── resources/
    │       ├── application.properties            # 数据库、端口、Thymeleaf 等配置
    │       └── templates/
    │           ├── _head.html                    # 页面头部片段（meta + favicon）
    │           ├── layout.html                   # 统一布局模板（后台管理页面共用）
    │           ├── login.html                    # 登录页面
    │           ├── main.html                     # 首页（登录后主页）
    │           ├── userManage.html               # 用户管理页面
    │           ├── userForm.html                 # 用户新增/编辑表单页面
    │           └── admin/
    │               ├── groupManage.html           # 分组管理页面
    │               ├── groupDetail.html          # 分组详情页面
    │               └── permissionManage.html     # 权限管理页面
```

---

## 各文件功能说明

### 程序入口

**MywebsiteApplication.java**
- 程序启动入口，`main()` 方法所在类
- `@SpringBootApplication` 注解标记为 Spring Boot 应用
- 启动后自动扫描同包及子包下的组件（Controller、Service、Repository 等）

---

### 全局配置

**GlobalControllerAdvice.java**
- `@ControllerAdvice` 注解，作用于所有 Controller
- `@ModelAttribute("faviconLink")` 统一向每个 Model 注入 favicon 数据
- 所有页面通过 `th:href="${faviconLink}"` 引用，全局只需维护此处

**DataInitializer.java**
- 实现 `CommandLineRunner`，应用启动完成后自动执行
- 初始化默认管理员账号（admin / 123456）
- 初始化系统默认页面记录

**application.properties**
- Spring Boot 配置文件
- 包含数据库路径（mywebsite.db）、端口（8080）、Thymeleaf 模板配置

---

### 实体类（Entity）

| 文件 | 数据库表 | 说明 |
|---|---|---|
| User.java | user | 用户实体，含用户名、密码、管理员标识等 |
| Group.java | user_group | 分组实体，含名称、描述等 |
| UserGroup.java | user_user_group | 用户-分组多对多关联 |
| GroupPermission.java | group_permission | 分组-页面权限多对多关联 |
| Page.java | page | 页面实体，用于权限管理，记录可授权页面路径 |

---

### Repository（数据访问层）

| 文件 | 操作实体 | 说明 |
|---|---|---|
| UserRepository.java | User | 用户查询，如按用户名查找 |
| GroupRepository.java | Group | 分组查询 |
| UserGroupRepository.java | UserGroup | 查询某用户关联的分组 |
| GroupPermissionRepository.java | GroupPermission | 查询某分组关联的页面权限 |
| PageRepository.java | Page | 页面查询，如按路径查找 |

---

### Service（业务逻辑层）

| 文件 | 说明 |
|---|---|
| GroupService.java | 分组相关业务逻辑 |
| PermissionService.java | 权限相关业务逻辑（分组-页面权限关联管理） |

---

### Controller（请求处理层）

| 文件 | 处理路由 | 说明 |
|---|---|---|
| UserController.java | `/`, `/login`, `/logout`, `/main` | 登录认证、主页、退出 |
| GroupController.java | `/admin/groups`, `/admin/group/*` | 分组列表、详情、新增、编辑 |
| PermissionController.java | `/admin/users`, `/admin/permissions` | 用户管理、权限分配 |

---

### 模板文件（Thymeleaf HTML）

| 文件 | 说明 |
|---|---|
| `_head.html` | 页面头部片段，被各模板 include，含 favicon |
| `layout.html` | 后台管理统一布局，含侧边导航栏、顶栏、退出按钮 |
| `login.html` | 登录页，表单提交到 `/login` |
| `main.html` | 首页，渐变 Hero 风格，显示欢迎信息 |
| `userManage.html` | 用户管理列表页，表格展示所有用户 |
| `userForm.html` | 用户新增/编辑表单 |
| `admin/groupManage.html` | 分组管理列表页 |
| `admin/groupDetail.html` | 分组详情，含成员列表和权限配置 |
| `admin/permissionManage.html` | 全局权限管理，配置分组-页面访问权限 |

---

## 数据库说明

- 使用 SQLite，文件位于项目根目录 `mywebsite.db`
- JPA/Hibernate 自动建表，无需手动执行 SQL
- 核心数据表：`user`、`user_group`、`user_user_group`、`group_permission`、`page`

---

## 启动方式

```bash
mvn -U clean spring-boot:run
```

访问地址：http://localhost:8080

默认管理员账号：`admin` / `123456`
