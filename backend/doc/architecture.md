# API HUB 架构分析

## 项目定位

企业级数据平台 API 统一管理网关，实现多种数据源（HBase、Solr、通用 SQL（MySQL/Oracle））的 API 注册、授权和查询。

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 框架 | Spring Boot 4.0.6 |
| 语言 | Java 17 |
| ORM | MyBatis 4.0.1 + PageHelper 4.1.0 |
| 元数据库 | MySQL（HikariCP 连接池） |
| API 文档 | SpringDoc OpenAPI 3.0.3 |
| 日志 | Logback（logback-spring.xml） |
| 工具 | Lombok |
| 外部数据源 | HBase / Solr / 通用 SQL（MySQL、Oracle） |

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
       ├── datasource/      数据源管理
       ├── dataapi/         API 注册管理
       └── query/           API 查询执行
```

### 包结构说明

```
com.wayne.apihub
├── ApiHubApplication.java          -- Spring Boot 入口 (@MapperScan + @SpringBootApplication)
├── config/
│   ├── CorsConfig.java             -- 跨域配置
│   └── OpenapiConfig.java          -- OpenAPI 文档配置
├── model/
│   ├── BaseDataObject.java         -- 数据基类 (id, name, comments, status, 审计字段)
│   ├── BasePageRequest.java        -- 分页请求基类
│   ├── BaseResponse.java           -- 统一响应封装 (code 200/400/401/403/500)
│   └── HbaseColumnFamily.java      -- HBase 列族模型
├── dao/
│   ├── DataApiHbaseDao.java        -- HBase API 配置 DAO
│   ├── DataApiSolrDao.java         -- Solr API 配置 DAO
│   ├── DataApiSqlDao.java          -- 通用 SQL API 配置 DAO
│   ├── DataSourceHbaseDao.java     -- HBase 数据源 DAO
│   ├── DataSourceSolrDao.java      -- Solr 数据源 DAO
│   └── DataSourceSqlDao.java       -- 通用 SQL 数据源 DAO
├── service/
│   ├── DataApiConfService.java     -- API 配置业务逻辑 (HBase / Solr / 通用 SQL)
│   └── DataSourceConfService.java  -- 数据源配置业务逻辑 (HBase / Solr / 通用 SQL)
├── utils/
│   ├── Constants.java              -- 常量定义 (状态、SQL方言、JDBC驱动、连接池参数)
│   ├── JsonUtils.java              -- JSON 工具
│   └── ResponseUtils.java          -- 响应工具 (已迁移至 BaseResponse)
└── modules/
    ├── datasource/
    │   ├── client/
    │   │   ├── CommonClient.java        -- 客户端接口 (close)
    │   │   ├── HbaseClient.java         -- HBase 客户端
    │   │   ├── SolrClient.java          -- Solr 客户端
    │   │   └── SqlClient.java           -- 通用 SQL 客户端 (HikariCP)
    │   ├── conf/
    │   │   ├── BaseSourceConf.java      -- 数据源配置基类
    │   │   ├── HbaseSourceConf.java     -- HBase 数据源配置
    │   │   ├── SolrSourceConf.java      -- Solr 数据源配置
    │   │   └── SqlSourceConf.java       -- 通用 SQL 数据源配置 (dialect, url, username, password)
    │   ├── controller/
    │   │   ├── HbaseSourceController.java
    │   │   ├── SolrSourceController.java
    │   │   └── SqlSourceController.java
    │   ├── exception/
    │   │   └── DataSourceException.java
    │   └── factory/
    │       ├── CommonClientFactory.java      -- 客户端工厂接口
    │       ├── AbstractClientFactory.java    -- 抽象基类 (模板方法)
    │       ├── HbaseClientFactory.java
    │       ├── SolrClientFactory.java
    │       └── SqlClientFactory.java
    ├── dataapi/
    │   ├── conf/
    │   │   ├── BaseApiConf.java             -- API 配置基类
    │   │   ├── HbaseApiConf.java
    │   │   ├── SolrApiConf.java
    │   │   └── SqlApiConf.java
    │   ├── controller/
    │   │   ├── HbaseApiController.java
    │   │   ├── SolrApiController.java
    │   │   └── SqlApiController.java
    │   ├── exception/
    │   │   └── DataApiException.java
    │   ├── factory/
    │   │   ├── CommonApiHandlerFactory.java  -- API 处理器工厂接口
    │   │   ├── AbstractApiHandlerFactory.java-- 抽象基类 (模板方法)
    │   │   ├── HbaseApiHandlerFactory.java
    │   │   ├── SolrApiHandlerFactory.java
    │   │   └── SqlApiHandlerFactory.java
    │   └── handler/
    │       ├── CommonApiHandler.java         -- API 处理器接口
    │       ├── HbaseApiHandler.java
    │       ├── SolrApiHandler.java
    │       └── SqlApiHandler.java
    └── query/
        ├── controller/
        │   ├── HbaseQueryController.java
        │   ├── SolrQueryController.java
        │   └── SqlQueryController.java
        ├── exception/
        │   └── QueryParamException.java
        ├── handler/
        │   ├── HbaseQueryHandler.java
        │   ├── SolrQueryHandler.java
        │   └── SqlQueryHandler.java
        ├── request/
        │   ├── BaseQueryRequest.java
        │   ├── HbaseQueryRequest.java
        │   ├── SolrQueryRequest.java
        │   └── SqlQueryRequest.java
        ├── result/
        │   ├── PageResult.java
        │   └── QueryPageInfo.java
        └── sql/                              -- SQL 方言分页策略
            ├── CommonPageHelper.java         -- 分页接口
            ├── MysqlPageHelper.java          -- MySQL 分页实现 (LIMIT 语法)
            ├── OraclePageHelper.java         -- Oracle 分页实现 (ROWNUM 语法)
            ├── SqlPageHelperFactory.java     -- 方言分页工厂
            ├── SqlParam.java                 -- SQL 参数实体
            └── SqlParamUtil.java             -- SQL 参数工具 (命名参数 → JDBC 参数)
