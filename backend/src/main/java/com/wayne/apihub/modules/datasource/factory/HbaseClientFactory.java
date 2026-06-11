package com.wayne.apihub.modules.datasource.factory;

import com.wayne.apihub.modules.datasource.client.HbaseClient;
import com.wayne.apihub.modules.datasource.conf.BaseSourceConf;
import com.wayne.apihub.modules.datasource.conf.HbaseSourceConf;
import com.wayne.apihub.service.DataApiConfService;
import com.wayne.apihub.service.DataSourceConfService;
import org.springframework.stereotype.Service;

import com.wayne.apihub.utils.Constants;
import java.util.List;

@Service
public class HbaseClientFactory extends AbstractClientFactory<HbaseClient> {

    public HbaseClientFactory(DataSourceConfService dataSourceConfService, DataApiConfService dataApiConfService) {
        super(dataSourceConfService, dataApiConfService);
    }

    @Override
    protected HbaseClient doCreateClient(BaseSourceConf conf) throws Exception {
        return new HbaseClient((HbaseSourceConf) conf);
    }

    @Override
    protected List<? extends BaseSourceConf> listConfs() {
        return dataSourceConfService.listHbaseSourceConfs();
    }

    @Override
    protected BaseSourceConf getConfById(Long id) {
        return dataSourceConfService.getHbaseSourceConfById(id);
    }

    @Override
    protected void insertConf(BaseSourceConf conf) {
        dataSourceConfService.insertHbaseSourceConf((HbaseSourceConf) conf);
    }

    @Override
    protected void updateConfStatus(Long id, Integer status) {
        dataSourceConfService.updateHbaseSourceConfStatus(id, status);
    }

    @Override
    protected long countApiBySourceId(Long sourceId) {
        return dataApiConfService.countHbaseApiConfBySourceId(sourceId);
    }

    @Override
    protected String getTypeName() {
        return Constants.DS_HBASE;
    }
}
