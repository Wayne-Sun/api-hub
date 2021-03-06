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
package com.wayne.apihub.modules.dataapi.factory;

import com.wayne.apihub.modules.dataapi.conf.BaseApiConf;
import com.wayne.apihub.modules.dataapi.conf.SolrApiConf;
import com.wayne.apihub.modules.dataapi.exception.DataApiException;
import com.wayne.apihub.modules.dataapi.handler.SolrApiHandler;
import com.wayne.apihub.service.DataApiConfService;
import com.wayne.apihub.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Wayne
 */
@Service
@Slf4j
public class SolrApiHandlerFactory implements CommonApiHandlerFactory {
    private final DataApiConfService dataApiConfService;
    private final Map<Long, SolrApiHandler> solrApiHandlerMap = new ConcurrentHashMap<>();

    @Autowired
    public SolrApiHandlerFactory(DataApiConfService dataApiConfService) {
        this.dataApiConfService = dataApiConfService;
    }

    @Override
    @PostConstruct
    public void init() {
        List<SolrApiConf> solrApiInfoList = dataApiConfService.listSolrApiConfs();
        for (SolrApiConf solrApiInfo : solrApiInfoList) {
            initApi(solrApiInfo);
        }
    }

    @Override
    public void registerApi(BaseApiConf baseApiConf) {
        SolrApiConf solrApiInfo = (SolrApiConf) baseApiConf;
        SolrApiHandler solrApiHandler = new SolrApiHandler(solrApiInfo);
        dataApiConfService.insertSolrApiConf(solrApiInfo);
        solrApiHandlerMap.put(solrApiInfo.getId(), solrApiHandler);
        log.info("Register Solr API: {}", baseApiConf.getName());
    }

    @Override
    public void initApi(BaseApiConf baseApiConf) {
        SolrApiConf solrApiInfo = (SolrApiConf) baseApiConf;
        SolrApiHandler solrApiHandler = new SolrApiHandler(solrApiInfo);
        solrApiHandlerMap.put(solrApiInfo.getId(), solrApiHandler);
        log.info("Initialize Solr API: {}", baseApiConf.getName());
    }

    @Override
    public void enableApi(Long id) throws DataApiException {
        if (solrApiHandlerMap.containsKey(id)) {
            throw DataApiException.alreadyEnabled();
        }
        SolrApiConf solrApiInfo = dataApiConfService.getSolrApiConfById(id);
        initApi(solrApiInfo);
        dataApiConfService.updateSolrApiConfStatus(id, Constants.STATUS_ENABLE);
    }

    @Override
    public void disableApi(Long id) throws DataApiException {
        if (!solrApiHandlerMap.containsKey(id)) {
            throw DataApiException.alreadyDisabled();
        }
        solrApiHandlerMap.remove(id);
        dataApiConfService.updateSolrApiConfStatus(id, Constants.STATUS_DISABLE);
    }

    @Override
    public SolrApiHandler getApiHandler(Long id) {
        return solrApiHandlerMap.get(id);
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        solrApiHandlerMap.clear();
    }
}
