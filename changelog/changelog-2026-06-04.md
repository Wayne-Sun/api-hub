# Changelog

## 2026-06-04 — 框架升级与架构重构

### 框架升级

- **Spring Boot**: 2.7.17 → 4.0.6
- **Java**: 1.8 → 17
- **mybatis-spring-boot-starter**: 2.3.1 → 4.0.1
- **pagehelper-spring-boot-starter**: 1.4.7 → 4.1.0
- **springdoc-openapi**: 更换 artifact 为 `springdoc-openapi-starter-webmvc-ui`，版本 3.0.3
- **日志框架**: Log4j2 → Logback（Spring Boot 默认）
- **javax.\* → jakarta.\***: 所有 `javax.annotation`/`javax.servlet` 导入迁移至 `jakarta.*`
- **保留**: SolrJ 8.11.2, HBase 2.4.17（兼容 Java 17 且依赖服务端版本）


### Bug 修复

- **`MysqlClient.query()` ResultSet 提前关闭**: try-with-resources 中 Connection 关闭导致返回的 ResultSet 不可用。引入 `ResultSetCallback<T>` 回调接口，ResultSet 在连接生命周期内消费完毕再返回
- **`application.yml` 日志配置路径错误**: `logging.config` 引用已删除的 `log4j2.xml`，改为 `logback-spring.xml`
- **`QueryPageInfo.getStartRow()` 分页偏移错误**: 从 1-based 改为 0-based offset，修复 SQL 分页跳行问题。Oracle 分页加 1 补偿 ROWNUM 的 1-based 语义


### 架构重构

- **`AbstractClientFactory<T>`**: 提取 4 个 ClientFactory 的公共生命周期管理（init/enable/disable/close），每个工厂从 ~120 行缩减至 ~20 行，消除 ~380 行重复代码
- **`AbstractApiHandlerFactory<T>`**: 提取 4 个 ApiHandlerFactory 的公共逻辑，每个工厂从 ~103 行缩减至 ~20 行，消除 ~330 行重复代码
- **包整理**: `modules/common/` 中的 SQL 分页/参数代码搬迁至 `modules/query/sql/`（`CommonPageHelper`, `MysqlPageHelper`, `OraclePageHelper`, `SqlPageHelperFactory`, `SqlParamUtil`, `SqlParam`）
- **`HashMap` → `ConcurrentHashMap`**: MysqlClientFactory、SolrClientFactory、SqlClientFactory 修复并发安全隐患
- **异常体系**: `DataSourceException`、`DataApiException`、`QueryParamException` 改为 `extends RuntimeException`，移除不必要的方法签名
- **分页请求去重**: `pageNum`/`pageSize` 字段上移至 `BaseQueryRequest`，子类 `MysqlQueryRequest`/`SolrQueryRequest`/`SqlQueryRequest` 移除重复定义
- **废弃 API 替换**: `JsonParser.getCurrentName()` → `currentName()`（Jackson 兼容性）


### 新增文件

| 文件 | 说明 |
|------|------|
| `AbstractClientFactory.java` | 数据源客户端工厂抽象类，统一生命周期管理 |
| `AbstractApiHandlerFactory.java` | API 处理器工厂抽象类，统一注册/启禁管理 |
| `ResultSetCallback.java` | 函数式接口，安全消费 ResultSet |
| `logback-spring.xml` | Logback 日志配置（Console + 按天/50MB 滚动文件，保留 90 天） |
| `modules/query/sql/*.java` | 6 个 SQL 查询相关类（从 `modules/common` 搬迁） |


### 删除文件

| 文件 | 原因 |
|------|------|
| `log4j2.xml` | 日志框架切换至 Logback |
| `modules/common/` 目录下 6 个文件 | 搬迁至 `modules/query/sql/` |
| 原有 8 个工厂类的大量重复代码 | AbstractClientFactory/AbstractApiHandlerFactory 统一管理 |


### 变更统计

```
36 files changed, 172 insertions(+), 949 deletions(-)
```
