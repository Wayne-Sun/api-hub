# API HUB 架构分析

## 项目定位

企业级数据平台 API 统一管理网关，实现多种数据源（MySQL、HBase、Solr、通用 SQL）的 API 注册、授权和查询。

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 框架 | Spring Boot 4.0.6 |
| 语言 | Java 17 |
| ORM | MyBatis 4.0.1 + PageHelper 4.1.0 |
| 数据库 | MySQL（元数据持久化） |
| API 文档 | SpringDoc OpenAPI 3.0.3 |
| 日志 | Log4j2 |
| 工具 | Lombok、Gson |
| 外部数据源 | MySQL / HBase / Solr / JDBC |

---

## 整体分层

```
ApiHubApplication (入口)
  │
  ├── config/           全局配置 (CORS、OpenAPI)
  ├── model/            统一响应模型、分页请求、数据基类
  ├── dao/              MyBatis Mapper 接口
  ├── service/          业务编排层 (数据源 + API 配置维护)
  ├── utils/            工具类 (JSON、响应封装、常量)
  └── modules/          核心功能模块
       ├── common/          跨模块公用组件 (SQL 分页、参数工具)
       ├── datasource/      数据源管理
       ├── dataapi/         API 注册管理
       └── query/           API 查询执行
```

### 包结构说明

```
com.wayne.apihub
├── ApiHubApplication.java          -- Spring Boot 入口
├── config/
│   ├── CorsConfig.java             -- 跨域配置
│   └── OpenapiConfig.java          -- OpenAPI 文档配置
├── model/
│   ├── BaseDataObject.java         -- 数据基类
│   ├── BasePageRequest.java        -- 分页请求基类
│   ├── BaseResponse.java           -- 统一响应封装
│   └── HbaseColumnFamily.java      -- HBase 列族模型
├── dao/
│   ├── DataApiHbaseDao.java        -- HBase API 配置 DAO
│   ├── DataApiMysqlDao.java        -- MySQL API 配置 DAO
│   ├── DataApiSolrDao.java         -- Solr API 配置 DAO
│   ├── DataApiSqlDao.java          -- SQL API 配置 DAO
│   ├── DataSourceHbaseDao.java     -- HBase 数据源 DAO
│   ├── DataSourceMysqlDao.java     -- MySQL 数据源 DAO
│   ├── DataSourceSolrDao.java      -- Solr 数据源 DAO
│   └── DataSourceSqlDao.java       -- SQL 数据源 DAO
├── service/
│   ├── DataApiConfService.java     -- API 配置业务逻辑
│   └── DataSourceConfService.java  -- 数据源配置业务逻辑
├── utils/
│   ├── Constants.java              -- 常量定义
│   ├── JsonUtils.java              -- JSON 工具
│   └── ResponseUtils.java          -- 响应工具
└── modules/
    ├── common/
    │   ├── SqlPageHelperFactory.java    -- SQL 方言分页工厂
    │   ├── SqlParamUtil.java            -- SQL 参数工具
    │   ├── entity/SqlParam.java         -- SQL 参数实体
    │   └── page/
    │       ├── CommonPageHelper.java    -- 分页接口
    │       ├── MysqlPageHelper.java     -- MySQL 分页实现
    │       └── OraclePageHelper.java    -- Oracle 分页实现
    ├── datasource/
    │   ├── client/
    │   │   ├── CommonClient.java        -- 客户端接口
    │   │   ├── HbaseClient.java         -- HBase 客户端
    │   │   ├── MysqlClient.java         -- MySQL 客户端
    │   │   ├── SolrClient.java          -- Solr 客户端
    │   │   └── SqlClient.java           -- SQL 客户端
    │   ├── conf/
    │   │   ├── BaseSourceConf.java      -- 数据源配置基类
    │   │   ├── HbaseSourceConf.java     -- HBase 数据源配置
    │   │   ├── MysqlSourceConf.java     -- MySQL 数据源配置
    │   │   ├── SolrSourceConf.java      -- Solr 数据源配置
    │   │   └── SqlSourceConf.java       -- SQL 数据源配置
    │   ├── controller/
    │   │   ├── HbaseSourceController.java
    │   │   ├── MysqlSourceController.java
    │   │   ├── SolrSourceController.java
    │   │   └── SqlSourceController.java
    │   ├── exception/DataSourceException.java
    │   └── factory/
    │       ├── CommonClientFactory.java  -- 客户端工厂接口
    │       ├── HbaseClientFactory.java
    │       ├── MysqlClientFactory.java
    │       ├── SolrClientFactory.java
    │       └── SqlClientFactory.java
    ├── dataapi/
    │   ├── conf/
    │   │   ├── BaseApiConf.java         -- API 配置基类
    │   │   ├── HbaseApiConf.java
    │   │   ├── MysqlApiConf.java
    │   │   ├── SolrApiConf.java
    │   │   └── SqlApiConf.java
    │   ├── controller/
    │   │   ├── HbaseApiController.java
    │   │   ├── MysqlApiController.java
    │   │   ├── SolrApiController.java
    │   │   └── SqlApiController.java
    │   ├── exception/DataApiException.java
    │   ├── factory/
    │   │   ├── CommonApiHandlerFactory.java -- API 处理器工厂接口
    │   │   ├── HbaseApiHandlerFactory.java
    │   │   ├── MysqlApiHandlerFactory.java
    │   │   ├── SolrApiHandlerFactory.java
    │   │   └── SqlApiHandlerFactory.java
    │   └── handler/
    │       ├── CommonApiHandler.java    -- API 处理器接口
    │       ├── HbaseApiHandler.java
    │       ├── MysqlApiHandler.java
    │       ├── SolrApiHandler.java
    │       └── SqlApiHandler.java
    └── query/
        ├── controller/
        │   ├── HbaseQueryController.java
        │   ├── MysqlQueryController.java
        │   ├── SolrQueryController.java
        │   └── SqlQueryController.java
        ├── exception/QueryParamException.java
        ├── handler/
        │   ├── HbaseQueryHandler.java
        │   ├── MysqlQueryHandler.java
        │   ├── SolrQueryHandler.java
        │   └── SqlQueryHandler.java
        ├── request/
        │   ├── BaseQueryRequest.java
        │   ├── HbaseQueryRequest.java
        │   ├── MysqlQueryRequest.java
        │   ├── SolrQueryRequest.java
        │   └── SqlQueryRequest.java
        └── result/
            ├── PageResult.java
            └── QueryPageInfo.java
```

