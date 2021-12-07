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

import com.wayne.apihub.modules.datasource.client.HbaseClient;
import com.wayne.apihub.modules.datasource.exception.DataSourceException;
import com.wayne.apihub.modules.datasource.info.BaseSourceConf;
import com.wayne.apihub.modules.datasource.info.HbaseSourceConf;
import com.wayne.apihub.service.DataApiConfService;
import com.wayne.apihub.service.DataSourceConfService;
import com.wayne.apihub.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Wayne
 */
@Service
@Slf4j
public class HbaseClientFactory implements CommonClientFactory {
    private final Map<Long, HbaseClient> hbaseClientMap = new ConcurrentHashMap<>();
    private final DataSourceConfService dataSourceConfService;
    private final DataApiConfService dataApiConfService;

    @Autowired
    public HbaseClientFactory(DataSourceConfService dataSourceConfService, DataApiConfService dataApiConfService) {
        this.dataSourceConfService = dataSourceConfService;
        this.dataApiConfService = dataApiConfService;
    }

    @Override
    @PostConstruct
    public void init() throws IOException {
        List<HbaseSourceConf> sourceInfoList = dataSourceConfService.listHbaseSourceConfs();
        for (HbaseSourceConf hbaseSourceInfo : sourceInfoList) {
            initClient(hbaseSourceInfo);
        }
    }

    @Override
    public void createClient(BaseSourceConf baseSourceConf) throws IOException {
        HbaseSourceConf hbaseSourceInfo = (HbaseSourceConf) baseSourceConf;
        HbaseClient hbaseClient = new HbaseClient(hbaseSourceInfo);
        dataSourceConfService.insertHbaseSourceConf(hbaseSourceInfo);
        hbaseClientMap.put(hbaseSourceInfo.getId(), hbaseClient);
        log.info("Create Hbase Client: {}", hbaseSourceInfo.getName());
    }

    @Override
    public void initClient(BaseSourceConf baseSourceConf) throws IOException {
        HbaseSourceConf hbaseSourceInfo = (HbaseSourceConf) baseSourceConf;
        HbaseClient hbaseClient = new HbaseClient(hbaseSourceInfo);
        hbaseClientMap.put(hbaseSourceInfo.getId(), hbaseClient);
        log.info("Initialize Hbase Client: {}", hbaseSourceInfo.getName());
    }

    @Override
    public void enableClient(Long id) throws DataSourceException, IOException {
        if (hbaseClientMap.containsKey(id)) {
            throw DataSourceException.alreadyEnabled();
        }
        HbaseSourceConf hbaseSourceInfo = dataSourceConfService.getHbaseSourceConfById(id);
        initClient(hbaseSourceInfo);
        dataSourceConfService.updateHbaseSourceConfStatus(id, Constants.STATUS_ENABLE);
    }

    @Override
    public void disableClient(Long id) throws DataSourceException, IOException {
        if (!hbaseClientMap.containsKey(id)) {
            throw DataSourceException.alreadyDisabled();
        }
        if (dataApiConfService.countHbaseApiConfBySourceId(id) > 0) {
            throw DataSourceException.apiNotDisabled();
        }
        hbaseClientMap.get(id).close();
        hbaseClientMap.remove(id);
        dataSourceConfService.updateHbaseSourceConfStatus(id, Constants.STATUS_DISABLE);
    }

    @Override
    public HbaseClient getClient(Long id) {
        return hbaseClientMap.get(id);
    }

    @Override
    @PreDestroy
    public void close() {
        if (!hbaseClientMap.isEmpty()) {
            for (Map.Entry<Long, HbaseClient> entry : hbaseClientMap.entrySet()) {
                try {
                    entry.getValue().close();
                } catch (IOException e) {
                    log.error("Hbase Client Close Error, name: {}, exception: {}", entry.getKey(), e);
                }
            }
            hbaseClientMap.clear();
        }
    }
}
