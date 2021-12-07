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
import com.wayne.apihub.modules.dataapi.handler.MysqlApiHandler;
import com.wayne.apihub.modules.dataapi.info.BaseApiConf;
import com.wayne.apihub.modules.dataapi.info.MysqlApiConf;
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
public class MysqlApiHandlerFactory implements CommonApiHandlerFactory {
    private final DataApiConfService dataApiConfService;
    private final Map<Long, MysqlApiHandler> mysqlApiHandlerMap = new ConcurrentHashMap<>();

    @Autowired
    public MysqlApiHandlerFactory(DataApiConfService dataApiConfService) {
        this.dataApiConfService = dataApiConfService;
    }

    @Override
    @PostConstruct
    public void init() {
        List<MysqlApiConf> mysqlApiInfoList = dataApiConfService.listMysqlApiConfs();
        for (MysqlApiConf mysqlApiInfo : mysqlApiInfoList) {
            initApi(mysqlApiInfo);
        }
    }

    @Override
    public void registerApi(BaseApiConf baseApiConf) {
        MysqlApiConf mysqlApiInfo = (MysqlApiConf) baseApiConf;
        MysqlApiHandler mysqlApiHandler = new MysqlApiHandler(mysqlApiInfo);
        dataApiConfService.insertMysqlApiConf(mysqlApiInfo);
        mysqlApiHandlerMap.put(mysqlApiInfo.getId(), mysqlApiHandler);
        log.info("Register Mysql API: {}", baseApiConf.getName());
    }

    @Override
    public void initApi(BaseApiConf baseApiConf) {
        MysqlApiConf mysqlApiInfo = (MysqlApiConf) baseApiConf;
        MysqlApiHandler mysqlApiHandler = new MysqlApiHandler(mysqlApiInfo);
        mysqlApiHandlerMap.put(mysqlApiInfo.getId(), mysqlApiHandler);
        log.info("Initialize Mysql API: {}", baseApiConf.getName());
    }

    @Override
    public void enableApi(Long id) throws DataApiException {
        if (mysqlApiHandlerMap.containsKey(id)) {
            throw DataApiException.alreadyEnabled();
        }
        MysqlApiConf mysqlApiInfo = dataApiConfService.getMysqlApiConfById(id);
        initApi(mysqlApiInfo);
        dataApiConfService.updateMysqlApiConfStatus(id, Constants.STATUS_ENABLE);
    }

    @Override
    public void disableApi(Long id) throws DataApiException {
        if (!mysqlApiHandlerMap.containsKey(id)) {
            throw DataApiException.alreadyDisabled();
        }
        mysqlApiHandlerMap.remove(id);
        dataApiConfService.updateMysqlApiConfStatus(id, Constants.STATUS_DISABLE);
    }

    @Override
    public MysqlApiHandler getApiHandler(Long id) {
        return mysqlApiHandlerMap.get(id);
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        mysqlApiHandlerMap.clear();
    }
}
