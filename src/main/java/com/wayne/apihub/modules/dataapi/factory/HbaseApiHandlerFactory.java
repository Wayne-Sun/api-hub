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
package com.wayne.apihub.modules.dataapi.factory;

import com.wayne.apihub.modules.dataapi.exception.DataApiException;
import com.wayne.apihub.modules.dataapi.handler.HbaseApiHandler;
import com.wayne.apihub.modules.dataapi.info.BaseApiConf;
import com.wayne.apihub.modules.dataapi.info.HbaseApiConf;
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
public class HbaseApiHandlerFactory implements CommonApiHandlerFactory {
    private final DataApiConfService dataApiConfService;
    private final Map<Long, HbaseApiHandler> hbaseApiHandlerMap = new ConcurrentHashMap<>();

    @Autowired
    public HbaseApiHandlerFactory(DataApiConfService dataApiConfService) {
        this.dataApiConfService = dataApiConfService;
    }

    @Override
    @PostConstruct
    public void init() {
        List<HbaseApiConf> hbaseApiConfList = dataApiConfService.listHbaseApiConfs();
        for (HbaseApiConf hbaseApiConf : hbaseApiConfList) {
            initApi(hbaseApiConf);
        }
    }

    @Override
    public void registerApi(BaseApiConf baseApiConf) {
        HbaseApiConf hbaseApiConf = (HbaseApiConf) baseApiConf;
        HbaseApiHandler hbaseApiHandler = new HbaseApiHandler(hbaseApiConf);
        dataApiConfService.insertHbaseApiConf(hbaseApiConf);
        hbaseApiHandlerMap.put(hbaseApiConf.getId(), hbaseApiHandler);
        log.info("Register Hbase API: {}", baseApiConf.getName());
    }

    @Override
    public void initApi(BaseApiConf baseApiConf) {
        HbaseApiConf hbaseApiConf = (HbaseApiConf) baseApiConf;
        HbaseApiHandler hbaseApiHandler = new HbaseApiHandler(hbaseApiConf);
        hbaseApiHandlerMap.put(hbaseApiConf.getId(), hbaseApiHandler);
        log.info("Initialize Hbase API: {}", baseApiConf.getName());
    }

    @Override
    public void enableApi(Long id) throws DataApiException {
        if (hbaseApiHandlerMap.containsKey(id)) {
            throw DataApiException.alreadyEnabled();
        }
        HbaseApiConf hbaseApiConf = dataApiConfService.getHbaseApiConfById(id);
        initApi(hbaseApiConf);
        dataApiConfService.updateHbaseApiConfStatus(id, Constants.STATUS_ENABLE);
    }

    @Override
    public void disableApi(Long id) throws DataApiException {
        if (!hbaseApiHandlerMap.containsKey(id)) {
            throw DataApiException.alreadyDisabled();
        }
        hbaseApiHandlerMap.remove(id);
        dataApiConfService.updateHbaseApiConfStatus(id, Constants.STATUS_DISABLE);
    }

    @Override
    public HbaseApiHandler getApiHandler(Long id) {
        return hbaseApiHandlerMap.get(id);
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        hbaseApiHandlerMap.clear();
    }
}
