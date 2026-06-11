package com.wayne.apihub.modules.dataapi.factory;

import com.wayne.apihub.modules.dataapi.conf.BaseApiConf;
import com.wayne.apihub.modules.dataapi.conf.SolrApiConf;
import com.wayne.apihub.modules.dataapi.handler.SolrApiHandler;
import com.wayne.apihub.service.DataApiConfService;
import org.springframework.stereotype.Service;

import com.wayne.apihub.utils.Constants;
import java.util.List;

@Service
public class SolrApiHandlerFactory extends AbstractApiHandlerFactory<SolrApiHandler> {

    public SolrApiHandlerFactory(DataApiConfService dataApiConfService) {
        super(dataApiConfService);
    }

    @Override
    protected SolrApiHandler doCreateApiHandler(BaseApiConf conf) {
        return new SolrApiHandler((SolrApiConf) conf);
    }

    @Override
    protected List<? extends BaseApiConf> listApis() {
        return dataApiConfService.listSolrApiConfs();
    }

    @Override
    protected BaseApiConf getApiConfById(Long id) {
        return dataApiConfService.getSolrApiConfById(id);
    }

    @Override
    protected void insertApiConf(BaseApiConf conf) {
        dataApiConfService.insertSolrApiConf((SolrApiConf) conf);
    }

    @Override
    protected void updateApiConfStatus(Long id, Integer status) {
        dataApiConfService.updateSolrApiConfStatus(id, status);
    }

    @Override
    protected String getTypeName() {
        return Constants.DS_SOLR;
    }
}
