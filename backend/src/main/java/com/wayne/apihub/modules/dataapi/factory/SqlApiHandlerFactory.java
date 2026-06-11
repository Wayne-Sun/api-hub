package com.wayne.apihub.modules.dataapi.factory;

import com.wayne.apihub.modules.dataapi.conf.BaseApiConf;
import com.wayne.apihub.modules.dataapi.conf.SqlApiConf;
import com.wayne.apihub.modules.dataapi.handler.SqlApiHandler;
import com.wayne.apihub.service.DataApiConfService;
import com.wayne.apihub.utils.Constants;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqlApiHandlerFactory extends AbstractApiHandlerFactory<SqlApiHandler> {

    public SqlApiHandlerFactory(DataApiConfService dataApiConfService) {
        super(dataApiConfService);
    }

    @Override
    protected SqlApiHandler doCreateApiHandler(BaseApiConf conf) {
        return new SqlApiHandler((SqlApiConf) conf);
    }

    @Override
    protected List<? extends BaseApiConf> listApis() {
        return dataApiConfService.listSqlApiConfs();
    }

    @Override
    protected BaseApiConf getApiConfById(Long id) {
        return dataApiConfService.getSqlApiConfById(id);
    }

    @Override
    protected void insertApiConf(BaseApiConf conf) {
        dataApiConfService.insertSqlApiConf((SqlApiConf) conf);
    }

    @Override
    protected void updateApiConfStatus(Long id, Integer status) {
        dataApiConfService.updateSqlApiConfStatus(id, status);
    }

    @Override
    protected String getTypeName() {
        return Constants.DS_SQL;
    }
}
