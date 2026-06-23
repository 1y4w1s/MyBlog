# 博客管理系统（含前台 + 后台管理）

基于 Spring Boot + Vue 的博客系统，包含前台博客展示和后台管理功能。

## 项目结构

```
├── blog/                  # 博客前台后端（端口 7777）
├── admin/                 # 后台管理后端（端口 8989）
├── framework/             # 公共模块（实体、服务、工具类）
├── admin-vue/             # 后台管理前端（端口 9528）
├── fronted/               # 博客前台前端（端口 8080）
└── pom.xml                # Maven 父工程
```

## 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0+
- Redis
- Node.js（前台前端需要，后台前端可直接用 dist）

## 快速启动

### 1. 数据库初始化

```sql
CREATE DATABASE blog DEFAULT CHARACTER SET utf8mb4;
```

然后导入初始化脚本（包含建表 + 初始数据）：
```bash
mysql -u root -p blog < blog/sql/blog.sql
```

### 2. 启动 Redis

```bash
redis-server
```

### 3. 启动后端

```bash
# 编译打包（跳过测试）
mvn clean package -DskipTests

# 启动博客后端（端口 7777）
java -jar blog/target/blog-1.0-SNAPSHOT.jar

# 启动管理后台后端（端口 8989）
java -jar admin/target/admin-1.0-SNAPSHOT.jar
```

### 4. 启动前端

```bash
# 后台管理前端（推荐直接用 dist，无需 npm install）
cd admin-vue/dist
npx serve -l 9528
# 或者用 Node 简易服务器
node serve.js

# 博客前台前端
cd fronted/ptu-blog-vue
node serve.js
```

### 5. 访问地址

| 服务 | 地址 |
|------|------|
| 后台管理 | http://localhost:9528 |
| 博客前台 | http://localhost:8080 |

**默认账号**：admin / 123456

---

## 我修改过的地方（重要）

### 后端修改

#### 1. AdminLoginController.java
- 登录接口路径从 `/login` 改为 `/user/login`（适配前端 `login.js` 中的 `/user/login`）
- 新增 `GET /getInfo` 接口，返回当前用户信息、角色、权限

#### 2. admin/SecurityConfig.java
- 白名单路径从 `/login` 改为 `/user/login`
- 新增了后台管理需要的认证路径配置

#### 3. blog/src/main/resources/application.yml
- **数据库密码从 `lyj050726` 改为 `123456`**，如果别人的密码不同需要改回来

#### 4. framework/.../ArticleServiceImpl.java
- 修复 `articleList` 方法空指针异常：`categoryId` 为 null 时 `categoryId != 0` 会 NPE
- 改为 `categoryId != null && categoryId != 0`

#### 5. framework/.../RoleMenu.java / UserRole.java
- 去掉了 `@TableId("id")` 注解，因为数据库表 `sys_role_menu` 和 `sys_user_role` 没有 `id` 列（是联合主键）

#### 6. framework/.../UserInfoVo.java
- 新增 `userName`、`status`、`phonenumber` 字段（后台管理用户详情需要）

### 前端修改

#### 7. admin-vue/src/store/modules/permission.js
- 权限匹配逻辑增加 `*:*:*` 通配符支持
- 原来只检查 `permissions.includes('admin')`，改为同时检查 `permissions.includes('*:*:*')`

---

## 数据库表说明

| 表名 | 说明 | 备注 |
|------|------|------|
| user | 用户表 | admin 密码需为 BCrypt 加密 |
| article | 文章表 | |
| category | 分类表 | |
| link | 友链表 | |
| comment | 评论表 | |
| menu | 菜单表 | |
| sys_role | 角色表 | |
| sys_role_menu | 角色菜单关联 | 联合主键，无 id 列 |
| sys_user_role | 用户角色关联 | 联合主键，无 id 列 |

## 常见问题

### 1. 启动后报数据库连接错误
检查 `application.yml` 中的数据库密码，我的是 `123456`，如果你的不同请修改。

### 2. 侧栏菜单不显示
检查 `admin-vue/src/store/modules/permission.js` 是否有 `*:*:*` 的通配符判断。

### 3. 登录返回 401
确认后端登录接口路径是 `/user/login` 而不是 `/login`。

### 4. 文章列表报 500
确认 `ArticleServiceImpl.java` 中 `categoryId` 的空指针检查已修复。

### 5. admin-vue 的 dist 编译
admin-vue 的 `dist` 目录已经编译好，可以直接用 `npx serve -l 9528` 启动，不需要 `npm install`。

如果需要重新编译：
```bash
cd admin-vue
npm install
npm run dev
```

### 6. Node.js 版本兼容
前台前端 `fronted/ptu-blog-vue` 的 webpack-dev-server 版本较老，Node.js v18+ 可能有兼容性问题。推荐用 `node serve.js` 启动静态服务。

## API 接口概览

### Admin 后端（8989）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /user/login | 登录 |
| POST | /logout | 退出 |
| GET | /getInfo | 获取当前用户信息 |
| GET | /system/user/list | 用户列表 |
| GET | /system/user/{id} | 用户详情 |
| POST | /system/user | 新增用户 |
| PUT | /system/user | 修改用户 |
| DELETE | /system/user/{id} | 删除用户 |
| GET | /system/role/list | 角色列表 |
| GET | /system/role/{id} | 角色详情 |
| GET | /system/role/listAllRole | 所有正常角色 |
| POST | /system/role | 新增角色 |
| PUT | /system/role | 修改角色 |
| PUT | /system/role/changeStatus | 角色状态变更 |
| DELETE | /system/role/{id} | 删除角色 |
| GET | /system/menu/list | 菜单列表 |
| GET | /system/menu/treeselect | 菜单树 |
| GET | /system/menu/{id} | 菜单详情 |
| POST | /system/menu | 新增菜单 |
| PUT | /system/menu | 修改菜单 |
| DELETE | /system/menu/{menuId} | 删除菜单 |

### Blog 后端（7777）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /article/articleList | 文章列表 |
| GET | /article/hotArticleList | 热门文章 |
| GET | /article/{id} | 文章详情 |
| PUT | /article/updateViewCount/{id} | 更新浏览量 |
| GET | /category/getCategoryList | 分类列表 |
| GET | /category/{id} | 分类详情 |
| GET | /link/getLinkList | 友链列表 |
| GET | /link/{id} | 友链详情 |
| GET | /tag/tagList | 标签列表 |
| GET | /comment/linkCommentList | 评论列表 |
