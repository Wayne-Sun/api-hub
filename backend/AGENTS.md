# BACKEND KNOWLEDGE BASE

**Domain:** `backend/` — Spring Boot 4 + Java 17 + Maven + MyBatis + MySQL.

## OVERVIEW

Spring Boot 4 REST API for unified enterprise data platform API management across SQL (MySQL/Oracle), Solr, and HBase data sources.

## PACKAGE MAP

```
com.wayne.apihub/
├── config/                    # CORS + OpenAPI/Swagger config
├── dao/                       # 6 MyBatis @Mapper interfaces (2 per type)
│   ├── DataSource{Hbase,Solr,Sql}Dao.java
│   └── DataApi{Hbase,Solr,Sql}Dao.java
├── model/                     # Base entity hierarchy
│   ├── BaseDataObject.java    # Root entity (id, createTime, updateTime, status)
│   ├── BaseResponse.java      # Generic API response wrapper
│   └── BasePageRequest.java   # Pagination params
├── modules/
│   ├── dataapi/               # API registration & lifecycle
│   │   ├── conf/              # BaseApiConf → {Hbase,Solr,Sql}ApiConf
│   │   ├── controller/        # 3 REST controllers
│   │   ├── exception/         # DataApiException
│   │   ├── factory/           # AbstractApiHandlerFactory + 4 concrete
│   │   └── handler/           # {Hbase,Solr,Sql}ApiHandler (not CommonApiHandler here)
│   ├── datasource/            # Data source management
│   │   ├── client/            # CommonClient + {Sql,Solr,Hbase}Client
│   │   ├── conf/              # BaseSourceConf → {Sql,Solr,Hbase}SourceConf
│   │   ├── controller/        # 3 REST controllers
│   │   ├── exception/         # DataSourceException
│   │   └── factory/           # AbstractClientFactory + 4 concrete
│   └── query/                 # Query execution (no factory pattern)
│       ├── controller/        # 3 REST controllers
│       ├── handler/           # Direct @Service handlers
│       ├── request/           # Query request DTOs
│       ├── sql/               # SqlParam + SqlParamBuilder
│       └── result/            # Query result DTOs
├── service/                   # 2 facade services bridging DAOs
│   ├── DataSourceConfService.java
│   └── DataApiConfService.java
└── utils/                     # Constants, JsonUtils, ResponseUtils
```

## ARCHITECTURE

- **Template Method factories**: `AbstractClientFactory` / `AbstractApiHandlerFactory` define init+lifecycle skeleton. Concrete factories (`SqlClientFactory`, `HbaseApiHandlerFactory`, etc.) implement type-specific logic. `@PostConstruct init()` restores state from DB on startup. `ConcurrentHashMap<Long,T>` holds active clients/handlers in memory.
- **3×3 module layout**: Each data source type (SQL/Solr/HBase) appears across all 3 modules as parallel files — e.g., `SqlSourceController` + `SqlApiController` + `SqlQueryController`.
- **Query module divergence**: Uses direct `@Service` handlers instead of factory pattern. Unique exception.
- **Error handling**: Per-method try-catch in all 9 controllers, returning `BaseResponse.ok()/.bad()/.error()`. No `@ControllerAdvice`.
- **No service interfaces**: Concrete `@Service` classes, constructor injection. No `@Transactional`.
- **Resources**: 6 MyBatis XML mappers in `resources/mybatis/mappers/` + `mybatis-config.xml`. One `application.yml`. `logback-spring.xml`.

## CONVENTIONS

- **Controllers**: `@Tag` + `@Slf4j` + `@RestController` + `@RequestMapping("/v1/{domain}/{type}")`. Datasource domain = `source`, dataapi domain = `api`. All return `BaseResponse`.
- **Models**: Lombok `@Data` + `@ToString(callSuper=true)`. All extend `BaseDataObject`.
- **DAO**: `@Mapper` + `@Param` on all parameters. XML mappers use `useGeneratedKeys=true`.
- **Factories**: `Abstract{Client,ApiHandler}Factory` — abstract class with `@PostConstruct init()`, `ConcurrentHashMap<Long,T>` cache, abstract `createX()` method.
- **POM**: Spring Boot 4.0.6 parent, Java 17, MyBatis 4.0.1, PageHelper 4.1.0, SpringDoc 3.0.3, SolrJ 8.11.2, HBase 2.4.17.

## KNOWN ISSUES

- `DataApiSqlMapper.xml` references `modules.common.entity.SqlParam` — does not exist. Actual class: `modules.query.sql.SqlParam`. Fix before running.
- `BaseSourceConf.subTypes` missing `SqlSourceConf.class` (only declares HBase/Solr).
- No `@Transactional` — `DataApiConfService.insertSqlApiConf()` does 2 writes without atomicity.
- `SolrSourceController.initSource()` parameter typed as `HbaseSourceConf` (copy-paste error).
- `application.yml` has hardcoded DB credentials.
- 3 controllers use redundant `@ModelAttribute @RequestBody` combo.
- No backend tests exist.

## WHERE TO ADD

| Task | Follow |
|------|--------|
| New data source type | `datasource/` pattern: client/ + conf/ + controller/ + factory/ |
| New API type | `dataapi/` pattern: conf/ + controller/ + factory/ + handler/ |
| New query type | `query/` pattern: controller/ + handler/ + request/ + result/ |
| New endpoint | Copy try-catch template from existing `{Type}Controller` |
| New DAO | `@Mapper` in `dao/` + XML in `resources/mybatis/mappers/` |
| New Exception | Extend `RuntimeException`, add to module's `exception/` |
