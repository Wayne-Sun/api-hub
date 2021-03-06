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
package com.wayne.apihub.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wayne.apihub.dao.DataApiHbaseDao;
import com.wayne.apihub.dao.DataApiMysqlDao;
import com.wayne.apihub.dao.DataApiSolrDao;
import com.wayne.apihub.dao.DataApiSqlDao;
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.dataapi.conf.HbaseApiConf;
import com.wayne.apihub.modules.dataapi.conf.MysqlApiConf;
import com.wayne.apihub.modules.dataapi.conf.SolrApiConf;
import com.wayne.apihub.modules.dataapi.conf.SqlApiConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Wayne
 */
@Service
public class DataApiConfService {
    private final DataApiHbaseDao dataApiHbaseDao;
    private final DataApiMysqlDao dataApiMysqlDao;
    private final DataApiSolrDao dataApiSolrDao;
    private final DataApiSqlDao dataApiSqlDao;

    @Autowired
    public DataApiConfService(DataApiHbaseDao dataApiHbaseDao, DataApiMysqlDao dataApiMysqlDao,
                              DataApiSolrDao dataApiSolrDao, DataApiSqlDao dataApiSqlDao) {
        this.dataApiHbaseDao = dataApiHbaseDao;
        this.dataApiMysqlDao = dataApiMysqlDao;
        this.dataApiSolrDao = dataApiSolrDao;
        this.dataApiSqlDao = dataApiSqlDao;
    }

    /**
     * Hbase api operation
     */
    public void insertHbaseApiConf(HbaseApiConf hbaseApiConf) {
        dataApiHbaseDao.insertHbaseApi(hbaseApiConf);
    }

    public BaseResponse listHbaseApiConfs(Integer pageNum, Integer pageSize) {
        Page<HbaseApiConf> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(dataApiHbaseDao::listHbaseApi);
        return BaseResponse.ok(page);
    }

    public List<HbaseApiConf> listHbaseApiConfs() {
        return dataApiHbaseDao.listHbaseApi();
    }

    public HbaseApiConf getHbaseApiConfById(Long id) {
        return dataApiHbaseDao.getHbaseApiById(id);
    }

    public void updateHbaseApiConfStatus(Long id, Integer status) {
        dataApiHbaseDao.updateHbaseApiStatus(id, status);
    }

    public Long countHbaseApiConfBySourceId(Long sourceId) {
        return dataApiHbaseDao.countHbaseApiBySourceId(sourceId);
    }

    /**
     * Mysql api operation
     */
    public void insertMysqlApiConf(MysqlApiConf mysqlApiConf) {
        dataApiMysqlDao.insertMysqlApi(mysqlApiConf);
    }

    public BaseResponse listMysqlApiConfs(Integer pageNum, Integer pageSize) {
        Page<MysqlApiConf> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(dataApiMysqlDao::listMysqlApi);
        return BaseResponse.ok(page);
    }

    public List<MysqlApiConf> listMysqlApiConfs() {
        return dataApiMysqlDao.listMysqlApi();
    }

    public MysqlApiConf getMysqlApiConfById(Long id) {
        return dataApiMysqlDao.getMysqlApiById(id);
    }

    public void updateMysqlApiConfStatus(Long id, Integer status) {
        dataApiMysqlDao.updateMysqlApiStatus(id, status);
    }

    public Long countMysqlApiConfBySourceId(Long sourceId) {
        return dataApiMysqlDao.countMysqlApiBySourceId(sourceId);
    }

    /**
     * Solr api operation
     */
    public void insertSolrApiConf(SolrApiConf solrApiConf) {
        dataApiSolrDao.insertSolrApi(solrApiConf);
    }

    public BaseResponse listSolrApiConfs(Integer pageNum, Integer pageSize) {
        Page<SolrApiConf> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(dataApiSolrDao::listSolrApi);
        return BaseResponse.ok(page);
    }

    public List<SolrApiConf> listSolrApiConfs() {
        return dataApiSolrDao.listSolrApi();
    }

    public SolrApiConf getSolrApiConfById(Long id) {
        return dataApiSolrDao.getSolrApiById(id);
    }

    public void updateSolrApiConfStatus(Long id, Integer status) {
        dataApiSolrDao.updateSolrApiStatus(id, status);
    }

    public Long countSolrApiConfBySourceId(Long sourceId) {
        return dataApiSolrDao.countSolrApiBySourceId(sourceId);
    }

    /**
     * Sql api operation
     */
    public void insertSqlApiConf(SqlApiConf sqlApiConf) {
        dataApiSqlDao.insertSqlApi(sqlApiConf);
        dataApiSqlDao.insertSqlParam(sqlApiConf.getParamList());
    }

    public List<SqlApiConf> listSqlApiConfs() {
        return dataApiSqlDao.listSqlApi();
    }

    public BaseResponse listSqlApiConfs(Integer pageNum, Integer pageSize) {
        Page<SqlApiConf> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(dataApiSqlDao::listSqlApi);
        return BaseResponse.ok(page);
    }

    public SqlApiConf getSqlApiConfById(Long id) {
        return dataApiSqlDao.getSqlApiById(id);
    }

    public BaseResponse listApiParamByApiId(Long apiId) {
        return BaseResponse.ok(dataApiSqlDao.listApiParamByApiId(apiId));
    }

    public void updateSqlApiConfStatus(Long id, Integer status) {
        dataApiSqlDao.updateSqlApiStatus(id, status);
    }

    public Long countSqlApiConfBySourceId(Long sourceId) {
        return dataApiSqlDao.countSqlApiBySourceId(sourceId);
    }

    public void deleteApiParamByApiId(Long apiId) {
        dataApiSqlDao.deleteApiParamByApiId(apiId);
    }
}
