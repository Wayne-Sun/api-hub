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
import com.wayne.apihub.dao.DataSourceSolrDao;
import com.wayne.apihub.dao.DataSourceSqlDao;
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.datasource.conf.HbaseSourceConf;
import com.wayne.apihub.modules.datasource.conf.SolrSourceConf;
import com.wayne.apihub.modules.datasource.conf.SqlSourceConf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.when;

class DataSourceConfServiceTest {

    private DataSourceHbaseDao dataSourceHbaseDao;
    private DataSourceSolrDao dataSourceSolrDao;
    private DataSourceSqlDao dataSourceSqlDao;
    private DataSourceConfService service;

    @BeforeEach
    void setUp() {
        dataSourceHbaseDao = Mockito.mock(DataSourceHbaseDao.class);
        dataSourceSolrDao = Mockito.mock(DataSourceSolrDao.class);
        dataSourceSqlDao = Mockito.mock(DataSourceSqlDao.class);
        service = new DataSourceConfService(dataSourceHbaseDao, dataSourceSolrDao, dataSourceSqlDao);
    }

    // ==================== HBase ====================

    @Test
    void insertHbaseSourceConf_shouldReturnOk() {
        HbaseSourceConf conf = new HbaseSourceConf();
        conf.setName("test-hbase");
        conf.setHbaseSitePath("/path/to/hbase-site.xml");

        BaseResponse result = service.insertHbaseSourceConf(conf);

        verify(dataSourceHbaseDao).insertHbaseSource(conf);
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("SUCCESS");
    }

