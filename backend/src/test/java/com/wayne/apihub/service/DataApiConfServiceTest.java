package com.wayne.apihub.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wayne.apihub.dao.DataApiHbaseDao;
import com.wayne.apihub.dao.DataApiSolrDao;
import com.wayne.apihub.dao.DataApiSqlDao;
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.dataapi.conf.HbaseApiConf;
import com.wayne.apihub.modules.dataapi.conf.SolrApiConf;
import com.wayne.apihub.modules.dataapi.conf.SqlApiConf;
import com.wayne.apihub.modules.query.sql.SqlParam;
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

class DataApiConfServiceTest {

    private DataApiHbaseDao dataApiHbaseDao;
    private DataApiSolrDao dataApiSolrDao;
    private DataApiSqlDao dataApiSqlDao;
    private DataApiConfService service;

    @BeforeEach
    void setUp() {
        dataApiHbaseDao = Mockito.mock(DataApiHbaseDao.class);
        dataApiSolrDao = Mockito.mock(DataApiSolrDao.class);
        dataApiSqlDao = Mockito.mock(DataApiSqlDao.class);
        service = new DataApiConfService(dataApiHbaseDao, dataApiSolrDao, dataApiSqlDao);
    }

    // ==================== HBase API ====================

    @Test
    void insertHbaseApiConf_shouldCallDao() {
        HbaseApiConf conf = new HbaseApiConf();
        conf.setName("test-hbase-api");
        conf.setDataSourceId(1L);

        service.insertHbaseApiConf(conf);

        verify(dataApiHbaseDao).insertHbaseApi(conf);
    }