```

---

## 核心模块

### 1. `datasource` — 数据源管理

管理 3 种数据源的注册、启禁及生命周期。

```
HbaseSourceController  ──→  HbaseClientFactory  ──→  HbaseClient
SolrSourceController   ──→  SolrClientFactory   ──→  SolrClient
SqlSourceController    ──→  SqlClientFactory    ──→  SqlClient
       │                         │
   REST 接口               AbstractClientFactory      CommonClient
                          (抽象基类 - 模板方法)       (客户端接口)
                              │
                         CommonClientFactory
                          (工厂接口)
```

- **模板方法模式**：`AbstractClientFactory<T extends CommonClient>` 实现了 `CommonClientFactory` 接口，封装了 `init / createClient / enableClient / disableClient / close` 的通用生命周期流程。各数据源工厂（`HbaseClientFactory`、`SolrClientFactory`、`SqlClientFactory`）继承抽象基类，仅需实现 `doCreateClient()`、`listConfs()` 等模板方法。
- **Client 接口**：`CommonClient` 只定义 `close()`，具体查询能力由各实现类通过 JDBC / SolrJ / HBase API 完成。
- **不支持的 MySQL 独立类型**：MySQL 不再作为独立数据源类型存在，其能力由 **通用 SQL** 数据源通过 `dialect` 字段区分 MySQL/Oracle 方言来替代。
- **Controller**：提供 RESTful 接口进行数据源的 CRUD 及启禁操作。
- **生命周期**：`@PostConstruct` 从 DB 加载已注册的数据源并建立连接，`@PreDestroy` 关闭所有连接。

### 2. `dataapi` — API 注册管理

每个 API 是一个**预定义的查询模板**（如通用 SQL 的 SQL 语句、Solr 的 collection+fields、HBase 的 table+columns）。

```
XxxApiController  ──→  XxxApiHandlerFactory  ──→  XxxApiHandler
                              │
                        AbstractApiHandlerFactory   CommonApiHandler
                        (抽象基类 - 模板方法)        (处理器接口)
                              │
                        CommonApiHandlerFactory
                          (工厂接口)
```

- API 配置存储在 MySQL 元数据库中。
- **模板方法模式**：`AbstractApiHandlerFactory<T extends CommonApiHandler>` 实现了 `CommonApiHandlerFactory` 接口，封装了 `registerApi / initApi / enableApi / disableApi / close` 的通用生命周期流程。各 API 工厂继承抽象基类，仅需实现 `doCreateApiHandler()`、`listApis()` 等模板方法。
- 工厂维护 `Map<Long, XxxApiHandler>`，支持运行时启禁。
- `XxxApiHandler` 解析 API 配置，生成可执行的查询语句。

### 3. `query` — 查询执行

最终用户入口：传入 API ID + 参数 → 获取数据。

```
POST /v1/query/sql
  → XxxQueryController
    → XxxQueryHandler
      → XxxApiHandlerFactory.getApiHandler(id)    // 获取 API 定义
      → XxxClientFactory.getClient(sourceId)       // 获取数据源连接
      → XxxApiHandler.generateSql()                // 生成查询语句
      → XxxClient.query()                          // 执行查询
      → handleResultSet()                          // 组装 JSON 结果
      → BaseResponse.ok(PageResult)                // 统一响应
```

- **分页策略**：通用 SQL 查询通过 `SqlPageHelperFactory` 根据数据源的 dialect 选择 `MysqlPageHelper`（LIMIT 语法）或 `OraclePageHelper`（ROWNUM 语法），实现了 `CommonPageHelper` 接口。

---

## 请求处理链路（以通用 SQL 为例）

```
客户端
  │
  ▼
