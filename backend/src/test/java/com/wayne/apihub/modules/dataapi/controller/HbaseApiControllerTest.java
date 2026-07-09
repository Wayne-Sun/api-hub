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
package com.wayne.apihub.modules.dataapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayne.apihub.model.BasePageRequest;
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.dataapi.conf.HbaseApiConf;
import com.wayne.apihub.modules.dataapi.exception.DataApiException;
import com.wayne.apihub.modules.dataapi.factory.HbaseApiHandlerFactory;
import com.wayne.apihub.service.DataApiConfService;
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
class HbaseApiControllerTest {

    @Mock
    private DataApiConfService dataApiConfService;

    @Mock
    private HbaseApiHandlerFactory hbaseApiHandlerFactory;

    @InjectMocks
    private HbaseApiController hbaseApiController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(hbaseApiController).build();
    }

    @Test
    void registerApi_success_shouldReturn200() throws Exception {
        HbaseApiConf apiConf = new HbaseApiConf();
        apiConf.setName("test-hbase-api");
        apiConf.setTableName("test_table");
        apiConf.setColumns("col1,col2");
        apiConf.setType(1);

        doNothing().when(hbaseApiHandlerFactory).registerApi(any(HbaseApiConf.class));

        mockMvc.perform(post("/v1/api/hbase/registerApi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(apiConf)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void registerApi_exception_shouldReturn500() throws Exception {
        HbaseApiConf apiConf = new HbaseApiConf();
        apiConf.setName("test-hbase-api");

        doThrow(new RuntimeException("Register failed"))
                .when(hbaseApiHandlerFactory).registerApi(any(HbaseApiConf.class));

        mockMvc.perform(post("/v1/api/hbase/registerApi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(apiConf)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void listApi_success_shouldReturn200() throws Exception {
        BasePageRequest pageRequest = new BasePageRequest(1, 10);

        when(dataApiConfService.listHbaseApiConfs(1, 10))
                .thenReturn(BaseResponse.ok("mock data"));

        mockMvc.perform(post("/v1/api/hbase/listApi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void enableApi_success_shouldReturn200() throws Exception {
        doNothing().when(hbaseApiHandlerFactory).enableApi(anyLong());

        mockMvc.perform(get("/v1/api/hbase/enableApi")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void enableApi_DataApiException_shouldReturn400() throws Exception {
        doThrow(DataApiException.alreadyEnabled())
                .when(hbaseApiHandlerFactory).enableApi(anyLong());

        mockMvc.perform(get("/v1/api/hbase/enableApi")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void enableApi_genericException_shouldReturn500() throws Exception {
        doThrow(new RuntimeException("Unexpected error"))
                .when(hbaseApiHandlerFactory).enableApi(anyLong());

        mockMvc.perform(get("/v1/api/hbase/enableApi")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void disableApi_success_shouldReturn200() throws Exception {
        doNothing().when(hbaseApiHandlerFactory).disableApi(anyLong());

        mockMvc.perform(get("/v1/api/hbase/disableApi")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void disableApi_DataApiException_shouldReturn400() throws Exception {
        doThrow(DataApiException.alreadyDisabled())
                .when(hbaseApiHandlerFactory).disableApi(anyLong());

        mockMvc.perform(get("/v1/api/hbase/disableApi")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }
}