---

## 核心模块

### 1. `datasource` — 数据源管理

管理 4 种数据源的注册、启禁及生命周期。

```
HbaseSourceController  ──→  HbaseClientFactory  ──→  HbaseClient
MysqlSourceController  ──→  MysqlClientFactory  ──→  MysqlClient
SolrSourceController   ──→  SolrClientFactory   ──→  SolrClient
SqlSourceController    ──→  SqlClientFactory    ──→  SqlClient
       │                         │
   REST 接口                 CommonClientFactory        CommonClient
                            (工厂接口)                 (客户端接口)
```

- **工厂模式**：每个 `XxxClientFactory` 实现 `CommonClientFactory`，管理连接的 `init / create / enable / disable / close`
- **Client 接口**：`CommonClient` 只定义 `close()`，具体查询能力由各实现类通过 JDBC / SolrJ / HBase API 完成
- **Controller**：提供 RESTful 接口进行数据源的 CRUD 及启禁操作
- **生命周期**：工厂在 `@PostConstruct` 时从 DB 加载已注册的数据源并建立连接，`@PreDestroy` 时关闭所有连接

### 2. `dataapi` — API 注册管理

每个 API 是一个**预定义的查询模板**（如 MySQL 的 SELECT 语句、Solr 的 collection+fields、HBase 的 table+columns）。

```
XxxApiController  ──→  XxxApiHandlerFactory  ──→  XxxApiHandler
                               │
                         CommonApiHandlerFactory   CommonApiHandler
                            (工厂接口)              (处理器接口)
```

