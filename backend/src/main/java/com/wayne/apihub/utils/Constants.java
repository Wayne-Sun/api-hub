package com.wayne.apihub.utils;

public class Constants {

    // ======================== 状态 ========================
    public static final Integer STATUS_ENABLE = 1;
    public static final Integer STATUS_DISABLE = 0;

    // ======================== 参数类型 ========================
    public static final String PARAM_TYPE_STRING = "STRING";
    public static final String PARAM_TYPE_NUMERIC = "NUMERIC";

    // ======================== SQL 方言 ========================
    public static final String DIALECT_ORACLE = "ORACLE";
    public static final String DIALECT_MYSQL = "MYSQL";

    // ======================== SQL 命名参数 ========================
    public static final String PARAM_START_ROW = "PARAM_START_ROW";
    public static final String PARAM_END_ROW = "PARAM_END_ROW";

    // ======================== 数据源类型名称 ========================
    public static final String DS_MYSQL = "Mysql";
    public static final String DS_SQL = "Sql";
    public static final String DS_SOLR = "Solr";
    public static final String DS_HBASE = "Hbase";

    // ======================== HBase API 类型 ========================
    public static final int HBASE_API_TYPE_GET = 0;
    public static final int HBASE_API_TYPE_SCAN = 1;

    // ======================== 分隔符 ========================
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String DOT = ".";

    // ======================== HikariCP 连接池配置 ========================
    public static final Integer POOL_MIN_IDLE = 1;
    public static final Integer POOL_MAX_SIZE = 20;
    public static final Long POOL_CONN_TIMEOUT = 6000L;
    public static final Long POOL_IDLE_TIMEOUT = 600000L;
    public static final Long POOL_MAX_LIFETIME = 1200000L;

    // ======================== JDBC 驱动 ========================
    public static final String JDBC_DRIVER_MYSQL = "com.mysql.cj.jdbc.Driver";
    public static final String JDBC_DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver";

    // ======================== 连接测试查询 ========================
    public static final String TEST_QUERY_MYSQL = "select 1";
    public static final String TEST_QUERY_ORACLE = "select 1 from dual";

    // ======================== HikariCP 连接池名称前缀 ========================
    public static final String POOL_NAME_PREFIX_MYSQL = "Database-Pool-";
    public static final String POOL_NAME_PREFIX_SQL = "SqlSource-Connection-Pool-";

    // ======================== HikariCP DataSource 属性 ========================
    public static final String PROP_CACHE_PREP_STMTS = "cachePrepStmts";
    public static final String PROP_PREP_STMT_CACHE_SIZE = "prepStmtCacheSize";
    public static final String PROP_PREP_STMT_CACHE_SQL_LIMIT = "prepStmtCacheSqlLimit";
    public static final String PROP_USE_SERVER_PREP_STMTS = "useServerPrepStmts";
    public static final String PROP_USE_LOCAL_SESSION_STATE = "useLocalSessionState";
    public static final String PROP_REWRITE_BATCHED_STMTS = "rewriteBatchedStatements";
    public static final String PROP_CACHE_RESULT_SET_METADATA = "cacheResultSetMetadata";
    public static final String PROP_CACHE_SERVER_CONFIGURATION = "cacheServerConfiguration";
    public static final String PROP_ELIDE_SET_AUTO_COMMITS = "elideSetAutoCommits";
    public static final String PROP_MAINTAIN_TIME_STATS = "maintainTimeStats";

    // ======================== HikariCP DataSource 属性值 ========================
    public static final String PROP_VALUE_TRUE = "true";
    public static final String PROP_VALUE_FALSE = "false";
    public static final String PROP_VALUE_PREP_STMT_CACHE_SIZE = "250";
    public static final String PROP_VALUE_PREP_STMT_CACHE_SQL_LIMIT = "2048";

    // ======================== 编码与 Content-Type ========================
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String CONTENT_TYPE_JSON = "application/json";

    // ======================== Solr 客户端 ========================
    public static final Integer SOLR_CONN_TIMEOUT = 3000;

    // ======================== CORS ========================
    public static final Long CORS_MAX_AGE = 3600L;

    // ======================== SQL 模式匹配 ========================
    public static final String REGEX_NAMED_PARAM = ":(\\w+)";
    public static final String REGEX_EXPR_PARAM = "\\$\\{(\\w+)}";

    // ======================== SQL 查询模板 ========================
    public static final String COUNT_SQL_WRAPPER = "SELECT COUNT(*) FROM (%s)";
    public static final String COUNT_SQL_WRAPPER_MYSQL = "SELECT COUNT(1) FROM (%s) COUNT_TMP";

    private Constants() {
    }
}
