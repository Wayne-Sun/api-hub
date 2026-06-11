# Changelog

## 2026-06-08 — 移除遗留 MysqlClient+MysqlQueryHandler 路径

### 架构重构

- **移除遗留 MySQL 专属查询路径（路径二）**：删除基于位置参数（`?`）和原始 JDBC `PreparedStatement` 的 `MysqlClient` + `MysqlQueryHandler` 实现。统一使用基于命名参数（`:paramName`）和 Spring `NamedParameterJdbcTemplate` 的通用 `SqlClient` + `SqlQueryHandler` 路径，该路径天然支持多方言（MySQL/Oracle）分页

### 删除文件（16 个）

| 文件 | 说明 |
|------|------|
| `datasource/client/MysqlClient.java` | MySQL 专用客户端（HikariCP + 原始 PreparedStatement） |
| `datasource/client/ResultSetCallback.java` | 仅被 MysqlClient 引用的回调接口 |
| `datasource/factory/MysqlClientFactory.java` | MysqlClient 工厂 |
| `datasource/conf/MysqlSourceConf.java` | MySQL 数据源配置实体 |
| `datasource/controller/MysqlSourceController.java` | MySQL 数据源管理 REST 接口 |
| `dataapi/conf/MysqlApiConf.java` | MySQL API 配置实体 |
| `dataapi/handler/MysqlApiHandler.java` | MySQL API 处理器（动态拼接 SQL） |
| `dataapi/factory/MysqlApiHandlerFactory.java` | MysqlApiHandler 工厂 |
| `dataapi/controller/MysqlApiController.java` | MySQL API 管理 REST 接口 |
| `query/handler/MysqlQueryHandler.java` | MySQL 查询处理器 |
| `query/controller/MysqlQueryController.java` | MySQL 查询 REST 接口 |
| `query/request/MysqlQueryRequest.java` | MySQL 查询请求 DTO |
| `dao/DataSourceMysqlDao.java` | MySQL 数据源 MyBatis DAO |
| `dao/DataApiMysqlDao.java` | MySQL API MyBatis DAO |
| `mybatis/mappers/DataSourceMysqlMapper.xml` | MySQL 数据源 MyBatis XML |
| `mybatis/mappers/DataApiMysqlMapper.xml` | MySQL API MyBatis XML |

### 清理交叉引用（6 个文件）

| 文件 | 变更 |
|------|------|
| `BaseQueryRequest.java` | `@Schema subTypes` 移除 `MysqlQueryRequest.class` |
| `BaseSourceConf.java` | `@Schema subTypes` 移除 `MysqlSourceConf.class` |
| `BaseApiConf.java` | `@Schema subTypes` 移除 `MysqlApiConf.class` |
| `SqlQueryController.java` | 修正 `@Tag name` 为 `SqlQueryController` |
| `DataSourceConfService.java` | 移除 Mysql 字段/依赖注入/5 个方法 |
| `DataApiConfService.java` | 移除 Mysql 字段/依赖注入/6 个方法 |

### 保留

- **`MysqlPageHelper.java`**：属于通用查询路径（路径一），被 `SqlPageHelperFactory` → `SqlQueryHandler` 引用，用于 MySQL 方言分页

### 2026-06-08 — 常量统一管理重构

### 重构说明

将整个项目中散落在各处的硬编码常量/魔法值提取到 `Constants.java`，按 16 个语义分组统一管理，消除重复定义。

### 常量分组结构

| 分组 | 常量 | 说明 |
|------|------|------|
| **状态** | `STATUS_ENABLE` / `STATUS_DISABLE` | 启用/禁用状态值 |
| **参数类型** | `PARAM_TYPE_STRING` / `PARAM_TYPE_NUMERIC` | JDBC 参数绑定类型 |
| **SQL 方言** | `DIALECT_ORACLE` / `DIALECT_MYSQL` | 数据库方言标识 |
| **SQL 命名参数** | `PARAM_START_ROW` / `PARAM_END_ROW` | 分页参数占位符 |
| **数据源类型名称** | `DS_MYSQL` / `DS_SQL` / `DS_SOLR` / `DS_HBASE` | 日志/工厂的 type name |
| **HBase API 类型** | `HBASE_API_TYPE_GET` / `HBASE_API_TYPE_SCAN` | HBase 查询类型 |
| **分隔符** | `COMMA` / `COLON` / `DOT` | 字符串切分/拼接分隔符 |
| **HikariCP 池配置** | `POOL_MIN_IDLE` / `POOL_MAX_SIZE` / `POOL_CONN_TIMEOUT` 等 | 连接池参数 |
| **JDBC 驱动** | `JDBC_DRIVER_MYSQL` / `JDBC_DRIVER_ORACLE` | 驱动类名 |
| **连接测试查询** | `TEST_QUERY_MYSQL` / `TEST_QUERY_ORACLE` | 连接健康检查 SQL |
| **池名称前缀** | `POOL_NAME_PREFIX_MYSQL` / `POOL_NAME_PREFIX_SQL` | HikariCP 连接池名称 |
| **HikariCP 属性** | `PROP_CACHE_PREP_STMTS` / `PROP_USE_SERVER_PREP_STMTS` 等 | DataSource 属性名 |
| **属性值** | `PROP_VALUE_TRUE` / `PROP_VALUE_FALSE` / 缓存大小 | DataSource 属性值 |
| **编码与 Content** | `CHARSET_UTF8` / `CONTENT_TYPE_JSON` | HTTP 响应 |
| **Solr 客户端** | `SOLR_CONN_TIMEOUT` | Solr 连接超时 |
| **SQL 正则/模板** | `REGEX_NAMED_PARAM` / `REGEX_EXPR_PARAM` / `COUNT_SQL_WRAPPER` | 参数提取与计数 SQL |

### 旧常量向后兼容

旧常量已全部替换为新常量名：`Constants.PARAM_TYPE_STRING` / `Constants.PARAM_TYPE_NUMERIC` / `Constants.DIALECT_ORACLE` / `Constants.DIALECT_MYSQL`

### 变更统计

| Session | 文件变更 | 插入行 | 删除行 |
|---------|---------|--------|--------|
| 移除 MysqlClient | 39 files | 161 | 1153 |
| 常量统一管理 | 21 files | 189 | 117 |

## 2026-06-08 — 项目结构拆分：backend + frontend

### 重构说明

将 Spring Boot 后端移至 `backend/` 目录，并在 `frontend/` 目录下创建 Vue 3 前端项目脚手架。

### 后端迁移

| 文件/目录 | 目标路径 |
|-----------|---------|
| `pom.xml` | `backend/pom.xml` |
| `src/` | `backend/src/` |
| `target/` | `backend/target/` |
| `doc/` | `backend/doc/` |
| `LICENSE` | `backend/LICENSE` |
| `README.md` | `backend/README.md` |
| `.gitignore` | `backend/.gitignore` |

### 前端脚手架

| 特性 | 选型 |
|------|------|
| 构建工具 | Vite |
| 语言 | TypeScript |
| 框架 | Vue 3 |
| 路由 | Vue Router |
| 状态管理 | Pinia |
| Lint / Format | ESLint + Prettier + oxlint |
| 测试 | Vitest |
| 版本 | create-vue@3.22.3 |

### 根目录变更

- 新增 `frontend/` — Vue 3 前端项目
- 新增 `.gitignore` — 适配前后端分离结构