- API 配置存储在 MySQL 元数据库中
- 工厂维护 `Map<Long, XxxApiHandler>`，支持运行时启禁
- `XxxApiHandler` 解析 API 配置，生成可执行的查询语句

### 3. `query` — 查询执行

最终用户入口：传入 API ID + 参数 → 获取数据。

```
POST /v1/query/mysql
  → XxxQueryController
    → XxxQueryHandler
      → XxxApiHandlerFactory.getApiHandler(id)    // 获取 API 定义
      → XxxClientFactory.getClient(sourceId)       // 获取数据源连接
      → XxxApiHandler.generateSql()                // 生成查询语句
      → XxxClient.query()                          // 执行查询
      → handleResultSet()                          // 组装 JSON 结果
      → BaseResponse.ok(PageResult)                // 统一响应
```

---

## 请求处理链路（以 MySQL 为例）

```
客户端
  │
  ▼
MysqlQueryController.query(@RequestBody MysqlQueryRequest)
  │
  ▼
MysqlQueryHandler.handle(mysqlQueryRequest)
  │
  ├── MysqlApiHandlerFactory.getApiHandler(id)
  │     └── 返回 MysqlApiHandler (含 API 配置: 表名, 字段, 条件, 排序)
  │
  ├── 校验参数数量与类型
  │
  ├── MysqlClientFactory.getClient(dataSourceId)
  │     └── 返回 MysqlClient (含 JDBC 连接)
  │
  ├── MysqlApiHandler.generateSql(pageNum, pageSize)
  │     └── 生成带 LIMIT 分页的 SQL
  │
  ├── MysqlClient.query(countSql, params, types)
  │     └── 执行 COUNT 查询 → 总记录数
  │
  ├── MysqlClient.query(sql, params, types)
  │     └── 执行数据查询 → ResultSet
  │
  ├── handleResultSet(resultSet, columns)
  │     └── ResultSet → JsonArray
  │
  └── BaseResponse.ok(PageResult(data, pageInfo))
```

---

## 数据库表结构

元数据存储于 MySQL `api_hub` 库，按数据源类型拆表：

| 分类 | 表名 | 用途 |
|------|------|------|
| 数据源 | `data_source_hbase` | HBase 连接配置 (hbase_site_path, core_site_path) |
| 数据源 | `data_source_mysql` | MySQL 连接配置 (url, username, password) |
| 数据源 | `data_source_solr` | Solr 连接配置 (zk_hosts, zk_chroots) |
| 数据源 | `data_source_sql` | 通用 SQL 连接配置 (dialect, url, username, password) |
| API | `data_api_hbase` | HBase API 定义 (table_name, columns) |
| API | `data_api_mysql` | MySQL API 定义 (database_name, table_name, columns, conditions) |
| API | `data_api_solr` | Solr API 定义 (collection, fields, conditions) |
| API | `data_api_sql` | 通用 SQL API 定义 (sql, page_tag) |
| 参数 | `data_api_sql_param` | SQL API 入参定义 (name, type) |

所有表均包含统一审计字段：`id, name, comments, status, create_time, create_by, update_time, update_by`。

---

## 架构特点

| 特点 | 说明 |
|------|------|
| **多数据源抽象** | 通过 `CommonClient` / `CommonClientFactory` 接口统一 4 种异构数据源 |
| **工厂模式** | 每个数据源/API 类型都有独立工厂，管理实例的注册、启禁、生命周期 |
| **模块化** | 按功能拆分为 `datasource` / `dataapi` / `query` 三个独立子模块，职责清晰 |
| **模板化 API** | 预注册 API 定义（SQL/字段/条件），查询时只需传 ID + 参数，无需暴露底层 SQL |
| **统一响应** | 所有接口返回 `BaseResponse(code, message, data)`，code 200/400/401/403/500 |
| **分页支持** | MySQL 使用 PageHelper 插件；通用 SQL 使用自定义方言分页策略 |

---

## TODO

1. 支持 Elasticsearch、Clickhouse、Redis 等更多数据源
2. 高可用和服务网关特性
3. 用户、用户组和权限管理
4. 日志和告警
