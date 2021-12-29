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
package com.wayne.apihub.modules.datasource.factory;

import com.wayne.apihub.modules.datasource.client.SqlClient;
import com.wayne.apihub.modules.datasource.conf.BaseSourceConf;
import com.wayne.apihub.modules.datasource.conf.SqlSourceConf;
import com.wayne.apihub.modules.datasource.exception.DataSourceException;
import com.wayne.apihub.service.DataApiConfService;
import com.wayne.apihub.service.DataSourceConfService;
import com.wayne.apihub.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wayne
 */
@Service
@Slf4j
public class SqlClientFactory implements CommonClientFactory {
    private final Map<Long, SqlClient> sqlClientMap = new HashMap<>();
    private final DataSourceConfService dataSourceConfService;
    private final DataApiConfService dataApiConfService;

    @Autowired
    public SqlClientFactory(DataSourceConfService dataSourceConfService, DataApiConfService dataApiConfService) {
        this.dataSourceConfService = dataSourceConfService;
        this.dataApiConfService = dataApiConfService;
    }

    @Override
    @PostConstruct
    public void init() throws Exception {
        List<SqlSourceConf> sourceInfoList = dataSourceConfService.listSqlSourceConfs();
        for (SqlSourceConf sqlSourceConf : sourceInfoList) {
            initClient(sqlSourceConf);
        }
    }

    @Override
    public void createClient(BaseSourceConf baseSourceConf) {
        SqlSourceConf sqlSourceConf = (SqlSourceConf) baseSourceConf;
        SqlClient sqlClient = new SqlClient(sqlSourceConf);
        sqlClientMap.put(sqlSourceConf.getId(), sqlClient);
        dataSourceConfService.insertSqlSourceConf(sqlSourceConf);
        log.info("Create sql Client: {}", sqlSourceConf.getName());
    }

    @Override
    public void initClient(BaseSourceConf baseSourceConf) {
        SqlSourceConf sqlSourceConf = (SqlSourceConf) baseSourceConf;
        SqlClient sqlClient = new SqlClient(sqlSourceConf);
        sqlClientMap.put(sqlSourceConf.getId(), sqlClient);
        log.info("Initialize sql Client: {}", sqlSourceConf.getName());
    }

    @Override
    public void enableClient(Long id) throws Exception {
        if (sqlClientMap.containsKey(id)) {
            throw DataSourceException.alreadyEnabled();
        }
        SqlSourceConf sqlSourceConf = dataSourceConfService.getSqlSourceConfById(id);
        initClient(sqlSourceConf);
        dataSourceConfService.updateSqlSourceConfStatus(id, Constants.STATUS_ENABLE);
    }

    @Override
    public void disableClient(Long id) throws Exception {
        if (!sqlClientMap.containsKey(id)) {
            throw DataSourceException.alreadyDisabled();
        }
        if (dataApiConfService.countSqlApiConfBySourceId(id) > 0) {
            throw DataSourceException.apiNotDisabled();
        }
        sqlClientMap.get(id).close();
        sqlClientMap.remove(id);
        dataSourceConfService.updateSqlSourceConfStatus(id, Constants.STATUS_DISABLE);
    }

    @Override
    public SqlClient getClient(Long id) {
        return this.sqlClientMap.get(id);
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        if (!sqlClientMap.isEmpty()) {
            for (Map.Entry<Long, SqlClient> entry : sqlClientMap.entrySet()) {
                entry.getValue().close();
            }
            sqlClientMap.clear();
        }
    }
}