    @Test
    @SuppressWarnings("unchecked")
    void listHbaseApiConfs_withPagination_shouldReturnPagedData() {
        int pageNum = 1;
        int pageSize = 10;
        Page<HbaseApiConf> page = new Page<>(pageNum, pageSize);
        HbaseApiConf conf = new HbaseApiConf();
        conf.setId(1L);
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
            BaseResponse result = service.listHbaseApiConfs(pageNum, pageSize);

            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getData()).isNotNull();
            Map<String, Object> data = (Map<String, Object>) result.getData();
            assertThat(data).containsKeys("total", "list", "pageNum", "pageSize");
        }
    }

    @Test
    void listHbaseApiConfs_noArg_shouldReturnList() {
        HbaseApiConf conf = new HbaseApiConf();
        conf.setId(1L);
        conf.setName("test-hbase-api");
        List<HbaseApiConf> expected = Arrays.asList(conf);
        when(dataApiHbaseDao.listHbaseApi()).thenReturn(expected);

        List<HbaseApiConf> result = service.listHbaseApiConfs();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(dataApiHbaseDao).listHbaseApi();
    }

    @Test
    void getHbaseApiConfById_shouldReturnConf() {
        HbaseApiConf expected = new HbaseApiConf();
        expected.setId(1L);
        expected.setName("test-hbase-api");
        when(dataApiHbaseDao.getHbaseApiById(1L)).thenReturn(expected);

        HbaseApiConf result = service.getHbaseApiConfById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("test-hbase-api");
        verify(dataApiHbaseDao).getHbaseApiById(1L);
    }

    @Test
    void updateHbaseApiConfStatus_shouldCallDao() {
        service.updateHbaseApiConfStatus(1L, 0);

        verify(dataApiHbaseDao).updateHbaseApiStatus(1L, 0);
    }

    @Test
    void countHbaseApiConfBySourceId_shouldReturnCount() {
        when(dataApiHbaseDao.countHbaseApiBySourceId(1L)).thenReturn(5L);

        Long result = service.countHbaseApiConfBySourceId(1L);

        assertThat(result).isEqualTo(5L);
        verify(dataApiHbaseDao).countHbaseApiBySourceId(1L);
    }

    // ==================== Solr API ====================

    @Test
    void insertSolrApiConf_shouldCallDao() {
        SolrApiConf conf = new SolrApiConf();
        conf.setName("test-solr-api");
        conf.setDataSourceId(1L);

        service.insertSolrApiConf(conf);

        verify(dataApiSolrDao).insertSolrApi(conf);
    }

    @Test
    @SuppressWarnings("unchecked")
    void listSolrApiConfs_withPagination_shouldReturnPagedData() {
        int pageNum = 1;
        int pageSize = 10;
        Page<SolrApiConf> page = new Page<>(pageNum, pageSize);
        SolrApiConf conf = new SolrApiConf();
        conf.setId(1L);
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
            BaseResponse result = service.listSolrApiConfs(pageNum, pageSize);

            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getData()).isNotNull();
            Map<String, Object> data = (Map<String, Object>) result.getData();
            assertThat(data).containsKeys("total", "list", "pageNum", "pageSize");
        }
    }

    @Test
    void listSolrApiConfs_noArg_shouldReturnList() {
        SolrApiConf conf = new SolrApiConf();
        conf.setId(1L);
        conf.setName("test-solr-api");
        List<SolrApiConf> expected = Arrays.asList(conf);
        when(dataApiSolrDao.listSolrApi()).thenReturn(expected);

        List<SolrApiConf> result = service.listSolrApiConfs();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(dataApiSolrDao).listSolrApi();
    }

    @Test
    void getSolrApiConfById_shouldReturnConf() {
        SolrApiConf expected = new SolrApiConf();
        expected.setId(1L);
        expected.setName("test-solr-api");
        when(dataApiSolrDao.getSolrApiById(1L)).thenReturn(expected);

        SolrApiConf result = service.getSolrApiConfById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("test-solr-api");
        verify(dataApiSolrDao).getSolrApiById(1L);
    }

    @Test
    void updateSolrApiConfStatus_shouldCallDao() {
        service.updateSolrApiConfStatus(1L, 0);

        verify(dataApiSolrDao).updateSolrApiStatus(1L, 0);
    }

    @Test
    void countSolrApiConfBySourceId_shouldReturnCount() {
        when(dataApiSolrDao.countSolrApiBySourceId(1L)).thenReturn(5L);

        Long result = service.countSolrApiConfBySourceId(1L);

        assertThat(result).isEqualTo(5L);
        verify(dataApiSolrDao).countSolrApiBySourceId(1L);
    }

    // ==================== SQL API ====================

    @Test
    void insertSqlApiConf_shouldCallDao() {
        SqlApiConf conf = new SqlApiConf();
        conf.setName("test-sql-api");
        conf.setDataSourceId(1L);
        SqlParam param = new SqlParam();
        param.setName("id");
        conf.setParamList(Arrays.asList(param));

        service.insertSqlApiConf(conf);

        verify(dataApiSqlDao).insertSqlApi(conf);
        verify(dataApiSqlDao).insertSqlParam(conf.getParamList());
    }

    @Test
    @SuppressWarnings("unchecked")
    void listSqlApiConfs_withPagination_shouldReturnPagedData() {
        int pageNum = 1;
        int pageSize = 10;
        Page<SqlApiConf> page = new Page<>(pageNum, pageSize);
        SqlApiConf conf = new SqlApiConf();
        conf.setId(1L);
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
            BaseResponse result = service.listSqlApiConfs(pageNum, pageSize);

            assertThat(result.getCode()).isEqualTo(200);
            assertThat(result.getData()).isNotNull();
            Map<String, Object> data = (Map<String, Object>) result.getData();
            assertThat(data).containsKeys("total", "list", "pageNum", "pageSize");
        }
    }

    @Test
    void listSqlApiConfs_noArg_shouldReturnList() {
        SqlApiConf conf = new SqlApiConf();
        conf.setId(1L);
        conf.setName("test-sql-api");
        List<SqlApiConf> expected = Arrays.asList(conf);
        when(dataApiSqlDao.listSqlApi()).thenReturn(expected);

        List<SqlApiConf> result = service.listSqlApiConfs();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(dataApiSqlDao).listSqlApi();
    }

    @Test
    void getSqlApiConfById_shouldReturnConf() {
        SqlApiConf expected = new SqlApiConf();
        expected.setId(1L);
        expected.setName("test-sql-api");
        when(dataApiSqlDao.getSqlApiById(1L)).thenReturn(expected);

        SqlApiConf result = service.getSqlApiConfById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("test-sql-api");
        verify(dataApiSqlDao).getSqlApiById(1L);
    }

    @Test
    void listApiParamByApiId_shouldReturnParams() {
        SqlParam param = new SqlParam();
        param.setName("id");
        param.setType("Long");
        List<SqlParam> expected = Arrays.asList(param);
        when(dataApiSqlDao.listApiParamByApiId(1L)).thenReturn(expected);

        BaseResponse result = service.listApiParamByApiId(1L);

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData()).isNotNull();
        verify(dataApiSqlDao).listApiParamByApiId(1L);
    }

    @Test
    void updateSqlApiConfStatus_shouldCallDao() {
        service.updateSqlApiConfStatus(1L, 0);

        verify(dataApiSqlDao).updateSqlApiStatus(1L, 0);
    }

    @Test
    void countSqlApiConfBySourceId_shouldReturnCount() {
        when(dataApiSqlDao.countSqlApiBySourceId(1L)).thenReturn(5L);

        Long result = service.countSqlApiConfBySourceId(1L);

        assertThat(result).isEqualTo(5L);
        verify(dataApiSqlDao).countSqlApiBySourceId(1L);
    }

    @Test
    void deleteApiParamByApiId_shouldCallDao() {
        service.deleteApiParamByApiId(1L);

        verify(dataApiSqlDao).deleteApiParamByApiId(1L);
    }
}
