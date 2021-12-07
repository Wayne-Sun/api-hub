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
package com.wayne.apihub.modules.dataapi.handler;

import com.wayne.apihub.modules.dataapi.conf.MysqlApiConf;
import com.wayne.apihub.utils.Constants;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Wayne
 */
@Data
public class MysqlApiHandler implements CommonApiHandler {
    private static final String SELECT = "SELECT ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final String ORDER_BY = " ORDER BY ";
    private static final String LIMIT = " LIMIT ";
    private static final String DOT = ".";
    private static final String COMMA = ",";
    private static final String COUNT_PREFIX = "SELECT COUNT(1) FROM (";
    private static final String COUNT_SUFFIX = ") COUNT_TMP";

    private final List<String> paramTypes = new ArrayList<>();
    private final List<String> columns = new ArrayList<>();
    private final Long dataSourceId;
    private final String sql;
    private final String countSql;

    public MysqlApiHandler(MysqlApiConf mysqlApiInfo) {
        this.dataSourceId = mysqlApiInfo.getDataSourceId();
        this.sql = SELECT + mysqlApiInfo.getColumns() +
                FROM + mysqlApiInfo.getDatabaseName() + DOT + mysqlApiInfo.getTableName() +
                WHERE + mysqlApiInfo.getConditions() + ORDER_BY + mysqlApiInfo.getOrders();
        this.countSql = COUNT_PREFIX + sql + COUNT_SUFFIX;
        String[] columnArray = mysqlApiInfo.getColumns().split(",");
        columns.addAll(Arrays.asList(columnArray));
        String[] conditionTypes = mysqlApiInfo.getConditionTypes().split(",");
        for (String conditionType : conditionTypes) {
            if (Constants.STRING.equals(conditionType)) {
                paramTypes.add(Constants.STRING);
            } else {
                paramTypes.add(Constants.NUMERIC);
            }
        }
    }

    public String generateSql(Integer pageNum, Integer pageSize) {
        int start = (pageNum > 1) ? (pageNum - 1) * pageSize : 0;
        return sql + LIMIT + start + COMMA + pageSize;
    }
}
