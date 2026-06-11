package com.wayne.apihub.modules.datasource.factory;

import com.wayne.apihub.modules.datasource.client.CommonClient;
import com.wayne.apihub.modules.datasource.conf.BaseSourceConf;
import com.wayne.apihub.modules.datasource.exception.DataSourceException;
import com.wayne.apihub.service.DataApiConfService;
import com.wayne.apihub.service.DataSourceConfService;
import com.wayne.apihub.utils.Constants;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractClientFactory<T extends CommonClient> implements CommonClientFactory {
    protected final Map<Long, T> clientMap = new ConcurrentHashMap<>();
    protected final DataSourceConfService dataSourceConfService;
    protected final DataApiConfService dataApiConfService;

    protected AbstractClientFactory(DataSourceConfService dataSourceConfService, DataApiConfService dataApiConfService) {
        this.dataSourceConfService = dataSourceConfService;
        this.dataApiConfService = dataApiConfService;
    }

    protected abstract T doCreateClient(BaseSourceConf conf) throws Exception;

    protected abstract List<? extends BaseSourceConf> listConfs();

    protected abstract BaseSourceConf getConfById(Long id);

    protected abstract void insertConf(BaseSourceConf conf);

    protected abstract void updateConfStatus(Long id, Integer status);

    protected abstract long countApiBySourceId(Long sourceId);

    protected abstract String getTypeName();

    @Override
    @PostConstruct
    public void init() throws Exception {
        for (BaseSourceConf conf : listConfs()) {
            initClient(conf);
        }
    }

    @Override
    public void createClient(BaseSourceConf baseSourceConf) throws Exception {
        T client = doCreateClient(baseSourceConf);
        clientMap.put(baseSourceConf.getId(), client);
        insertConf(baseSourceConf);
        log.info("Create {} Client: {}", getTypeName(), baseSourceConf.getName());
    }

    @Override
    public void initClient(BaseSourceConf baseSourceConf) throws Exception {
        T client = doCreateClient(baseSourceConf);
        clientMap.put(baseSourceConf.getId(), client);
        log.info("Initialize {} Client: {}", getTypeName(), baseSourceConf.getName());
    }

    @Override
    public void enableClient(Long id) throws Exception {
        if (clientMap.containsKey(id)) {
            throw DataSourceException.alreadyEnabled();
        }
        BaseSourceConf conf = getConfById(id);
        initClient(conf);
        updateConfStatus(id, Constants.STATUS_ENABLE);
    }

    @Override
    public void disableClient(Long id) throws Exception {
        if (!clientMap.containsKey(id)) {
            throw DataSourceException.alreadyDisabled();
        }
        if (countApiBySourceId(id) > 0) {
            throw DataSourceException.apiNotDisabled();
        }
        T client = clientMap.get(id);
        client.close();
        clientMap.remove(id);
        updateConfStatus(id, Constants.STATUS_DISABLE);
    }

    @Override
    public T getClient(Long id) {
        return clientMap.get(id);
    }

    @Override
    @PreDestroy
    public void close() {
        if (!clientMap.isEmpty()) {
            for (Map.Entry<Long, T> entry : clientMap.entrySet()) {
                try {
                    entry.getValue().close();
                } catch (Exception e) {
                    log.error("{} Client Close Error, name: {}, exception: {}", getTypeName(), entry.getKey(), e.getMessage());
                    log.debug("{} Client Close Error details", getTypeName(), e);
                }
            }
            clientMap.clear();
        }
    }
}
