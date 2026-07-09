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
package com.wayne.apihub.modules.datasource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayne.apihub.model.BasePageRequest;
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.datasource.conf.HbaseSourceConf;
import com.wayne.apihub.modules.datasource.conf.SolrSourceConf;
import com.wayne.apihub.modules.datasource.exception.DataSourceException;
import com.wayne.apihub.modules.datasource.factory.SolrClientFactory;
import com.wayne.apihub.service.DataSourceConfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SolrSourceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DataSourceConfService dataSourceConfService;

    @Mock
    private SolrClientFactory solrClientFactory;

    @InjectMocks
    private SolrSourceController controller;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void insertSource_success_returns200() throws Exception {
        SolrSourceConf conf = new SolrSourceConf();
        conf.setName("test-solr");
        conf.setZkHosts("zk1:2181,zk2:2181");
        conf.setZkChroot("/solr");

        when(dataSourceConfService.insertSolrSourceConf(any())).thenReturn(BaseResponse.ok());

        mockMvc.perform(post("/v1/source/solr/insertSource")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conf)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void insertSource_serviceThrowsException_returns500() throws Exception {
        SolrSourceConf conf = new SolrSourceConf();
        conf.setName("test-solr");

        when(dataSourceConfService.insertSolrSourceConf(any())).thenThrow(new RuntimeException("Solr connection failed"));

        mockMvc.perform(post("/v1/source/solr/insertSource")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conf)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("ERROR"))
                .andExpect(jsonPath("$.data").value("Solr connection failed"));
    }

    @Test
    void listSource_success_returns200() throws Exception {
        BasePageRequest pageRequest = new BasePageRequest(1, 10);

        when(dataSourceConfService.listSolrSourceConfs(anyInt(), anyInt())).thenReturn(BaseResponse.ok("solr list"));

        mockMvc.perform(post("/v1/source/solr/listSource")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value("solr list"));
    }

    @Test
    void initSource_success_returns200() throws Exception {
        // NOTE: Known copy-paste bug — SolrSourceController.initSource() parameter is typed as HbaseSourceConf
        HbaseSourceConf conf = new HbaseSourceConf();
        conf.setName("test-init");
        conf.setHbaseSitePath("/path/to/hbase-site.xml");

        doNothing().when(solrClientFactory).initClient(any());

        mockMvc.perform(post("/v1/source/solr/initSource")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conf)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void enableSource_success_returns200() throws Exception {
        doNothing().when(solrClientFactory).enableClient(anyLong());

        mockMvc.perform(get("/v1/source/solr/enableSource")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void enableSource_dataSourceException_returns400() throws Exception {
        doThrow(DataSourceException.alreadyEnabled()).when(solrClientFactory).enableClient(anyLong());

        mockMvc.perform(get("/v1/source/solr/enableSource")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Wrong Parameter"))
                .andExpect(jsonPath("$.data").value("Data source already enabled"));
    }

    @Test
    void disableSource_success_returns200() throws Exception {
        doNothing().when(solrClientFactory).disableClient(anyLong());

        mockMvc.perform(get("/v1/source/solr/disableSource")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void disableSource_dataSourceException_returns400() throws Exception {
        doThrow(DataSourceException.alreadyDisabled()).when(solrClientFactory).disableClient(anyLong());

        mockMvc.perform(get("/v1/source/solr/disableSource")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Wrong Parameter"))
                .andExpect(jsonPath("$.data").value("Data source already disabled"));
    }
}
