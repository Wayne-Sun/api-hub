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
package com.wayne.apihub.modules.query.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.dataapi.factory.MysqlApiHandlerFactory;
import com.wayne.apihub.modules.dataapi.handler.MysqlApiHandler;
import com.wayne.apihub.modules.datasource.client.MysqlClient;
import com.wayne.apihub.modules.datasource.factory.MysqlClientFactory;
import com.wayne.apihub.modules.query.exception.QueryParamException;
import com.wayne.apihub.modules.query.request.MysqlQueryRequest;
import com.wayne.apihub.modules.query.result.PageResult;
import com.wayne.apihub.modules.query.result.QueryPageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Wayne
 */
@Service
@Slf4j
public class MysqlQueryHandler {
    private final MysqlApiHandlerFactory mysqlApiHandlerFactory;
    private final MysqlClientFactory mysqlClientFactory;

    @Autowired
    public MysqlQueryHandler(MysqlApiHandlerFactory mysqlApiHandlerFactory, MysqlClientFactory mysqlClientFactory) {
        this.mysqlApiHandlerFactory = mysqlApiHandlerFactory;
        this.mysqlClientFactory = mysqlClientFactory;
    }

    public BaseResponse handle(MysqlQueryRequest mysqlQueryRequest) throws QueryParamException, SQLException {
        MysqlApiHandler mysqlApiHandler = mysqlApiHandlerFactory.getApiHandler(mysqlQueryRequest.getId());
        if (mysqlApiHandler == null) {
            throw QueryParamException.apiNotExists();
        }
        List<String> params = mysqlQueryRequest.getParams();
        List<String> paramTypes = mysqlApiHandler.getParamTypes();
        if (params.size() != paramTypes.size()) {
            throw new QueryParamException();
        }
        MysqlClient mysqlClient = mysqlClientFactory.getClient(mysqlApiHandler.getDataSourceId());
        if (mysqlClient == null) {
            throw QueryParamException.srcNotExists();
        }
        Integer pageNum = mysqlQueryRequest.getPageNum();
        Integer pageSize = mysqlQueryRequest.getPageSize();
        String sql = mysqlApiHandler.generateSql(pageNum, pageSize);
        String countSql = mysqlApiHandler.getCountSql();
        ResultSet countResult = mysqlClient.query(countSql, params, paramTypes);
        ResultSet resultSet = mysqlClient.query(sql, params, paramTypes);
        List<String> columns = mysqlApiHandler.getColumns();
        JsonArray data = handleResultSet(resultSet, columns);
        long total = handleCountResult(countResult);
        QueryPageInfo pageInfo = new QueryPageInfo(total, pageNum, pageSize);
        return BaseResponse.ok(new PageResult(data, pageInfo));
    }

    private JsonArray handleResultSet(ResultSet resultSet, List<String> columns) throws SQLException {
        JsonArray jsonArray = new JsonArray();
        while (resultSet.next()) {
            JsonObject jsonObject = new JsonObject();
            for (String column : columns) {
                jsonObject.addProperty(column, resultSet.getString(column));
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    private long handleCountResult(ResultSet resultSet) throws SQLException {
        resultSet.next();
        return resultSet.getLong(1);
    }
}
