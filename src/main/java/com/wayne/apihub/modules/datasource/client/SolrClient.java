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

import com.wayne.apihub.modules.datasource.info.SolrSourceConf;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Wayne
 */
public class SolrClient implements CommonClient {
    private final CloudSolrClient solrClient;

    public SolrClient(SolrSourceConf info) {
        solrClient = new CloudSolrClient.Builder(Arrays.asList(info.getZkHosts().split(",").clone()), Optional.ofNullable(info.getZkChroot()))
                .withConnectionTimeout(3000)
                .build();
        solrClient.connect();
    }

    public QueryResponse query(String collection, SolrQuery query) throws IOException, SolrServerException {
        solrClient.setDefaultCollection(collection);
        return solrClient.query(query);
    }

    @Override
    public void close() throws IOException {
        solrClient.close();
    }
}
