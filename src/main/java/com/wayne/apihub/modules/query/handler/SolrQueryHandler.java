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
import com.wayne.apihub.modules.dataapi.factory.SolrApiHandlerFactory;
import com.wayne.apihub.modules.dataapi.handler.SolrApiHandler;
import com.wayne.apihub.modules.datasource.client.SolrClient;
import com.wayne.apihub.modules.datasource.factory.SolrClientFactory;
import com.wayne.apihub.modules.query.exception.QueryParamException;
import com.wayne.apihub.modules.query.request.SolrQueryRequest;
import com.wayne.apihub.modules.query.result.PageResult;
import com.wayne.apihub.modules.query.result.QueryPageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author Wayne
 */
@Service
@Slf4j
public class SolrQueryHandler {
    private final SolrApiHandlerFactory solrApiHandlerFactory;
    private final SolrClientFactory solrClientFactory;

    @Autowired
    public SolrQueryHandler(SolrApiHandlerFactory solrApiHandlerFactory, SolrClientFactory solrClientFactory) {
        this.solrApiHandlerFactory = solrApiHandlerFactory;
        this.solrClientFactory = solrClientFactory;
    }

    public BaseResponse handle(SolrQueryRequest solrQueryRequest) throws QueryParamException, IOException, SolrServerException {
        SolrApiHandler solrApiHandler = solrApiHandlerFactory.getApiHandler(solrQueryRequest.getId());
        if (solrApiHandler == null) {
            throw QueryParamException.apiNotExists();
        }
        List<String> params = solrQueryRequest.getParams();
        List<String> conditions = solrApiHandler.getConditions();
        if (params.size() != conditions.size()) {
            throw new QueryParamException();
        }
        SolrClient solrClient = solrClientFactory.getClient(solrApiHandler.getDataSourceId());
        if (solrClient == null) {
            throw QueryParamException.srcNotExists();
        }
        Integer pageNum = solrQueryRequest.getPageNum();
        Integer pageSize = solrQueryRequest.getPageSize();
        SolrQuery solrQuery = solrApiHandler.generateQuery(params, pageNum, pageSize);
        QueryResponse queryResponse = solrClient.query(solrApiHandler.getCollection(), solrQuery);
        List<String> fields = solrApiHandler.getFields();
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        JsonArray data = handleQueryResponse(solrDocumentList, fields);
        QueryPageInfo pageInfo = new QueryPageInfo(solrDocumentList.getNumFound(), pageNum, pageSize);
        return BaseResponse.ok(new PageResult(data, pageInfo));
    }

    private JsonArray handleQueryResponse(SolrDocumentList solrDocumentList, List<String> fields) {
        JsonArray jsonArray = new JsonArray();
        for (SolrDocument document : solrDocumentList) {
            JsonObject jsonObject = new JsonObject();
            for (String field : fields) {
                jsonObject.addProperty(field, (String) document.get(field));
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
