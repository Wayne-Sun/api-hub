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
package com.wayne.apihub.modules.query.handler;

import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.common.SqlPageHelperFactory;
import com.wayne.apihub.modules.common.page.CommonPageHelper;
import com.wayne.apihub.modules.dataapi.factory.SqlApiHandlerFactory;
import com.wayne.apihub.modules.dataapi.handler.SqlApiHandler;
import com.wayne.apihub.modules.datasource.client.SqlClient;
import com.wayne.apihub.modules.datasource.factory.SqlClientFactory;
import com.wayne.apihub.modules.query.exception.QueryParamException;
import com.wayne.apihub.modules.query.request.SqlQueryRequest;
import com.wayne.apihub.modules.query.result.PageResult;
import com.wayne.apihub.modules.query.result.QueryPageInfo;
import com.wayne.apihub.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Wayne
 */
@Service
@Slf4j
public class SqlQueryHandler {
    private final SqlApiHandlerFactory sqlApiHandlerFactory;
    private final SqlClientFactory sqlClientFactory;
    private final SqlPageHelperFactory sqlPageHelperFactory;

    @Autowired
    public SqlQueryHandler(SqlApiHandlerFactory sqlApiHandlerFactory, SqlClientFactory sqlClientFactory, SqlPageHelperFactory sqlPageHelperFactory) {
        this.sqlApiHandlerFactory = sqlApiHandlerFactory;
        this.sqlClientFactory = sqlClientFactory;
        this.sqlPageHelperFactory = sqlPageHelperFactory;
    }

    public BaseResponse handle(SqlQueryRequest sqlQueryRequest) throws QueryParamException, IOException {
        SqlApiHandler sqlApiHandler = sqlApiHandlerFactory.getApiHandler(sqlQueryRequest.getId());
        if (sqlApiHandler == null) {
            throw QueryParamException.apiNotExists();
        }
        if (!JsonUtils.checkSchema(sqlQueryRequest.getParams(), sqlApiHandler.getParamMap())) {
            throw new QueryParamException();
        }
        SqlClient sqlClient = sqlClientFactory.getClient(sqlApiHandler.getDataSourceId());
        if (sqlClient == null) {
            throw QueryParamException.srcNotExists();
        }
        BaseResponse response;
        String sql = sqlApiHandler.getSql();
        Map<String, Object> paramMap = JsonUtils.toMap(sqlQueryRequest.getParams(), sqlApiHandler.getParamMap());
        if (sqlApiHandler.getPageTag() == 1) {
            Integer pageNum = sqlQueryRequest.getPageNum();
            Integer pageSize = sqlQueryRequest.getPageSize();
            QueryPageInfo queryPageInfo = new QueryPageInfo(0, pageNum, pageSize);
            CommonPageHelper pageHelper = sqlPageHelperFactory.getPageHelper(sqlClient.getDialect());
            Long total = sqlClient.queryForTotal(getContSql(sql), paramMap);
            queryPageInfo.setTotal(total);
            sql = pageHelper.getPageSql(sql, queryPageInfo);
            paramMap = pageHelper.processParamMap(paramMap, queryPageInfo);
            List<Map<String, Object>> data = sqlClient.query(sql, paramMap);
            PageResult result = new PageResult(data, queryPageInfo);
            response = BaseResponse.ok(result);
        } else {
            Map<String, Object> data = sqlClient.queryForOne(sql, paramMap);
            response = BaseResponse.ok(data);
        }
        return response;
    }

    private String getContSql(String sql) {
        return "SELECT COUNT(*) FROM (" + sql + ")";
    }

}
