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

import com.wayne.apihub.modules.datasource.client.MysqlClient;
import com.wayne.apihub.modules.datasource.exception.DataSourceException;
import com.wayne.apihub.modules.datasource.info.BaseSourceConf;
import com.wayne.apihub.modules.datasource.info.MysqlSourceConf;
import com.wayne.apihub.service.DataApiConfService;
import com.wayne.apihub.service.DataSourceConfService;
import com.wayne.apihub.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wayne
 */
@Service
@Slf4j
public class MysqlClientFactory implements CommonClientFactory {
    private final Map<Long, MysqlClient> mysqlClientMap = new HashMap<>();
    private final DataSourceConfService dataSourceConfService;
    private final DataApiConfService dataApiConfService;

    @Autowired
    public MysqlClientFactory(DataSourceConfService dataSourceConfService, DataApiConfService dataApiConfService) {
        this.dataSourceConfService = dataSourceConfService;
        this.dataApiConfService = dataApiConfService;
    }

    @Override
    @PostConstruct
    public void init() throws SQLException {
        List<MysqlSourceConf> sourceInfoList = dataSourceConfService.listMysqlSourceConfs();
        for (MysqlSourceConf mysqlSourceInfo : sourceInfoList) {
            initClient(mysqlSourceInfo);
        }
    }

    @Override
    public void createClient(BaseSourceConf baseSourceConf) throws SQLException {
        MysqlSourceConf mysqlSourceInfo = (MysqlSourceConf) baseSourceConf;
        MysqlClient mysqlClient = new MysqlClient(mysqlSourceInfo);
        mysqlClientMap.put(mysqlSourceInfo.getId(), mysqlClient);
        dataSourceConfService.insertMysqlSourceConf(mysqlSourceInfo);
        log.info("Create Mysql Client: {}", mysqlSourceInfo.getName());
    }

    @Override
    public void initClient(BaseSourceConf baseSourceConf) throws SQLException {
        MysqlSourceConf mysqlSourceInfo = (MysqlSourceConf) baseSourceConf;
        MysqlClient mysqlClient = new MysqlClient(mysqlSourceInfo);
        mysqlClientMap.put(mysqlSourceInfo.getId(), mysqlClient);
        log.info("Initialize Mysql Client: {}", mysqlSourceInfo.getName());
    }

    @Override
    public void enableClient(Long id) throws DataSourceException, SQLException {
        if (mysqlClientMap.containsKey(id)) {
            throw DataSourceException.alreadyEnabled();
        }
        MysqlSourceConf mysqlSourceInfo = dataSourceConfService.getMysqlSourceConfById(id);
        initClient(mysqlSourceInfo);
        dataSourceConfService.updateMysqlSourceConfStatus(id, Constants.STATUS_ENABLE);
    }

    @Override
    public void disableClient(Long id) throws DataSourceException {
        if (!mysqlClientMap.containsKey(id)) {
            throw DataSourceException.alreadyDisabled();
        }
        if (dataApiConfService.countMysqlApiConfBySourceId(id) > 0) {
            throw DataSourceException.apiNotDisabled();
        }
        mysqlClientMap.get(id).close();
        mysqlClientMap.remove(id);
        dataSourceConfService.updateMysqlSourceConfStatus(id, Constants.STATUS_DISABLE);
    }

    @Override
    public MysqlClient getClient(Long id) {
        return mysqlClientMap.get(id);
    }

    @Override
    @PreDestroy
    public void close() {
        if (!mysqlClientMap.isEmpty()) {
            for (Map.Entry<Long, MysqlClient> entry : mysqlClientMap.entrySet()) {
                entry.getValue().close();
            }
            mysqlClientMap.clear();
        }
    }
}
