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
import com.wayne.apihub.dao.DataSourceHbaseDao;
import com.wayne.apihub.dao.DataSourceMysqlDao;
import com.wayne.apihub.dao.DataSourceSolrDao;
import com.wayne.apihub.dao.DataSourceSqlDao;
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.datasource.conf.HbaseSourceConf;
import com.wayne.apihub.modules.datasource.conf.MysqlSourceConf;
import com.wayne.apihub.modules.datasource.conf.SolrSourceConf;
import com.wayne.apihub.modules.datasource.conf.SqlSourceConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Wayne
 */
@Service
public class DataSourceConfService {
    private final DataSourceHbaseDao dataSourceHbaseDao;
    private final DataSourceMysqlDao dataSourceMysqlDao;
    private final DataSourceSolrDao dataSourceSolrDao;
    private final DataSourceSqlDao dataSourceSqlDao;

    @Autowired
    public DataSourceConfService(DataSourceHbaseDao dataSourceHbaseDao, DataSourceMysqlDao dataSourceMysqlDao,
                                 DataSourceSolrDao dataSourceSolrDao, DataSourceSqlDao dataSourceSqlDao) {
        this.dataSourceHbaseDao = dataSourceHbaseDao;
        this.dataSourceMysqlDao = dataSourceMysqlDao;
        this.dataSourceSolrDao = dataSourceSolrDao;
        this.dataSourceSqlDao = dataSourceSqlDao;
    }

    /**
     * Hbase source operation
     */
    public BaseResponse insertHbaseSourceConf(HbaseSourceConf hbaseSourceConf) {
        dataSourceHbaseDao.insertHbaseSource(hbaseSourceConf);
        return BaseResponse.ok();
    }

    public BaseResponse listHbaseSourceConfs(Integer pageNum, Integer pageSize) {
        Page<HbaseSourceConf> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(dataSourceHbaseDao::listHbaseSource);
        return BaseResponse.ok(page);
    }

    public List<HbaseSourceConf> listHbaseSourceConfs() {
        return dataSourceHbaseDao.listHbaseSource();
    }

    public HbaseSourceConf getHbaseSourceConfById(Long id) {
        return dataSourceHbaseDao.getHbaseSourceById(id);
    }

    public void updateHbaseSourceConfStatus(Long id, Integer status) {
        dataSourceHbaseDao.updateHbaseSourceStatus(id, status);
    }

    /**
     * Mysql source operation
     */
    public BaseResponse insertMysqlSourceConf(MysqlSourceConf mysqlSourceConf) {
        dataSourceMysqlDao.insertMysqlSource(mysqlSourceConf);
        return BaseResponse.ok();
    }

    public BaseResponse listMysqlSourceConfs(Integer pageNum, Integer pageSize) {
        Page<MysqlSourceConf> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(dataSourceMysqlDao::listMysqlSource);
        return BaseResponse.ok(page);
    }

    public List<MysqlSourceConf> listMysqlSourceConfs() {
        return dataSourceMysqlDao.listMysqlSource();
    }

    public MysqlSourceConf getMysqlSourceConfById(Long id) {
        return dataSourceMysqlDao.getMysqlSourceById(id);
    }

    public void updateMysqlSourceConfStatus(Long id, Integer status) {
        dataSourceMysqlDao.updateMysqlSourceStatus(id, status);
    }

    /**
     * Solr api operation
     */
    public BaseResponse insertSolrSourceConf(SolrSourceConf solrSourceConf) {
        dataSourceSolrDao.insertSolrSource(solrSourceConf);
        return BaseResponse.ok();
    }

    public BaseResponse listSolrSourceConfs(Integer pageNum, Integer pageSize) {
        Page<SolrSourceConf> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(dataSourceSolrDao::listSolrSource);
        return BaseResponse.ok(page);
    }

    public List<SolrSourceConf> listSolrSourceConfs() {
        return dataSourceSolrDao.listSolrSource();
    }

    public SolrSourceConf getSolrSourceConfById(Long id) {
        return dataSourceSolrDao.getSolrSourceById(id);
    }

    public void updateSolrSourceConfStatus(Long id, Integer status) {
        dataSourceSolrDao.updateSolrSourceStatus(id, status);
    }

    /**
     * Sql source operation
     */
    public BaseResponse insertSqlSourceConf(SqlSourceConf sqlSourceConf) {
        dataSourceSqlDao.insertSqlSource(sqlSourceConf);
        return BaseResponse.ok();
    }

    public BaseResponse listSqlSourceConfs(Integer pageNum, Integer pageSize) {
        Page<SqlSourceConf> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(dataSourceSqlDao::listSqlSource);
        return BaseResponse.ok(page);
    }

    public List<SqlSourceConf> listSqlSourceConfs() {
        return dataSourceSqlDao.listSqlSource();
    }

    public SqlSourceConf getSqlSourceConfById(Long id) {
        return dataSourceSqlDao.getSqlSourceById(id);
    }

    public void updateSqlSourceConfStatus(Long id, Integer status) {
        dataSourceSqlDao.updateSqlSourceStatus(id, status);
    }
}
