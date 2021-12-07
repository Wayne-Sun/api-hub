/**
 * Copyright 2021 Wayne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wayne.apihub.modules.datasource.client;

import com.wayne.apihub.modules.datasource.info.MysqlSourceConf;
import com.wayne.apihub.utils.Constants;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Wayne
 */
public class MysqlClient implements CommonClient {
    private static final String POOL_NAME_PREFIX = "Database-Pool-";
    private final HikariDataSource hikariDataSource;

    public MysqlClient(MysqlSourceConf mysqlSourceInfo) throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(mysqlSourceInfo.getUrl());
        config.setUsername(mysqlSourceInfo.getUsername());
        config.setPassword(mysqlSourceInfo.getPassword());
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(20);
        config.setConnectionTestQuery("select 1");
        config.setConnectionTimeout(6000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1200000);
        config.setPoolName(POOL_NAME_PREFIX + mysqlSourceInfo.getName());
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
        this.hikariDataSource = new HikariDataSource(config);
        hikariDataSource.getConnection();
    }

    public ResultSet query(String query, List<String> params, List<String> paramTypes) throws SQLException {
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0, size = params.size(); i < size; i++) {
                switch (paramTypes.get(i)) {
                    case Constants.STRING:
                        preparedStatement.setString(i + 1, params.get(i));
                        break;
                    case Constants.NUMERIC:
                        BigDecimal value = BigDecimal.valueOf(Double.parseDouble(params.get(i)));
                        preparedStatement.setBigDecimal(i + 1, value);
                        break;
                    default:
                        break;
                }
            }
            return preparedStatement.executeQuery();
        }
    }

    @Override
    public void close() {
        if (!hikariDataSource.isClosed()) {
            hikariDataSource.close();
        }
    }
}