    @Test
    @SuppressWarnings("unchecked")
    void listHbaseSourceConfs_withPagination_shouldReturnPagedData() {
        int pageNum = 1;
        int pageSize = 10;
        Page<HbaseSourceConf> page = new Page<>(pageNum, pageSize);
        HbaseSourceConf conf = new HbaseSourceConf();
        conf.setId(1L);
        conf.setName("test-hbase");
        page.add(conf);
        page.setTotal(1);

        try (MockedStatic<PageHelper> ps = Mockito.mockStatic(PageHelper.class, invocation -> {
            if ("startPage".equals(invocation.getMethod().getName())
                    && invocation.getArgument(0).equals(pageNum)
                    && invocation.getArgument(1).equals(pageSize)) {
                return page;
            }
            return invocation.callRealMethod();
        })) {
            BaseResponse result = service.listHbaseSourceConfs(pageNum, pageSize);

            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getData()).isNotNull();
            Map<String, Object> data = (Map<String, Object>) result.getData();
            assertThat(data).containsKeys("total", "list", "pageNum", "pageSize");
        }
    }

    @Test
    void listHbaseSourceConfs_noArg_shouldReturnList() {
        HbaseSourceConf conf = new HbaseSourceConf();
        conf.setId(1L);
        conf.setName("test-hbase");
        List<HbaseSourceConf> expectedList = Arrays.asList(conf);
        when(dataSourceHbaseDao.listHbaseSource()).thenReturn(expectedList);

        List<HbaseSourceConf> result = service.listHbaseSourceConfs();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(dataSourceHbaseDao).listHbaseSource();
    }

    @Test
    void getHbaseSourceConfById_shouldReturnConf() {
        HbaseSourceConf expected = new HbaseSourceConf();
        expected.setId(1L);
        expected.setName("test-hbase");
        when(dataSourceHbaseDao.getHbaseSourceById(1L)).thenReturn(expected);

        HbaseSourceConf result = service.getHbaseSourceConfById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("test-hbase");
        verify(dataSourceHbaseDao).getHbaseSourceById(1L);
    }

    @Test
    void updateHbaseSourceConfStatus_shouldCallDao() {
        service.updateHbaseSourceConfStatus(1L, 0);

        verify(dataSourceHbaseDao).updateHbaseSourceStatus(1L, 0);
    }

    // ==================== Solr ====================

    @Test
    void insertSolrSourceConf_shouldReturnOk() {
        SolrSourceConf conf = new SolrSourceConf();
        conf.setName("test-solr");
        conf.setZkHosts("localhost:2181");

        BaseResponse result = service.insertSolrSourceConf(conf);

        verify(dataSourceSolrDao).insertSolrSource(conf);
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("SUCCESS");
    }

    @Test
    @SuppressWarnings("unchecked")
    void listSolrSourceConfs_withPagination_shouldReturnPagedData() {
        int pageNum = 1;
        int pageSize = 10;
        Page<SolrSourceConf> page = new Page<>(pageNum, pageSize);
        SolrSourceConf conf = new SolrSourceConf();
        conf.setId(1L);
        conf.setName("test-solr");
        page.add(conf);
        page.setTotal(1);

        try (MockedStatic<PageHelper> ps = Mockito.mockStatic(PageHelper.class, invocation -> {
            if ("startPage".equals(invocation.getMethod().getName())
                    && invocation.getArgument(0).equals(pageNum)
                    && invocation.getArgument(1).equals(pageSize)) {
                return page;
            }
            return invocation.callRealMethod();
        })) {
            BaseResponse result = service.listSolrSourceConfs(pageNum, pageSize);

            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getData()).isNotNull();
            Map<String, Object> data = (Map<String, Object>) result.getData();
            assertThat(data).containsKeys("total", "list", "pageNum", "pageSize");
        }
    }

    @Test
    void listSolrSourceConfs_noArg_shouldReturnList() {
        SolrSourceConf conf = new SolrSourceConf();
        conf.setId(1L);
        conf.setName("test-solr");
        List<SolrSourceConf> expectedList = Arrays.asList(conf);
        when(dataSourceSolrDao.listSolrSource()).thenReturn(expectedList);

        List<SolrSourceConf> result = service.listSolrSourceConfs();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(dataSourceSolrDao).listSolrSource();
    }

    @Test
    void getSolrSourceConfById_shouldReturnConf() {
        SolrSourceConf expected = new SolrSourceConf();
        expected.setId(1L);
        expected.setName("test-solr");
        when(dataSourceSolrDao.getSolrSourceById(1L)).thenReturn(expected);

        SolrSourceConf result = service.getSolrSourceConfById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("test-solr");
        verify(dataSourceSolrDao).getSolrSourceById(1L);
    }

    @Test
    void updateSolrSourceConfStatus_shouldCallDao() {
        service.updateSolrSourceConfStatus(1L, 0);

        verify(dataSourceSolrDao).updateSolrSourceStatus(1L, 0);
    }

    // ==================== SQL ====================

    @Test
    void insertSqlSourceConf_shouldReturnOk() {
        SqlSourceConf conf = new SqlSourceConf();
        conf.setName("test-sql");
        conf.setUrl("jdbc:mysql://localhost:3306/test");
        conf.setUsername("root");
        conf.setPassword("password");

        BaseResponse result = service.insertSqlSourceConf(conf);

        verify(dataSourceSqlDao).insertSqlSource(conf);
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("SUCCESS");
    }

    @Test
    @SuppressWarnings("unchecked")
    void listSqlSourceConfs_withPagination_shouldReturnPagedData() {
        int pageNum = 1;
        int pageSize = 10;
        Page<SqlSourceConf> page = new Page<>(pageNum, pageSize);
        SqlSourceConf conf = new SqlSourceConf();
        conf.setId(1L);
        conf.setName("test-sql");
        page.add(conf);
        page.setTotal(1);

        try (MockedStatic<PageHelper> ps = Mockito.mockStatic(PageHelper.class, invocation -> {
            if ("startPage".equals(invocation.getMethod().getName())
                    && invocation.getArgument(0).equals(pageNum)
                    && invocation.getArgument(1).equals(pageSize)) {
                return page;
            }
            return invocation.callRealMethod();
        })) {
            BaseResponse result = service.listSqlSourceConfs(pageNum, pageSize);

            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getData()).isNotNull();
            Map<String, Object> data = (Map<String, Object>) result.getData();
            assertThat(data).containsKeys("total", "list", "pageNum", "pageSize");
        }
    }

    @Test
    void listSqlSourceConfs_noArg_shouldReturnList() {
        SqlSourceConf conf = new SqlSourceConf();
        conf.setId(1L);
        conf.setName("test-sql");
        List<SqlSourceConf> expectedList = Arrays.asList(conf);
        when(dataSourceSqlDao.listSqlSource()).thenReturn(expectedList);

        List<SqlSourceConf> result = service.listSqlSourceConfs();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(dataSourceSqlDao).listSqlSource();
    }

    @Test
    void getSqlSourceConfById_shouldReturnConf() {
        SqlSourceConf expected = new SqlSourceConf();
        expected.setId(1L);
        expected.setName("test-sql");
        when(dataSourceSqlDao.getSqlSourceById(1L)).thenReturn(expected);

        SqlSourceConf result = service.getSqlSourceConfById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("test-sql");
        verify(dataSourceSqlDao).getSqlSourceById(1L);
    }

    @Test
    void updateSqlSourceConfStatus_shouldCallDao() {
        service.updateSqlSourceConfStatus(1L, 0);

        verify(dataSourceSqlDao).updateSqlSourceStatus(1L, 0);
    }
}
