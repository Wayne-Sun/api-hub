# API HUB — Backend

Spring Boot 后端服务，提供统一的数据平台 API 管理能力。

### Features

1. 支持 Solr、HBase、MySQL（通用 SQL）数据源管理
2. 数据源注册、启禁、列表查询
3. API 注册、启禁、列表查询

### 环境要求

1. JDK 17+
2. 数据库（MySQL）用于元数据持久化，初始化脚本见 `src/main/resources/metadata/init.sql`

### 构建

```shell
mvn clean package
```

### TODO

1. 增加 Elasticsearch、Clickhouse、Redis 等支持
2. 高可用与服务网关
3. 用户、用户组与权限
4. 日志与告警