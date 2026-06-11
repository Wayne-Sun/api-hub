package com.wayne.apihub.modules.dataapi.factory;

import com.wayne.apihub.modules.dataapi.conf.BaseApiConf;
import com.wayne.apihub.modules.dataapi.exception.DataApiException;
import com.wayne.apihub.modules.dataapi.handler.CommonApiHandler;
import com.wayne.apihub.service.DataApiConfService;
import com.wayne.apihub.utils.Constants;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractApiHandlerFactory<T extends CommonApiHandler> implements CommonApiHandlerFactory {
    protected final Map<Long, T> apiHandlerMap = new ConcurrentHashMap<>();
    protected final DataApiConfService dataApiConfService;

    protected AbstractApiHandlerFactory(DataApiConfService dataApiConfService) {
        this.dataApiConfService = dataApiConfService;
    }

    protected abstract T doCreateApiHandler(BaseApiConf conf);

    protected abstract List<? extends BaseApiConf> listApis();

    protected abstract BaseApiConf getApiConfById(Long id);

    protected abstract void insertApiConf(BaseApiConf conf);

    protected abstract void updateApiConfStatus(Long id, Integer status);

    protected abstract String getTypeName();

    @Override
    @PostConstruct
    public void init() throws Exception {
        for (BaseApiConf conf : listApis()) {
            initApi(conf);
        }
    }

    @Override
    public void registerApi(BaseApiConf baseApiConf) {
        T handler = doCreateApiHandler(baseApiConf);
        apiHandlerMap.put(baseApiConf.getId(), handler);
        insertApiConf(baseApiConf);
        log.info("Register {} API: {}", getTypeName(), baseApiConf.getName());
    }

    @Override
    public void initApi(BaseApiConf baseApiConf) {
        T handler = doCreateApiHandler(baseApiConf);
        apiHandlerMap.put(baseApiConf.getId(), handler);
        log.info("Initialize {} API: {}", getTypeName(), baseApiConf.getName());
    }

    @Override
    public void enableApi(Long id) throws Exception {
        if (apiHandlerMap.containsKey(id)) {
            throw DataApiException.alreadyEnabled();
        }
        BaseApiConf conf = getApiConfById(id);
        initApi(conf);
        updateApiConfStatus(id, Constants.STATUS_ENABLE);
    }

    @Override
    public void disableApi(Long id) throws Exception {
        if (!apiHandlerMap.containsKey(id)) {
            throw DataApiException.alreadyDisabled();
        }
        apiHandlerMap.remove(id);
        updateApiConfStatus(id, Constants.STATUS_DISABLE);
    }

    @Override
    public T getApiHandler(Long id) {
        return apiHandlerMap.get(id);
    }

    @Override
    @PreDestroy
    public void close() {
        apiHandlerMap.clear();
    }
}