SqlQueryController.query(@RequestBody SqlQueryRequest)
  │
  ▼
SqlQueryHandler.handle(sqlQueryRequest)
  │
  ├── SqlApiHandlerFactory.getApiHandler(id)
  │     └── 返回 SqlApiHandler (含 API 配置: SQL模板, 参数定义, 分页标签)
  │
  ├── 解析请求参数 → 提取命名参数 / 表达式参数
  │
  ├── SqlPageHelperFactory.getPageHelper(dialect)
  │     └── 返回 MysqlPageHelper 或 OraclePageHelper
  │
  ├── 校验必填参数
  │
  ├── SqlClientFactory.getClient(dataSourceId)
  │     └── 返回 SqlClient (含 HikariCP 连接池)
  │
  ├── SqlApiHandler.generateSql(sqlPageHelper, pageNum, pageSize)
  │     └── 生成带方言分页的 SQL
  │
  ├── SqlClient.query(countSql, params, types)
  │     └── 执行 COUNT 查询 → 总记录数
  │
  ├── SqlClient.query(sql, params, types)
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
| 数据源 | `data_source_solr` | Solr 连接配置 (zk_hosts, zk_chroots) |
| 数据源 | `data_source_sql` | 通用 SQL 连接配置 (dialect, url, username, password) |
| API | `data_api_hbase` | HBase API 定义 (table_name, columns) |
| API | `data_api_solr` | Solr API 定义 (collection, fields, conditions) |
| API | `data_api_sql` | 通用 SQL API 定义 (sql, page_tag, page_size) |
| 参数 | `data_api_sql_param` | 通用 SQL API 入参定义 (name, type, description) |

> **注意**：`data_source_mysql` 和 `data_api_mysql` 表在 DDL 中保留但无对应 DAO/Service，功能已由通用 SQL 类型替代。

所有表均包含统一审计字段：`id, name, comments, status, create_time, create_by, update_time, update_by`。

---

## 架构特点

| 特点 | 说明 |
|------|------|
| **多数据源抽象** | 通过 `CommonClient` / `CommonClientFactory` 接口统一 3 种异构数据源（HBase、Solr、通用 SQL） |
| **模板方法模式** | `AbstractClientFactory` 和 `AbstractApiHandlerFactory` 封装生命周期通用逻辑，子类仅实现差异化模板方法 |
| **模块化** | 按功能拆分为 `datasource` / `dataapi` / `query` 三个独立子模块，职责清晰 |
| **模板化 API** | 预注册 API 定义（SQL/字段/条件），查询时只需传 ID + 参数，无需暴露底层 SQL |
| **统一响应** | 所有接口返回 `BaseResponse(code, message, data)`，code 200/400/401/403/500 |
| **SQL 方言分页** | 通用 SQL 通过 `SqlPageHelperFactory` 动态选择 MySQL（LIMIT）或 Oracle（ROWNUM）分页策略 |
| **通用 SQL 设计** | `SqlSourceConf` 通过 `dialect` 字段区分 MySQL/Oracle 方言，一个数据源类型支持多种关系型数据库 |

---

## 数据源类型说明

| 数据源类型 | 客户端实现 | 连接方式 | 查询方式 |
|------------|-----------|----------|----------|
| HBase | `HbaseClient` | HBase 原生客户端 (hbase-site.xml) | Get / Scan (指定 table + columns + qualifier) |
| Solr | `SolrClient` | SolrJ (ZooKeeper) | Solr 查询 (指定 collection + fields + conditions) |
| 通用 SQL | `SqlClient` | HikariCP 连接池 | 自定义 SQL 模板 + 命名参数替换，支持 MySQL/Oracle 方言分页 |

---

## RESTful API 概览

| HTTP | Path | 模块 | 功能 |
|------|------|------|------|
| POST | `/v1/datasource/{type}/create` | datasource | 注册数据源 |
| POST | `/v1/datasource/{type}/enable` | datasource | 启用数据源 |
| POST | `/v1/datasource/{type}/disable` | datasource | 禁用数据源 |
| GET | `/v1/datasource/{type}/list` | datasource | 数据源列表 |
| POST | `/v1/api/{type}/register` | dataapi | 注册 API |
| POST | `/v1/api/{type}/enable` | dataapi | 启用 API |
| POST | `/v1/api/{type}/disable` | dataapi | 禁用 API |
| GET | `/v1/api/{type}/list` | dataapi | API 列表 |
| POST | `/v1/query/{type}` | query | 执行数据查询 |

`{type}` 取值：`hbase` / `solr` / `sql`

---

## TODO

1. 支持 Elasticsearch、Clickhouse、Redis 等更多数据源
2. 高可用和服务网关特性
3. 用户、用户组和权限管理
4. 日志和告警
5. 统一异常处理与全局校验
