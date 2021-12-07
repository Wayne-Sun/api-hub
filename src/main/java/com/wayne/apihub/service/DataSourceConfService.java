/**
 * Copyright 2021 Wayne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
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
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.datasource.info.HbaseSourceConf;
import com.wayne.apihub.modules.datasource.info.MysqlSourceConf;
import com.wayne.apihub.modules.datasource.info.SolrSourceConf;
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

    @Autowired
    public DataSourceConfService(DataSourceHbaseDao dataSourceHbaseDao, DataSourceMysqlDao dataSourceMysqlDao, DataSourceSolrDao dataSourceSolrDao) {
        this.dataSourceHbaseDao = dataSourceHbaseDao;
        this.dataSourceMysqlDao = dataSourceMysqlDao;
        this.dataSourceSolrDao = dataSourceSolrDao;
    }

    public BaseResponse insertHbaseSourceConf(HbaseSourceConf hbaseSourceConf) {
        dataSourceHbaseDao.insertHbaseSource(hbaseSourceConf);
        return BaseResponse.ok();
    }

    public BaseResponse insertMysqlSourceConf(MysqlSourceConf mysqlSourceConf) {
        dataSourceMysqlDao.insertMysqlSource(mysqlSourceConf);
        return BaseResponse.ok();
    }

    public BaseResponse insertSolrSourceConf(SolrSourceConf solrSourceConf) {
        dataSourceSolrDao.insertSolrSource(solrSourceConf);
        return BaseResponse.ok();
    }

    public BaseResponse listHbaseSourceConfs(Integer pageNum, Integer pageSize) {
        Page<HbaseSourceConf> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(dataSourceHbaseDao::listHbaseSource);
        return BaseResponse.ok(page);
    }

    public List<HbaseSourceConf> listHbaseSourceConfs() {
        return dataSourceHbaseDao.listHbaseSource();
    }

    public BaseResponse listMysqlSourceConfs(Integer pageNum, Integer pageSize) {
        Page<MysqlSourceConf> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(dataSourceMysqlDao::listMysqlSource);
        return BaseResponse.ok(page);
    }

    public List<MysqlSourceConf> listMysqlSourceConfs() {
        return dataSourceMysqlDao.listMysqlSource();
    }

    public BaseResponse listSolrSourceConfs(Integer pageNum, Integer pageSize) {
        Page<SolrSourceConf> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(dataSourceSolrDao::listSolrSource);
        return BaseResponse.ok(page);
    }

    public List<SolrSourceConf> listSolrSourceConfs() {
        return dataSourceSolrDao.listSolrSource();
    }

    public HbaseSourceConf getHbaseSourceConfById(Long id) {
        return dataSourceHbaseDao.getHbaseSourceById(id);
    }

    public MysqlSourceConf getMysqlSourceConfById(Long id) {
        return dataSourceMysqlDao.getMysqlSourceById(id);
    }

    public SolrSourceConf getSolrSourceConfById(Long id) {
        return dataSourceSolrDao.getSolrSourceById(id);
    }

    public void updateHbaseSourceConfStatus(Long id, Integer status) {
        dataSourceHbaseDao.updateHbaseSourceStatus(id, status);
    }

    public void updateMysqlSourceConfStatus(Long id, Integer status) {
        dataSourceMysqlDao.updateMysqlSourceStatus(id, status);
    }

    public void updateSolrSourceConfStatus(Long id, Integer status) {
        dataSourceSolrDao.updateSolrSourceStatus(id, status);
    }
}
