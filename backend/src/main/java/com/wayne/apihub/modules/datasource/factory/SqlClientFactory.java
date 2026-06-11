package com.wayne.apihub.modules.datasource.factory;

import com.wayne.apihub.modules.datasource.client.SqlClient;
import com.wayne.apihub.modules.datasource.conf.BaseSourceConf;
import com.wayne.apihub.modules.datasource.conf.SqlSourceConf;
import com.wayne.apihub.service.DataApiConfService;
import com.wayne.apihub.service.DataSourceConfService;
import com.wayne.apihub.utils.Constants;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqlClientFactory extends AbstractClientFactory<SqlClient> {

    public SqlClientFactory(DataSourceConfService dataSourceConfService, DataApiConfService dataApiConfService) {
        super(dataSourceConfService, dataApiConfService);
    }

    @Override
    protected SqlClient doCreateClient(BaseSourceConf conf) {
        return new SqlClient((SqlSourceConf) conf);
    }

    @Override
    protected List<? extends BaseSourceConf> listConfs() {
        return dataSourceConfService.listSqlSourceConfs();
    }

    @Override
    protected BaseSourceConf getConfById(Long id) {
        return dataSourceConfService.getSqlSourceConfById(id);
    }

    @Override
    protected void insertConf(BaseSourceConf conf) {
        dataSourceConfService.insertSqlSourceConf((SqlSourceConf) conf);
    }

    @Override
    protected void updateConfStatus(Long id, Integer status) {
        dataSourceConfService.updateSqlSourceConfStatus(id, status);
    }

    @Override
    protected long countApiBySourceId(Long sourceId) {
        return dataApiConfService.countSqlApiConfBySourceId(sourceId);
    }

    @Override
    protected String getTypeName() {
        return Constants.DS_SQL;
    }
}
