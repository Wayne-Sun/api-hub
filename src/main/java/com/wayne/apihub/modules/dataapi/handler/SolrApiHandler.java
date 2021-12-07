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

import com.wayne.apihub.modules.dataapi.conf.SolrApiConf;
import lombok.Data;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.*;

/**
 * @author Wayne
 */
@Data
public class SolrApiHandler implements CommonApiHandler {
    private static final String ASC = "ASC";
    private static final String COLON = ":";
    private final Long dataSourceId;
    private final String collection;
    private final List<String> fields = new ArrayList<>();
    private final List<String> conditions = new ArrayList<>();
    private final LinkedHashMap<String, SolrQuery.ORDER> ordersMap = new LinkedHashMap<>();

    public SolrApiHandler(SolrApiConf solrApiInfo) {
        this.dataSourceId = solrApiInfo.getDataSourceId();
        this.collection = solrApiInfo.getCollection();
        String[] conditionArray = solrApiInfo.getConditions().split(",");
        String[] fieldArray = solrApiInfo.getFields().split(",");
        conditions.addAll(Arrays.asList(conditionArray));
        fields.addAll(Arrays.asList(fieldArray));
        String[] orderArray = solrApiInfo.getOrders().split(",");
        for (String order : orderArray) {
            String[] splitOrder = order.split(":");
            SolrQuery.ORDER solrOrder = splitOrder[1].equals(ASC) ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc;
            ordersMap.put(splitOrder[0], solrOrder);
        }
    }

    public SolrQuery generateQuery(List<String> params, Integer pageNum, Integer pageSize) {
        SolrQuery query = new SolrQuery();
        for (String field : fields) {
            query.addField(field);
        }
        StringBuilder queryBuilder = new StringBuilder();
        for (int i = 0, size = params.size(); i < size; i++) {
            queryBuilder.append(conditions.get(i)).append(COLON).append(params.get(i));
        }
        query.setQuery(queryBuilder.toString());
        for (Map.Entry<String, SolrQuery.ORDER> entry : ordersMap.entrySet()) {
            query.addSort(entry.getKey(), entry.getValue());
        }
        query.setStart(pageNum * pageSize);
        query.setRows(pageSize);
        return query;
    }
}
