/**
 * Copyright 2021 Wayne
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wayne.apihub.modules.datasource.client;

import com.wayne.apihub.modules.datasource.conf.SqlSourceConf;
import com.wayne.apihub.utils.Constants;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wayne
 */
public class SqlClient implements CommonClient {
    private static final String POOL_NAME_PREFIX = "SqlSource-Connection-Pool-";
    private static final Map<String, String> DIALECT_DRIVER_MAP = new HashMap<String, String>() {{
        put(Constants.MYSQL, "com.mysql.cj.jdbc.Driver");
        put(Constants.ORACLE, "oracle.jdbc.driver.OracleDriver");
    }};
    private final String dialect;
    private final HikariDataSource hikariDataSource;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public SqlClient(SqlSourceConf sqlSourceConf) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(sqlSourceConf.getUrl());
        config.setUsername(sqlSourceConf.getUsername());
        config.setPassword(sqlSourceConf.getPassword());
        config.setDriverClassName(DIALECT_DRIVER_MAP.get(sqlSourceConf.getDialect()));
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(20);
        config.setConnectionTestQuery("select 1 from dual");
        config.setConnectionTimeout(6000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1200000);
        config.setPoolName(POOL_NAME_PREFIX + sqlSourceConf.getName());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        this.dialect = sqlSourceConf.getDialect();
        this.hikariDataSource = new HikariDataSource(config);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(hikariDataSource);
    }

    public List<Map<String, Object>> query(String query, Map<String, Object> paramMap) {
        return this.namedParameterJdbcTemplate.queryForList(query, paramMap);
    }

    public Map<String, Object> queryForOne(String query, Map<String, Object> paramMap) {
        return this.namedParameterJdbcTemplate.queryForMap(query, paramMap);
    }

    public Long queryForTotal(String query, Map<String, Object> paramMap) {
        return this.namedParameterJdbcTemplate.queryForObject(query, paramMap, Long.class);
    }

    public String getDialect() {
        return this.dialect;
    }

    @Override
    public void close() {
        this.hikariDataSource.close();
    }
}
