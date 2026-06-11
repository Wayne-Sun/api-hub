package com.wayne.apihub.modules.dataapi.factory;

import com.wayne.apihub.modules.dataapi.conf.BaseApiConf;
import com.wayne.apihub.modules.dataapi.conf.HbaseApiConf;
import com.wayne.apihub.modules.dataapi.handler.HbaseApiHandler;
import com.wayne.apihub.service.DataApiConfService;
import org.springframework.stereotype.Service;

import com.wayne.apihub.utils.Constants;
import java.util.List;

@Service
public class HbaseApiHandlerFactory extends AbstractApiHandlerFactory<HbaseApiHandler> {

    public HbaseApiHandlerFactory(DataApiConfService dataApiConfService) {
        super(dataApiConfService);
    }

    @Override
    protected HbaseApiHandler doCreateApiHandler(BaseApiConf conf) {
        return new HbaseApiHandler((HbaseApiConf) conf);
    }

    @Override
    protected List<? extends BaseApiConf> listApis() {
        return dataApiConfService.listHbaseApiConfs();
    }

    @Override
    protected BaseApiConf getApiConfById(Long id) {
        return dataApiConfService.getHbaseApiConfById(id);
    }

    @Override
    protected void insertApiConf(BaseApiConf conf) {
        dataApiConfService.insertHbaseApiConf((HbaseApiConf) conf);
    }

    @Override
    protected void updateApiConfStatus(Long id, Integer status) {
        dataApiConfService.updateHbaseApiConfStatus(id, status);
    }

    @Override
    protected String getTypeName() {
        return Constants.DS_HBASE;
    }
}
