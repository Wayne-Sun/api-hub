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
import com.wayne.apihub.modules.dataapi.conf.SqlApiConf;
import com.wayne.apihub.modules.dataapi.exception.DataApiException;
import com.wayne.apihub.modules.dataapi.handler.SqlApiHandler;
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
public class SqlApiHandlerFactory implements CommonApiHandlerFactory {

    private final DataApiConfService dataApiConfService;
    private final Map<Long, SqlApiHandler> sqlApiHandlerMap = new ConcurrentHashMap<>();

    @Autowired
    public SqlApiHandlerFactory(DataApiConfService dataApiConfService) {
        this.dataApiConfService = dataApiConfService;
    }

    @Override
    @PostConstruct
    public void init() throws Exception {
        List<SqlApiConf> sqlApiConfList = dataApiConfService.listSqlApiConfs();
        for (SqlApiConf sqlApiConf : sqlApiConfList) {
            initApi(sqlApiConf);
        }
    }

    @Override
    public void registerApi(BaseApiConf baseApiConf) {
        SqlApiConf sqlApiConf = (SqlApiConf) baseApiConf;
        SqlApiHandler sqlApiHandler = new SqlApiHandler(sqlApiConf);
        dataApiConfService.insertSqlApiConf(sqlApiConf);
        sqlApiHandlerMap.put(sqlApiConf.getId(), sqlApiHandler);
        log.info("Register sql API: {}", baseApiConf.getName());
    }

    @Override
    public void initApi(BaseApiConf baseApiConf) {
        SqlApiConf sqlApiConf = (SqlApiConf) baseApiConf;
        SqlApiHandler sqlApiHandler = new SqlApiHandler(sqlApiConf);
        sqlApiHandlerMap.put(sqlApiConf.getId(), sqlApiHandler);
        log.info("Initialize sql API: {}", baseApiConf.getName());
    }

    @Override
    public void enableApi(Long id) throws DataApiException {
        if (sqlApiHandlerMap.containsKey(id)) {
            throw DataApiException.alreadyEnabled();
        }
        SqlApiConf sqlApiConf = dataApiConfService.getSqlApiConfById(id);
        initApi(sqlApiConf);
        dataApiConfService.updateSqlApiConfStatus(id, Constants.STATUS_ENABLE);
    }

    @Override
    public void disableApi(Long id) throws DataApiException {
        if (!sqlApiHandlerMap.containsKey(id)) {
            throw DataApiException.alreadyDisabled();
        }
        sqlApiHandlerMap.remove(id);
        dataApiConfService.updateSqlApiConfStatus(id, Constants.STATUS_DISABLE);
    }

    @Override
    public SqlApiHandler getApiHandler(Long id) {
        return sqlApiHandlerMap.get(id);
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        sqlApiHandlerMap.clear();
    }
}
