# MyBlog - SpringBoot 博客系统

基于 SpringBoot + Vue + MySQL + Redis + 七牛云OSS 的博客系统，包含博客前台和后台管理系统。

## 项目结构

```
MyBlog/
├── admin/                  # 后台管理 SpringBoot 服务 (端口 8989)
├── blog/                   # 博客前台 SpringBoot 服务 (端口 7777)
├── framework/              # 公共模块 (实体类/工具类/服务层)
├── admin-vue/              # 后台管理 Vue 前端 (端口 81)
├── fronted/
│   └── ptu-blog-vue/       # 博客前台 Vue 前端 (端口 8888)
├── blog/sql/blog.sql       # 数据库初始化脚本 (必须先执行)
└── pom.xml                 # Maven 父工程
```

## 环境要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 1.8+ | 后端运行环境 |
| Maven | 3.6+ | 构建工具 |
| MySQL | 5.7+ | 数据库 |
| Redis | 5.0+ | 缓存（登陆 token 存储） |
| Node.js | 6.0~16.x | Vue 前端构建（推荐 14.x 或 16.x） |
| npm | 3.0+ | 包管理 |

> **Node.js 版本注意**: 后台管理前端使用 webpack 3.x + Vue 2.x，Node.js 14.x/16.x 兼容性最好。Node.js 18+ 可能导致 `ERR_OSSL_EVP_UNSUPPORTED` 错误，Node.js 24+ 不兼容 webpack 3.x。

## 从零启动步骤

### 1. 初始化数据库

打开 MySQL 客户端，执行：

```sql
source blog/sql/blog.sql
```

数据库名 `blog`，默认账号 `root`，密码 `123456`。如果密码不同，修改 `admin/src/main/resources/application.yml` 和 `blog/src/main/resources/application.yml` 中的数据库连接信息。

### 2. 启动 Redis

确保 Redis 运行在 `localhost:6379`，无密码。

### 3. 启动后端

```bash
# 在项目根目录执行
mvn clean install -DskipTests

# 启动博客服务
cd blog
mvn spring-boot:run

# 新开终端，启动后台管理服务
cd admin
mvn spring-boot:run
```

如果 `mvn` 不在 PATH 中，使用完整路径：
```bash
D:\maven\apache-maven-3.9.6\bin\mvn.cmd clean install -DskipTests
```

### 4. 启动博客前台前端

```bash
cd fronted/ptu-blog-vue
npm install
npm run dev
# 浏览器访问 http://localhost:8888
```

> **如果 Node.js 版本过高 (18+) 导致 webpack 报错**：使用预构建的静态文件直接运行：
> ```bash
> node serve.js
> # 浏览器访问 http://localhost:8888
> ```

### 5. 启动后台管理前端

```bash
cd admin-vue
# 重要：必须删除旧的依赖后重新安装
rm -rf node_modules package-lock.json
npm install
npm run dev
# 浏览器访问 http://localhost:81
```

## 默认账号

| 服务 | 用户名 | 密码 |
|------|--------|------|
| 后台管理 | admin | 123456 |
| 博客前台 | admin | 123456 |

后台管理登录地址：`http://localhost:81/#/user/login`

## API 端口

| 服务 | 端口 | 说明 |
|------|------|------|
| 博客后台 | 7777 | 博客前台对应的 API |
| 管理后台 | 8989 | 后台管理系统的 API |
| 博客前端 | 8888 | 博客前台页面 |
| 管理前端 | 81 | 后台管理页面 |

## 技术栈

- **后端**: Spring Boot 2.7.10 + Spring Security + JWT + MyBatis-Plus 3.5.3.1 + Redis
- **前端**: Vue 2.x + Vuex + Vue Router + Element UI + Axios
- **存储**: 本地文件 + 七牛云 OSS
- **数据库**: MySQL 5.7+

## 已实现功能

### 后台管理 (admin)
- 用户管理 (CRUD、头像上传)
- 角色管理 (权限分配)
- 菜单管理 (动态路由)
- 文章管理 (发布、编辑、删除、标签)
- 分类管理
- 友链管理
- Tag 管理

### 博客前台 (blog)
- 首页文章列表、分类筛选
- 文章详情 (Markdown 渲染)
- 文章评论、友链评论
- 用户登陆/注册
- 个人中心 (头像上传)
- 友情链接页面

## 常见问题

### 后台管理前端启动后侧边栏空白/崩溃
```bash
# 必须删除旧依赖重新安装
cd admin-vue
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### 博客前端端口冲突
`serve.js` 默认端口 8888。如果被占用，修改 `serve.js` 中的 `port` 变量。

### Maven 构建找不到依赖
确保先在根目录执行 `mvn clean install -DskipTests`，三个模块都会被安装到本地仓库。

### 数据库连接失败
检查 `application.yml` 中的 MySQL 连接信息，默认 `root/123456@localhost:3306/blog`。
