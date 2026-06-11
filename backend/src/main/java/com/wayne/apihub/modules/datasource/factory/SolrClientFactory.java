package com.wayne.apihub.modules.datasource.factory;

import com.wayne.apihub.modules.datasource.client.SolrClient;
import com.wayne.apihub.modules.datasource.conf.BaseSourceConf;
import com.wayne.apihub.modules.datasource.conf.SolrSourceConf;
import com.wayne.apihub.service.DataApiConfService;
import com.wayne.apihub.service.DataSourceConfService;
import org.springframework.stereotype.Service;

import com.wayne.apihub.utils.Constants;
import java.util.List;

@Service
public class SolrClientFactory extends AbstractClientFactory<SolrClient> {

    public SolrClientFactory(DataSourceConfService dataSourceConfService, DataApiConfService dataApiConfService) {
        super(dataSourceConfService, dataApiConfService);
    }

    @Override
    protected SolrClient doCreateClient(BaseSourceConf conf) {
        return new SolrClient((SolrSourceConf) conf);
    }

    @Override
    protected List<? extends BaseSourceConf> listConfs() {
        return dataSourceConfService.listSolrSourceConfs();
    }

    @Override
    protected BaseSourceConf getConfById(Long id) {
        return dataSourceConfService.getSolrSourceConfById(id);
    }

    @Override
    protected void insertConf(BaseSourceConf conf) {
        dataSourceConfService.insertSolrSourceConf((SolrSourceConf) conf);
    }

    @Override
    protected void updateConfStatus(Long id, Integer status) {
        dataSourceConfService.updateSolrSourceConfStatus(id, status);
    }

    @Override
    protected long countApiBySourceId(Long sourceId) {
        return dataApiConfService.countSolrApiConfBySourceId(sourceId);
    }

    @Override
    protected String getTypeName() {
        return Constants.DS_SOLR;
    }
}
