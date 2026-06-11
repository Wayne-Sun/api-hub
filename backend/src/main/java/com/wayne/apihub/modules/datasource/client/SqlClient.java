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
    private static final Map<String, String> DIALECT_DRIVER_MAP = new HashMap<String, String>() {{
        put(Constants.DIALECT_MYSQL, Constants.JDBC_DRIVER_MYSQL);
        put(Constants.DIALECT_ORACLE, Constants.JDBC_DRIVER_ORACLE);
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
        config.setMinimumIdle(Constants.POOL_MIN_IDLE);
        config.setMaximumPoolSize(Constants.POOL_MAX_SIZE);
        config.setConnectionTestQuery(Constants.TEST_QUERY_ORACLE);
        config.setConnectionTimeout(Constants.POOL_CONN_TIMEOUT);
        config.setIdleTimeout(Constants.POOL_IDLE_TIMEOUT);
        config.setMaxLifetime(Constants.POOL_MAX_LIFETIME);
        config.setPoolName(Constants.POOL_NAME_PREFIX_SQL + sqlSourceConf.getName());
        config.addDataSourceProperty(Constants.PROP_CACHE_PREP_STMTS, Constants.PROP_VALUE_TRUE);
        config.addDataSourceProperty(Constants.PROP_PREP_STMT_CACHE_SIZE, Constants.PROP_VALUE_PREP_STMT_CACHE_SIZE);
        config.addDataSourceProperty(Constants.PROP_PREP_STMT_CACHE_SQL_LIMIT, Constants.PROP_VALUE_PREP_STMT_CACHE_SQL_LIMIT);
        config.addDataSourceProperty(Constants.PROP_USE_SERVER_PREP_STMTS, Constants.PROP_VALUE_TRUE);
        config.addDataSourceProperty(Constants.PROP_USE_LOCAL_SESSION_STATE, Constants.PROP_VALUE_TRUE);
        config.addDataSourceProperty(Constants.PROP_REWRITE_BATCHED_STMTS, Constants.PROP_VALUE_TRUE);
        config.addDataSourceProperty(Constants.PROP_CACHE_RESULT_SET_METADATA, Constants.PROP_VALUE_TRUE);
        config.addDataSourceProperty(Constants.PROP_CACHE_SERVER_CONFIGURATION, Constants.PROP_VALUE_TRUE);
        config.addDataSourceProperty(Constants.PROP_ELIDE_SET_AUTO_COMMITS, Constants.PROP_VALUE_TRUE);
        config.addDataSourceProperty(Constants.PROP_MAINTAIN_TIME_STATS, Constants.PROP_VALUE_FALSE);
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
