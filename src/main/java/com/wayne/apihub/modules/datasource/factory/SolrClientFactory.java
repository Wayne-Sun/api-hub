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
package com.wayne.apihub.modules.datasource.factory;

import com.wayne.apihub.modules.datasource.client.SolrClient;
import com.wayne.apihub.modules.datasource.exception.DataSourceException;
import com.wayne.apihub.modules.datasource.info.BaseSourceConf;
import com.wayne.apihub.modules.datasource.info.SolrSourceConf;
import com.wayne.apihub.service.DataApiConfService;
import com.wayne.apihub.service.DataSourceConfService;
import com.wayne.apihub.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wayne
 */
@Service
@Slf4j
public class SolrClientFactory implements CommonClientFactory {
    private final Map<Long, SolrClient> solrClientMap = new HashMap<>();
    private final DataSourceConfService dataSourceConfService;
    private final DataApiConfService dataApiConfService;

    @Autowired
    public SolrClientFactory(DataSourceConfService dataSourceConfService, DataApiConfService dataApiConfService) {
        this.dataSourceConfService = dataSourceConfService;
        this.dataApiConfService = dataApiConfService;
    }

    @Override
    @PostConstruct
    public void init() {
        List<SolrSourceConf> sourceInfoList = dataSourceConfService.listSolrSourceConfs();
        for (SolrSourceConf solrSourceInfo : sourceInfoList) {
            initClient(solrSourceInfo);
        }
    }

    @Override
    public void createClient(BaseSourceConf baseSourceConf) {
        SolrSourceConf solrSourceInfo = (SolrSourceConf) baseSourceConf;
        SolrClient solrClient = new SolrClient(solrSourceInfo);
        solrClientMap.put(solrSourceInfo.getId(), solrClient);
        dataSourceConfService.insertSolrSourceConf(solrSourceInfo);
        log.info("Create Solr Client: {}", solrSourceInfo.getName());
    }

    @Override
    public void initClient(BaseSourceConf baseSourceConf) {
        SolrSourceConf solrSourceInfo = (SolrSourceConf) baseSourceConf;
        SolrClient solrClient = new SolrClient(solrSourceInfo);
        solrClientMap.put(solrSourceInfo.getId(), solrClient);
        log.info("Initialize Solr Client: {}", solrSourceInfo.getName());
    }

    @Override
    public void enableClient(Long id) throws DataSourceException {
        if (solrClientMap.containsKey(id)) {
            throw DataSourceException.alreadyEnabled();
        }
        SolrSourceConf solrSourceInfo = dataSourceConfService.getSolrSourceConfById(id);
        initClient(solrSourceInfo);
        dataSourceConfService.updateSolrSourceConfStatus(id, Constants.STATUS_ENABLE);
    }

    @Override
    public void disableClient(Long id) throws DataSourceException, IOException {
        if (!solrClientMap.containsKey(id)) {
            throw DataSourceException.alreadyDisabled();
        }
        if (dataApiConfService.countSolrApiConfBySourceId(id) > 0) {
            throw DataSourceException.apiNotDisabled();
        }
        solrClientMap.get(id).close();
        solrClientMap.remove(id);
        dataSourceConfService.updateSolrSourceConfStatus(id, Constants.STATUS_DISABLE);
    }

    @Override
    public SolrClient getClient(Long id) {
        return solrClientMap.get(id);
    }

    @Override
    @PreDestroy
    public void close() {
        if (!solrClientMap.isEmpty()) {
            for (Map.Entry<Long, SolrClient> entry : solrClientMap.entrySet()) {
                try {
                    entry.getValue().close();
                } catch (IOException e) {
                    log.error("Solr Client Close Error, name: {}, exception: {}", entry.getKey(), e);
                }
            }
            solrClientMap.clear();
        }
    }
}
