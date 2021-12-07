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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.model.HbaseColumnFamily;
import com.wayne.apihub.modules.dataapi.factory.HbaseApiHandlerFactory;
import com.wayne.apihub.modules.dataapi.handler.HbaseApiHandler;
import com.wayne.apihub.modules.datasource.client.HbaseClient;
import com.wayne.apihub.modules.datasource.factory.HbaseClientFactory;
import com.wayne.apihub.modules.query.exception.QueryParamException;
import com.wayne.apihub.modules.query.request.HbaseQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Wayne
 */
@Service
@Slf4j
public class HbaseQueryHandler {
    private static final int TYPE_GET = 0;
    private static final int TYPE_SCAN = 1;
    private final HbaseApiHandlerFactory hbaseApiHandlerFactory;
    private final HbaseClientFactory hbaseClientFactory;

    @Autowired
    public HbaseQueryHandler(HbaseApiHandlerFactory hbaseApiHandlerFactory, HbaseClientFactory hbaseClientFactory) {
        this.hbaseApiHandlerFactory = hbaseApiHandlerFactory;
        this.hbaseClientFactory = hbaseClientFactory;
    }

    public BaseResponse handle(HbaseQueryRequest hbaseQueryRequest) throws IOException, QueryParamException {
        HbaseApiHandler hbaseApiHandler = hbaseApiHandlerFactory.getApiHandler(hbaseQueryRequest.getId());
        if (hbaseApiHandler == null) {
            throw QueryParamException.apiNotExists();
        }
        HbaseClient hbaseClient = hbaseClientFactory.getClient(hbaseApiHandler.getDataSourceId());
        if (hbaseClient == null) {
            throw QueryParamException.srcNotExists();
        }
        BaseResponse baseResponse;
        switch (hbaseApiHandler.getType()) {
            case TYPE_GET:
                baseResponse = handleGet(hbaseApiHandler, hbaseClient, hbaseQueryRequest);
                break;
            case TYPE_SCAN:
                baseResponse = handleScan(hbaseApiHandler, hbaseClient, hbaseQueryRequest);
                break;
            default:
                baseResponse = BaseResponse.bad("Hbase请求类型注册错误");
                break;
        }
        return baseResponse;
    }

    private BaseResponse handleGet(HbaseApiHandler hbaseApiHandler, HbaseClient hbaseClient, HbaseQueryRequest hbaseQueryRequest) throws IOException {
        String tableName = hbaseApiHandler.getTableName();
        Get get = hbaseApiHandler.generateGet(hbaseQueryRequest.getStartRowKey());
        Result result = hbaseClient.get(tableName, get);
        List<HbaseColumnFamily> identifierList = hbaseApiHandler.getHbaseColumnFamilyList();
        return BaseResponse.ok(handleResult(result, identifierList));
    }

    private BaseResponse handleScan(HbaseApiHandler hbaseApiHandler, HbaseClient hbaseClient, HbaseQueryRequest hbaseQueryRequest) throws IOException {
        String tableName = hbaseApiHandler.getTableName();
        Scan scan = hbaseApiHandler.generateScan(hbaseQueryRequest.getStartRowKey(), hbaseQueryRequest.getEndRowKey());
        List<Result> result = hbaseClient.scan(tableName, scan);
        List<HbaseColumnFamily> identifierList = hbaseApiHandler.getHbaseColumnFamilyList();
        return BaseResponse.ok(handleResults(result, identifierList));
    }

    private JsonObject handleResult(Result result, List<HbaseColumnFamily> identifierList) {
        JsonObject jsonObject = new JsonObject();
        for (HbaseColumnFamily identifier : identifierList) {
            byte[] value = result.getValue(identifier.getFamilyBytes(), identifier.getColumnBytes());
            jsonObject.addProperty(identifier.getIdentifier(), new String(value, StandardCharsets.UTF_8));
        }
        return jsonObject;
    }

    private JsonArray handleResults(List<Result> results, List<HbaseColumnFamily> identifierList) {
        JsonArray jsonArray = new JsonArray();
        for (Result result : results) {
            jsonArray.add(handleResult(result, identifierList));
        }
        return jsonArray;
    }
}
