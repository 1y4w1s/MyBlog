# 改动日志

> 日期：2026-06-12

---

## 1. Bug 修复

### 1.1 数据库连接修复

**问题**：登录时报 `Access denied for user 'root'@'localhost'`，MySQL 拒绝了连接。

**原因**：
- `blog` 和 `framework` 两个模块各有一份 `application.yml`，密码不一致
- `target/classes` 编译缓存未同步，后端加载了旧配置

**改动**：

| 文件 | 内容 |
|------|------|
| `blog/src/main/resources/application.yml` | 密码 `046751` → `root`；JDBC URL 追加 `useSSL=false&allowPublicKeyRetrieval=true` |
| `framework/src/main/resources/application.yml` | 密码 `046751` → `root`；数据库 `myblog` → `blog`；JDBC URL 同上 |
| `blog/target/classes/application.yml` | 密码同步 |
| `framework/target/classes/application.yml` | 密码同步 |

---

### 1.2 CategoryServiceImpl 空集合 SQL 错误

**问题**：文章表为空时生成非法 SQL `WHERE id IN ( )`

```sql
SELECT ... FROM category WHERE id IN ( ) AND del_flag=0
```

**改动**：`framework/.../service/impl/CategoryServiceImpl.java`

在 `selectBatchIds()` 前增加空集合判断，为空直接返回空列表。

```java
if (categoryIds.isEmpty()) {
    return ResponseResult.okResult(Collections.emptyList());
}
```

---

### 1.3 MyMetaObjectHandler 类型转换崩溃

**问题**：用户注册时（无认证），`SecurityContextHolder` 中 principal 为匿名用户字符串，强转 `LoginUser` 导致 `ClassCastException: String cannot be cast to LoginUser`。

**改动**：`framework/.../handler/MyMetaObjectHandler.java`

添加 `instanceof LoginUser` 安全判断，用 try-catch 包裹防止自动填充失败影响主流程。

---

### 1.4 UploadServiceImpl 空文件空指针

**问题**：上传接口不传文件时，`img.getOriginalFilename()` 空指针异常。

**改动**：`framework/.../service/impl/UploadServiceImpl.java`

增加 `img == null || img.isEmpty()` 判断，返回友好错误提示。

---

## 2. 新功能实现

### 2.1 头像上传接口（七牛云 OSS）

**接口**：`POST /upload`（无需 token）

| 文件 | 操作 | 说明 |
|------|------|------|
| `framework/.../utils/PathUtils.java` | **新增** | 文件路径生成，`yyyy/MM/dd/UUID.jpg` |
| `framework/.../service/UploadService.java` | **新增** | 上传服务接口 |
| `framework/.../service/impl/UploadServiceImpl.java` | **新增** | 七牛云 OSS 数据流上传，仅允许 png/jpg/jpeg |
| `blog/.../controller/UploadController.java` | **修改** | 注入 `UploadService` 替代旧的本地存储 |
| `blog/src/main/resources/application.yml` | **修改** | 配置前缀 `qiniu` → `oss`；新增七牛云 AK/SK/Bucket/Domain |
| `framework/.../utils/QiniuUtils.java` | **修改** | `@Value` 引用 `qiniu.domain` → `oss.domain` |

---

### 2.2 用户注册（校验 + 加密）

**接口**：`POST /user/register`（无需 token）

| 文件 | 操作 | 说明 |
|------|------|------|
| `framework/.../enums/AppHttpCodeEnum.java` | **修改** | 新增 5 个错误码 |
| `framework/.../service/impl/UserServiceImpl.java` | **修改** | 新增非空校验、重复校验、BCrypt 密码加密 |
| `framework/.../domain/entity/User.java` | **修改** | `createTime`/`updateTime` 添加 `@TableField` 自动填充 |

**新增错误码**：

| 枚举 | code | msg |
|------|------|-----|
| `USERNAME_NOT_NULL` | 510 | 用户名不能为空 |
| `NICKNAME_NOT_NULL` | 511 | 昵称不能为空 |
| `PASSWORD_NOT_NULL` | 512 | 密码不能为空 |
| `EMAIL_NOT_NULL` | 513 | 邮箱不能为空 |
| `NICKNAME_EXIST` | 514 | 昵称已存在 |

**校验逻辑**：

```
非空: userName / nickName / password / email  → 510/511/512/513
重复: userName(501) / nickName(514) / email(503)
加密: passwordEncoder.encode(password)
```

---

### 2.3 更新个人信息

**接口**：`PUT /user/userInfo`（需要 token）

接口已存在，无需新增代码。

---

## 3. 接口测试结果

| # | 接口 | 测试项 | 结果 |
|---|------|--------|------|
| 0 | `POST /login` | admin/123456 | ✅ |
| 1.1 | `POST /upload` | 正常上传 jpg | ✅ |
| 1.2 | `POST /upload` | 不传文件 | ✅ 上传文件不能为空 |
| 2.1 | `POST /user/register` | 正常注册 | ✅ |
| 2.2 | `POST /user/register` | 用户名为空 | ✅ 510 |
| 2.3 | `POST /user/register` | 昵称为空 | ✅ 511 |
| 2.4 | `POST /user/register` | 密码为空 | ✅ 512 |
| 2.5 | `POST /user/register` | 邮箱为空 | ✅ 513 |
| 2.6 | `POST /user/register` | 用户名重复 | ✅ 501 |
| 2.7 | `POST /user/register` | 昵称重复 | ✅ 514 |
| 2.8 | `POST /user/register` | 邮箱重复 | ✅ 503 |
| 3.1 | `PUT /user/userInfo` | 正常更新 | ✅ |
| 3.2 | `PUT /user/userInfo` | 无 token | ✅ 401 |

---

## 4. 全部改动文件

```
新增 (3):
  framework/.../utils/PathUtils.java
  framework/.../service/UploadService.java
  framework/.../service/impl/UploadServiceImpl.java

修改 (9):
  blog/src/main/resources/application.yml
  framework/src/main/resources/application.yml
  blog/target/classes/application.yml
  framework/target/classes/application.yml
  blog/.../controller/UploadController.java
  framework/.../service/impl/CategoryServiceImpl.java
  framework/.../service/impl/UserServiceImpl.java
  framework/.../enums/AppHttpCodeEnum.java
  framework/.../domain/entity/User.java
  framework/.../handler/MyMetaObjectHandler.java
  framework/.../utils/QiniuUtils.java
```

---

## 5. 当前配置

| 服务 | 端口 | 状态 |
|------|------|------|
| 前端 Vue | 8080 | ✅ |
| 后端 Spring Boot | 7777 | ✅ |
| MySQL | 3306 | ✅ |
| Redis | 6379 | ✅ |

| 配置项 | 值 |
|--------|-----|
| MySQL 数据库 | `blog` |
| MySQL 用户/密码 | `root` / `root` |
| 七牛云 Bucket | `tgirwha1m` |
| 七牛云 域名 | `http://tgirwha1m.hn-bkt.clouddn.com` |
